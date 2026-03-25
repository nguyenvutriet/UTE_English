Offline transcription server (Vosk) prototype

Overview
--------
This small prototype provides endpoints to transcribe YouTube videos to text using local Vosk models.
It downloads audio via `yt-dlp`, converts to 16 kHz mono with `ffmpeg` (if needed), then runs Vosk to produce text.
Transcripts are stored in a small sqlite database `server/transcripts.db`.

Requirements
------------
- Python 3.8+
- ffmpeg installed and on PATH
- yt-dlp installed (pip install yt-dlp)
- Vosk model downloaded (e.g., `vosk-model-small-en-us-0.15`) and placed into `server/model/` directory

Install Python deps
-------------------
```bash
python -m venv venv
venv\Scripts\activate    # on Windows
pip install -r server/requirements.txt
# If vosk wheel fails, follow Vosk instructions: https://alphacephei.com/vosk/
```

Run server
----------
```bash
python server/app.py
```

Endpoints
---------
- POST /transcribe
  - body JSON: {"videoId":"<youtube id>", "language":"en"}
  - response: {"jobId": <id>, "status":"queued"} (202) or existing job status (200)

- GET /transcript?videoId=<id>&language=en
  - response: {"jobId": <id>, "status":"done|queued|error|not_found", "transcript":"..."}

Notes and limitations
---------------------
- This prototype is intentionally minimal and lacks authentication, robust error handling, retry policies and scaling.
- Vosk models vary in accuracy; for higher accuracy consider using larger models or other ASR engines.
- Downloading audio from YouTube may violate YouTube terms of service if used improperly; ensure you have necessary rights.

Next steps to integrate with Android client
-----------------------------------------
- Android calls GET /transcript; if status=done display; if 404 then call POST /transcribe and poll GET /transcript periodically.
- On completion, save transcript to local Room DB for offline viewing.


