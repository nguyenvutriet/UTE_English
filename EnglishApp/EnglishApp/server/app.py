from flask import Flask, request, jsonify
import os
import threading
import sqlite3
import time
import subprocess
import sys
from vosk import Model, KaldiRecognizer
import wave
import json
import shutil

app = Flask(__name__)

# path to sqlite DB file used to store transcripts
DB_PATH = os.path.join(os.path.dirname(__file__), 'transcripts.db')

# Base model folder; if it contains a single subfolder (e.g. "vosk-model-small-en-us-0.15"), use that
BASE_MODEL_DIR = os.path.join(os.path.dirname(__file__), 'model')
MODEL_PATH = BASE_MODEL_DIR
# If model dir contains exactly one directory, use that as model path
if os.path.isdir(BASE_MODEL_DIR):
    entries = [e for e in os.listdir(BASE_MODEL_DIR) if os.path.isdir(os.path.join(BASE_MODEL_DIR, e))]
    if len(entries) == 1:
        MODEL_PATH = os.path.join(BASE_MODEL_DIR, entries[0])

# Allow configuring ffmpeg location via env var FFMPEG_PATH (binary or directory)
FFMPEG_PATH_ENV = os.environ.get('FFMPEG_PATH')


def _find_ffmpeg():
    # return (ffmpeg_binary_fullpath, ffmpeg_dir) or (None, None)
    ff = shutil.which('ffmpeg')
    if ff:
        return ff, os.path.dirname(ff)
    if FFMPEG_PATH_ENV:
        # if env points to a file
        if os.path.isfile(FFMPEG_PATH_ENV):
            return FFMPEG_PATH_ENV, os.path.dirname(FFMPEG_PATH_ENV)
        # if env points to a dir
        if os.path.isdir(FFMPEG_PATH_ENV):
            candidate = os.path.join(FFMPEG_PATH_ENV, 'ffmpeg.exe' if os.name == 'nt' else 'ffmpeg')
            if os.path.isfile(candidate):
                return candidate, FFMPEG_PATH_ENV
    return None, None

FFMPEG_BIN, FFMPEG_DIR = _find_ffmpeg()

# Resolve yt-dlp command
def _resolve_ytdlp_base_cmd():
    direct = shutil.which('yt-dlp')
    if direct:
        return [direct]
    return [sys.executable, '-m', 'yt_dlp']


YT_DLP_BASE_CMD = _resolve_ytdlp_base_cmd()


def _run_command(cmd):
    proc = subprocess.run(cmd, capture_output=True, text=True)
    if proc.stdout:
        print(proc.stdout)
    if proc.stderr:
        print(proc.stderr)
    proc.check_returncode()


# Ensure DB
def init_db():
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    c.execute('''CREATE TABLE IF NOT EXISTS transcripts (
                 id INTEGER PRIMARY KEY AUTOINCREMENT,
                 video_id TEXT,
                 language TEXT,
                 status TEXT,
                 transcript TEXT,
                 segments TEXT,
                 created_at INTEGER
                 )''')
    c.execute('CREATE INDEX IF NOT EXISTS idx_transcripts_video_lang_id ON transcripts(video_id, language, id DESC)')
    conn.commit()
    conn.close()

init_db()

# New helpers: parse SRT (very small parser) and try to download subtitles via yt-dlp

def parse_srt(srt_text):
    """Parse a simple SRT string into list of segments {start, end, text}.
    Times are converted to seconds (float).
    """
    segments = []
    lines = [l.strip() for l in srt_text.splitlines()]
    i = 0
    while i < len(lines):
        # skip empty or numeric index
        if not lines[i]:
            i += 1
            continue
        # possible index line: digits
        if lines[i].isdigit():
            i += 1
            if i >= len(lines):
                break
        # time line
        if i < len(lines) and '-->' in lines[i]:
            time_line = lines[i]
            try:
                start_s, end_s = [t.strip() for t in time_line.split('-->')]
                def to_seconds(ts):
                    # format 00:00:01,500 or 00:00:01.500
                    ts = ts.replace(',', '.')
                    parts = ts.split(':')
                    parts = [float(p) for p in parts]
                    if len(parts) == 3:
                        return parts[0]*3600 + parts[1]*60 + parts[2]
                    return 0.0
                start = to_seconds(start_s)
                end = to_seconds(end_s)
            except Exception:
                start = 0.0; end = 0.0
            i += 1
            text_lines = []
            while i < len(lines) and lines[i]:
                text_lines.append(lines[i])
                i += 1
            text = ' '.join(text_lines).strip()
            segments.append({'start': start, 'end': end, 'text': text})
        else:
            i += 1
    return segments


