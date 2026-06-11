package com.cmkdown.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cmkdown.model.DownloadItem;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "cmkdown.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_DOWNLOADS = "downloads";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_URL = "url";
    private static final String COL_SOURCE = "source";
    private static final String COL_FORMAT = "format";
    private static final String COL_FILE_PATH = "file_path";
    private static final String COL_FILE_SIZE = "file_size";
    private static final String COL_TIMESTAMP = "timestamp";
    private static final String COL_STATUS = "status";
    private static final String COL_THUMBNAIL = "thumbnail";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_DOWNLOADS + " (" +
                COL_ID + " TEXT PRIMARY KEY," +
                COL_TITLE + " TEXT," +
                COL_URL + " TEXT," +
                COL_SOURCE + " TEXT," +
                COL_FORMAT + " TEXT," +
                COL_FILE_PATH + " TEXT," +
                COL_FILE_SIZE + " TEXT," +
                COL_TIMESTAMP + " LONG," +
                COL_STATUS + " INTEGER," +
                COL_THUMBNAIL + " TEXT" +
                ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOADS);
        onCreate(db);
    }

    public long addDownload(DownloadItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID, item.getId());
        values.put(COL_TITLE, item.getTitle());
        values.put(COL_URL, item.getUrl());
        values.put(COL_SOURCE, item.getSource());
        values.put(COL_FORMAT, item.getFormat());
        values.put(COL_FILE_PATH, item.getFilePath());
        values.put(COL_FILE_SIZE, item.getFileSize());
        values.put(COL_TIMESTAMP, item.getTimestamp());
        values.put(COL_STATUS, item.getStatus());
        values.put(COL_THUMBNAIL, item.getThumbnail());
        return db.insert(TABLE_DOWNLOADS, null, values);
    }

    public List<DownloadItem> getAllDownloads() {
        List<DownloadItem> downloads = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DOWNLOADS, null, null, null, null, null,
                COL_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                DownloadItem item = new DownloadItem(
                        cursor.getString(cursor.getColumnIndex(COL_URL)),
                        cursor.getString(cursor.getColumnIndex(COL_SOURCE)),
                        cursor.getString(cursor.getColumnIndex(COL_FORMAT))
                );
                item.setId(cursor.getString(cursor.getColumnIndex(COL_ID)));
                item.setTitle(cursor.getString(cursor.getColumnIndex(COL_TITLE)));
                item.setFilePath(cursor.getString(cursor.getColumnIndex(COL_FILE_PATH)));
                item.setFileSize(cursor.getString(cursor.getColumnIndex(COL_FILE_SIZE)));
                item.setTimestamp(cursor.getLong(cursor.getColumnIndex(COL_TIMESTAMP)));
                item.setStatus(cursor.getInt(cursor.getColumnIndex(COL_STATUS)));
                item.setThumbnail(cursor.getString(cursor.getColumnIndex(COL_THUMBNAIL)));
                downloads.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return downloads;
    }

    public void updateDownloadStatus(String id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STATUS, status);
        db.update(TABLE_DOWNLOADS, values, COL_ID + "=?", new String[]{id});
    }

    public void updateDownloadProgress(String id, String filePath, String fileSize) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FILE_PATH, filePath);
        values.put(COL_FILE_SIZE, fileSize);
        db.update(TABLE_DOWNLOADS, values, COL_ID + "=?", new String[]{id});
    }

    public void deleteDownload(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOWNLOADS, COL_ID + "=?", new String[]{id});
    }
}
