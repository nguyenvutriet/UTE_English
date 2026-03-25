package com.example.englishapp.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface SubtitleDao {

    @Query("SELECT * FROM saved_subtitles WHERE video_id = :videoId AND language = :language ORDER BY start_time ASC")
    List<SubtitleEntity> getByVideoAndLanguage(String videoId, String language);

    @Insert
    void insertAll(List<SubtitleEntity> subs);

    @Query("DELETE FROM saved_subtitles WHERE video_id = :videoId AND language = :language")
    void deleteByVideoAndLanguage(String videoId, String language);

    @Transaction
    default void replaceForVideoAndLanguage(String videoId, String language, List<SubtitleEntity> subs) {
        deleteByVideoAndLanguage(videoId, language);
        insertAll(subs);
    }
}