def try_download_subtitles(video_id, out_dir, cookies=None, cookies_from_browser=None):
    """Try to download subtitles (auto-generated or provided) using yt-dlp.
    cookies: path to cookies file to pass via --cookies
    cookies_from_browser: browser name to pass via --cookies-from-browser (e.g. chrome, edge)
    Returns (transcript_text, segments_json) or (None, None) if not found.
    """
    out_template = os.path.join(out_dir, f"{video_id}.%(ext)s")
    base_cmd = YT_DLP_BASE_CMD + ['--skip-download', '--sub-format', 'srt', '--output', out_template, f'https://www.youtube.com/watch?v={video_id}']
    if cookies_from_browser:
        base_cmd = YT_DLP_BASE_CMD + ['--cookies-from-browser', cookies_from_browser, '--skip-download', '--sub-format', 'srt', '--output', out_template, f'https://www.youtube.com/watch?v={video_id}']
    elif cookies:
        base_cmd = YT_DLP_BASE_CMD + ['--cookies', cookies, '--skip-download', '--sub-format', 'srt', '--output', out_template, f'https://www.youtube.com/watch?v={video_id}']

    cmd = base_cmd.copy()
    cmd.insert(len(YT_DLP_BASE_CMD), '--write-auto-sub')
    try:
        _run_command(cmd)
    except Exception:
        cmd2 = base_cmd.copy()
        cmd2.insert(len(YT_DLP_BASE_CMD), '--write-sub')
        try:
            _run_command(cmd2)
        except Exception:
            return None, None

    # if file exists with .srt
    srt_path = os.path.join(out_dir, f"{video_id}.srt")
    if not os.path.exists(srt_path):
        # sometimes yt-dlp names with language code e.g. video.en.srt — search
        for fn in os.listdir(out_dir):
            if fn.startswith(video_id) and fn.endswith('.srt'):
                srt_path = os.path.join(out_dir, fn)
                break
        else:
            return None, None
    try:
        with open(srt_path, 'r', encoding='utf-8') as f:
            srt_text = f.read()
        segments = parse_srt(srt_text)
        transcript_text = ' '.join([seg.get('text','') for seg in segments])
        segments_json = json.dumps(segments, ensure_ascii=False)
        # cleanup the subtitle file (optional)
        try:
            os.remove(srt_path)
        except Exception:
            pass
        return transcript_text, segments_json
    except Exception:
        return None, None

# Simple worker: download audio via yt-dlp, transcribe via Vosk
def transcribe_worker(video_id, language, row_id, cookies=None, cookies_from_browser=None):
    wav_path = None
    conv_path = None
    try:
        conn = sqlite3.connect(DB_PATH)
        c = conn.cursor()
        c.execute('UPDATE transcripts SET status=? WHERE id=?', ('processing', row_id))
        conn.commit()
        conn.close()

        # first try to download subtitles via yt-dlp (faster and avoids ASR)
        out_dir = os.path.dirname(__file__)
        subs_result = try_download_subtitles(video_id, out_dir, cookies=cookies, cookies_from_browser=cookies_from_browser)
        if subs_result and subs_result[0] is not None:
            transcript_text, segments_json = subs_result
            conn = sqlite3.connect(DB_PATH)
            c = conn.cursor()
            c.execute('UPDATE transcripts SET status=?, transcript=?, segments=? WHERE id=?', ('done', transcript_text, segments_json, row_id))
            conn.commit()
            conn.close()
            return

        wav_path = os.path.join(os.path.dirname(__file__), f"{video_id}.wav")

        # If ffmpeg not available, fail early with helpful message
        if not FFMPEG_BIN:
            # update DB row to error with helpful message
            conn = sqlite3.connect(DB_PATH)
            c = conn.cursor()
            c.execute('UPDATE transcripts SET status=? WHERE id=?', ('error', row_id))
            conn.commit()
            conn.close()
            print('Transcription error: ffmpeg/ffprobe not found. Please install ffmpeg and ensure it is in PATH or set FFMPEG_PATH env var to the ffmpeg binary or its folder.')
            return

        # Download audio using yt-dlp; pass --ffmpeg-location so yt-dlp can find ffmpeg
        cmd = YT_DLP_BASE_CMD + [
            '-f', 'bestaudio',
            '--extract-audio',
            '--audio-format', 'wav',
            '--ffmpeg-location', FFMPEG_DIR if FFMPEG_DIR else os.path.dirname(FFMPEG_BIN),
            '-o', wav_path,
            f'https://www.youtube.com/watch?v={video_id}'
        ]
        _run_command(cmd)

        # Wait file
        if not os.path.exists(wav_path):
            raise RuntimeError('wav not found after yt-dlp')

        # Vosk model
        if not os.path.exists(MODEL_PATH):
            raise RuntimeError('Vosk model not found. Download a model and place it in server/model')

        wf = wave.open(wav_path, 'rb')
        if wf.getnchannels() != 1 or wf.getsampwidth() != 2 or wf.getframerate() not in (8000,16000,32000,44100,48000):
            # convert via ffmpeg to 16k mono
            conv_path = os.path.join(os.path.dirname(__file__), f"{video_id}_16k.wav")
            ffmpeg_exec = FFMPEG_BIN if FFMPEG_BIN else 'ffmpeg'
            conv_cmd = [ffmpeg_exec, '-y', '-i', wav_path, '-ar', '16000', '-ac', '1', conv_path]
            subprocess.check_call(conv_cmd)
            wf.close()
            wf = wave.open(conv_path, 'rb')

        model = Model(MODEL_PATH)
        rec = KaldiRecognizer(model, wf.getframerate())
        rec.SetWords(True)

        segments = []
        texts = []
        # Read audio and accumulate word-level segments
        while True:
            data = wf.readframes(4000)
            if len(data) == 0:
                break
            if rec.AcceptWaveform(data):
                res = rec.Result()
                j = json.loads(res)
                # j may contain {'result': [ { 'word':..., 'start':..., 'end':... }, ... ], 'text': '...'}
                words = j.get('result', [])
                for w in words:
                    word_text = w.get('word', '')
                    if not word_text:
                        continue
                    start = w.get('start')
                    end = w.get('end')
                    segments.append({'word': word_text, 'start': start, 'end': end})
                    texts.append(word_text)
        final = rec.FinalResult()
        j = json.loads(final)
        words = j.get('result', [])
        for w in words:
            word_text = w.get('word', '')
            if not word_text:
                continue
            start = w.get('start')
            end = w.get('end')
            segments.append({'word': word_text, 'start': start, 'end': end})
            texts.append(word_text)

        transcript_text = ' '.join([t for t in texts if t])
        segments_json = json.dumps(segments, ensure_ascii=False)

        # save to DB
        conn = sqlite3.connect(DB_PATH)
        c = conn.cursor()
        c.execute('UPDATE transcripts SET status=?, transcript=?, segments=? WHERE id=?', ('done', transcript_text, segments_json, row_id))
        conn.commit()
        conn.close()

    except Exception as e:
        conn = sqlite3.connect(DB_PATH)
        c = conn.cursor()
        c.execute('UPDATE transcripts SET status=? WHERE id=?', ('error', row_id))
        conn.commit()
        conn.close()
        print('Transcription error:', e)
    finally:
        try:
            if wav_path and os.path.exists(wav_path): os.remove(wav_path)
        except Exception:
            pass
        try:
            if conv_path and os.path.exists(conv_path): os.remove(conv_path)
        except Exception:
            pass

