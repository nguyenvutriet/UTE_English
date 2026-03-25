# small runner to execute transcribe_worker directly for debugging
from app import transcribe_worker

if __name__ == '__main__':
    video_id = 'Fm0MpfKIs5w'
    language = 'en'
    # row_id 9999 is arbitrary; we won't update DB in this debug run
    # but transcribe_worker expects a numeric row id; create a test row
    import sqlite3, time
    conn=sqlite3.connect('transcripts.db')
    c=conn.cursor()
    now=int(time.time())
    c.execute("INSERT INTO transcripts (video_id, language, status, transcript, segments, created_at) VALUES (?, ?, ?, ?, ?, ?)", (video_id, language, 'queued', '', '[]', now))
    row_id=c.lastrowid
    conn.commit()
    conn.close()
    print('Starting transcribe_worker for', video_id, 'row', row_id)
    transcribe_worker(video_id, language, row_id)
    print('Done')

