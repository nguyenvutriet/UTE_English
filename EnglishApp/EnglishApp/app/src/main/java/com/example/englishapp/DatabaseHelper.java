package com.example.englishapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EnglishApp.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_HISTORY = "test_history";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TEST_ID = "test_id";
    public static final String COLUMN_TEST_TITLE = "test_title";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_TOTAL_QUESTIONS = "total_questions";
    public static final String COLUMN_DATE_TAKEN = "date_taken";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_HISTORY + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TEST_ID + " INTEGER, " +
                    COLUMN_TEST_TITLE + " TEXT, " +
                    COLUMN_SCORE + " INTEGER, " +
                    COLUMN_TOTAL_QUESTIONS + " INTEGER, " +
                    COLUMN_DATE_TAKEN + " TEXT" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    // --- Insert a test result ---
    public long insertTestResult(int testId, String testTitle, int score, int totalQuestions, String dateTaken) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEST_ID, testId);
        values.put(COLUMN_TEST_TITLE, testTitle);
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_TOTAL_QUESTIONS, totalQuestions);
        values.put(COLUMN_DATE_TAKEN, dateTaken);

        long id = db.insert(TABLE_HISTORY, null, values);
        db.close();
        return id;
    }

    // --- Get all history records ---
    public List<TestHistoryRecord> getAllHistory() {
        List<TestHistoryRecord> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + COLUMN_ID + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                int testId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TEST_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEST_TITLE));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
                int totalQuestions = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_QUESTIONS));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_TAKEN));

                records.add(new TestHistoryRecord(id, testId, title, score, totalQuestions, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }

    // --- History Record Data Class ---
    public static class TestHistoryRecord {
        private int id;
        private int testId;
        private String testTitle;
        private int score;
        private int totalQuestions;
        private String dateTaken;

        public TestHistoryRecord(int id, int testId, String testTitle, int score, int totalQuestions, String dateTaken) {
            this.id = id;
            this.testId = testId;
            this.testTitle = testTitle;
            this.score = score;
            this.totalQuestions = totalQuestions;
            this.dateTaken = dateTaken;
        }

        public int getId() { return id; }
        public int getTestId() { return testId; }
        public String getTestTitle() { return testTitle; }
        public int getScore() { return score; }
        public int getTotalQuestions() { return totalQuestions; }
        public String getDateTaken() { return dateTaken; }
    }
}
