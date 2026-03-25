package com.example.englishapp.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

@Entity(tableName = "saved_subtitles", indices = {@Index(value = {"video_id", "language"})})
public class SubtitleEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String video_id;
    public String text;
    public float start_time;
    public float end_time;
    public String language;
    public String track_id;
    public String source;
    public long fetched_at;

    public SubtitleEntity(String video_id, String text, float start_time, float end_time,
                          String language, String track_id, String source, long fetched_at) {
        this.video_id = video_id;
        this.text = text;
        this.start_time = start_time;
        this.end_time = end_time;
        this.language = language;
        this.track_id = track_id;
        this.source = source;
        this.fetched_at = fetched_at;
    }
}
