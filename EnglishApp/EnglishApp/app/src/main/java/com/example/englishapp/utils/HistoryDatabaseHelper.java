package com.example.englishapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.englishapp.model.Video;

import java.util.ArrayList;
import java.util.List;

public class HistoryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "youtube_history.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_HISTORY = "history";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_VIDEO_ID = "video_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_THUMBNAIL = "thumbnail";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_WATCHED_DURATION = "watched_duration";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    public boolean isVideoInHistory(String videoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HISTORY, new String[]{COLUMN_ID}, 
                COLUMN_VIDEO_ID + "=?", new String[]{videoId}, 
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
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
}