@app.route('/transcribe', methods=['POST'])
def transcribe():
    data = request.json or {}
    video_id = data.get('videoId')
    language = data.get('language','en')
    cookies = data.get('cookies')
    cookies_from_browser = data.get('cookiesFromBrowser')
    if not video_id:
        return jsonify({'error':'videoId required'}), 400

    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    c.execute('SELECT id, status FROM transcripts WHERE video_id=? AND language=? ORDER BY id DESC LIMIT 1', (video_id, language))
    row = c.fetchone()
    if row:
        row_id, status = row
        if status == 'done':
            conn.close()
            return jsonify({'jobId': row_id, 'status': status, 'cached': True}), 200
        if status in ('queued', 'processing'):
            conn.close()
            return jsonify({'jobId': row_id, 'status': status, 'cached': False}), 202
        # status=error hoặc khác -> retry cùng row
        c.execute('UPDATE transcripts SET status=?, transcript=?, segments=?, created_at=? WHERE id=?', ('queued', '', '[]', int(time.time()), row_id))
        conn.commit()
        conn.close()
        threading.Thread(target=transcribe_worker, args=(video_id, language, row_id, cookies, cookies_from_browser), daemon=True).start()
        return jsonify({'jobId': row_id, 'status': 'queued', 'cached': False}), 202

    now = int(time.time())
    c.execute('INSERT INTO transcripts (video_id, language, status, transcript, segments, created_at) VALUES (?, ?, ?, ?, ?, ?)', (video_id, language, 'queued', '', '[]', now))
    row_id = c.lastrowid
    conn.commit()
    conn.close()

    threading.Thread(target=transcribe_worker, args=(video_id, language, row_id, cookies, cookies_from_browser), daemon=True).start()
    return jsonify({'jobId': row_id, 'status': 'queued', 'cached': False}), 202

@app.route('/transcript', methods=['GET'])
def get_transcript():
    video_id = request.args.get('videoId')
    language = request.args.get('language','en')
    if not video_id:
        return jsonify({'error':'videoId required'}), 400
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    c.execute('SELECT id, status, transcript, segments FROM transcripts WHERE video_id=? AND language=? ORDER BY id DESC LIMIT 1', (video_id, language))
    row = c.fetchone()
    conn.close()
    if not row:
        return jsonify({'status':'not_found'}), 404
    row_id, status, transcript, segments = row
    try:
        segs = json.loads(segments) if segments else []
    except Exception:
        segs = []
    return jsonify({'jobId': row_id, 'status': status, 'transcript': transcript, 'segments': segs}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
