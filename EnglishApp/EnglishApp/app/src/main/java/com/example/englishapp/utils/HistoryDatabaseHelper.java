package com.example.englishapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.englishapp.model.Subtitle;
import com.example.englishapp.model.Video;

import java.util.ArrayList;
import java.util.List;

public class HistoryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "youtube_history.db";
    private static final int DATABASE_VERSION = 3; // bump to add columns for subtitles metadata

    private static final String TABLE_HISTORY = "history";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_VIDEO_ID = "video_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_THUMBNAIL = "thumbnail";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_WATCHED_DURATION = "watched_duration";

    private static final String TABLE_SAVED_SUBTITLES = "saved_subtitles";


    public HistoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_VIDEO_ID + " TEXT UNIQUE,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_THUMBNAIL + " TEXT,"
                + COLUMN_TIMESTAMP + " INTEGER,"
                + COLUMN_WATCHED_DURATION + " REAL" + ")";

        db.execSQL(CREATE_HISTORY_TABLE);

        // ✅ THÊM TABLE saved_subtitles (mở rộng so với trước)
        String CREATE_SAVED_SUBTITLES_TABLE =
                "CREATE TABLE " + TABLE_SAVED_SUBTITLES + " ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "video_id TEXT,"
                        + "text TEXT,"
                        + "start_time REAL,"
                        + "end_time REAL,"
                        + "language TEXT,"
                        + "track_id TEXT,"
                        + "source TEXT,"
                        + "fetched_at INTEGER"
                        + ")";

        db.execSQL(CREATE_SAVED_SUBTITLES_TABLE);
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_saved_subtitles_video_language ON " + TABLE_SAVED_SUBTITLES + "(video_id, language)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Preserve existing saved_subtitles rows where possible; add new columns when upgrading from v2
        try {
            if (oldVersion < 2) {
                // older app - drop and recreate everything
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_SUBTITLES);
                onCreate(db);
                return;
            }

            if (oldVersion == 2) {
                // add new columns to saved_subtitles
                try {
                    db.execSQL("ALTER TABLE " + TABLE_SAVED_SUBTITLES + " ADD COLUMN language TEXT DEFAULT 'en'");
                } catch (Exception ignored) {}
                try {
                    db.execSQL("ALTER TABLE " + TABLE_SAVED_SUBTITLES + " ADD COLUMN track_id TEXT");
                } catch (Exception ignored) {}
                try {
                    db.execSQL("ALTER TABLE " + TABLE_SAVED_SUBTITLES + " ADD COLUMN source TEXT");
                } catch (Exception ignored) {}
                try {
                    db.execSQL("ALTER TABLE " + TABLE_SAVED_SUBTITLES + " ADD COLUMN fetched_at INTEGER");
                } catch (Exception ignored) {}

                try {
                    db.execSQL("CREATE INDEX IF NOT EXISTS idx_saved_subtitles_video_language ON " + TABLE_SAVED_SUBTITLES + "(video_id, language)");
                } catch (Exception ignored) {}
            }

            // If history table schema changed in future versions, you can handle that here
        } catch (Exception e) {
            // Fallback: recreate DB if something unexpected
            try {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED_SUBTITLES);
            } catch (Exception ignored) {}
            onCreate(db);
        }
    }

    public boolean isVideoInHistory(String videoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HISTORY, new String[]{COLUMN_ID},
                COLUMN_VIDEO_ID + "=?", new String[]{videoId},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public void addOrUpdateVideo(Video video, float watchedDuration) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First, check if video exists
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_WATCHED_DURATION + " FROM " + TABLE_HISTORY + " WHERE " + COLUMN_VIDEO_ID + "=?", new String[]{video.getVideoId()});
        if (cursor.moveToFirst()) {
            float oldDuration = cursor.getFloat(0);
            if (watchedDuration < oldDuration) {
                watchedDuration = oldDuration; // keep the longer duration
            }
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_VIDEO_ID, video.getVideoId());
        values.put(COLUMN_TITLE, video.getTitle());
        values.put(COLUMN_THUMBNAIL, video.getThumbnail());
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        values.put(COLUMN_WATCHED_DURATION, watchedDuration);

        db.insertWithOnConflict(TABLE_HISTORY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void updateWatchedDuration(String videoId, float duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WATCHED_DURATION, duration);
        db.update(TABLE_HISTORY, values, COLUMN_VIDEO_ID + "=?", new String[]{videoId});
        db.close();
    }

    public List<Video> getAllHistoryVideos() {
        List<Video> videoList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String videoId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VIDEO_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String thumbnail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_THUMBNAIL));
                float watchedDuration = 0;
                int durationIdx = cursor.getColumnIndex(COLUMN_WATCHED_DURATION);
                if (durationIdx != -1) {
                    watchedDuration = cursor.getFloat(durationIdx);
                }

                Video v = new Video(videoId, title, thumbnail);
                v.setWatchedDuration(watchedDuration);
                videoList.add(v);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return videoList;
    }

    public void saveSubtitle(String videoId, Subtitle sub) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("video_id", videoId);
        values.put("text", sub.getText());
        values.put("start_time", sub.getStartTime());
        values.put("end_time", sub.getEndTime());
        values.put("language", "en");
        values.put("fetched_at", System.currentTimeMillis());
        db.insert("saved_subtitles", null, values);
        db.close();
    }

    // Lấy tất cả subtitle cho video (mọi ngôn ngữ) - tương thích gọi cũ
    public List<Subtitle> getSavedSubtitles(String videoId) {
        List<Subtitle> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM saved_subtitles WHERE video_id=? ORDER BY start_time ASC",
                new String[]{videoId}
        );

        while (cursor.moveToNext()) {
            String text = cursor.getString(cursor.getColumnIndexOrThrow("text"));
            float start = cursor.getFloat(cursor.getColumnIndexOrThrow("start_time"));
            float end = cursor.getFloat(cursor.getColumnIndexOrThrow("end_time"));

            list.add(new Subtitle(text, start, end));
        }

        cursor.close();
        db.close();
        return list;
    }

    // Mới: lấy theo videoId + language
    public List<Subtitle> getSavedSubtitles(String videoId, String language) {
        List<Subtitle> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM saved_subtitles WHERE video_id=? AND language=? ORDER BY start_time ASC",
                new String[]{videoId, language}
        );

        while (cursor.moveToNext()) {
            String text = cursor.getString(cursor.getColumnIndexOrThrow("text"));
            float start = cursor.getFloat(cursor.getColumnIndexOrThrow("start_time"));
            float end = cursor.getFloat(cursor.getColumnIndexOrThrow("end_time"));

            list.add(new Subtitle(text, start, end));
        }

        cursor.close();
        db.close();
        return list;
    }

    // Mới: lưu nhiều subtitles trong 1 transaction. Xoá bản cũ (cùng video+language) trước khi insert.
    public void saveSubtitlesBatch(String videoId, String language, String trackId, String source, List<Subtitle> subs) {
        if (subs == null || subs.isEmpty()) return;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // Xoá bản cũ
            db.delete(TABLE_SAVED_SUBTITLES, "video_id=? AND language= ?", new String[]{videoId, language});

            long now = System.currentTimeMillis();
            for (Subtitle s : subs) {
                ContentValues values = new ContentValues();
                values.put("video_id", videoId);
                values.put("text", s.getText());
                values.put("start_time", s.getStartTime());
                values.put("end_time", s.getEndTime());
                values.put("language", language);
                values.put("track_id", trackId);
                values.put("source", source);
                values.put("fetched_at", now);
                db.insert(TABLE_SAVED_SUBTITLES, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}
