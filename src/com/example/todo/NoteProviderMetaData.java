package com.example.todo;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

public class NoteProviderMetaData {
	public static final String AUTHORITY = "com.example.todo.NoteProvider";
	
	// Database information
	public static final String DATABASE_NAME = "note.db";
    public static final int DATABASE_VERSION = 1;
    
    private NoteProviderMetaData() {}
    
    // Metadata of notes table
    public static final class NoteTable implements BaseColumns {
    	private NoteTable() {}
    	
    	public static final String TABLE_NAME = "notes";
    	
        // Uri and mime type definitions
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/notes");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.example.note";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.example.note";
        
        // Columns
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "desciption";
        public static final String CREATED_DATE = "created";
        public static final String MODIFIED_DATE = "modified";
        
        public static final String DEFAULT_SORT_ORDER = MODIFIED_DATE + " DESC";
        
        public static final void create(SQLiteDatabase db) {
        	db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                    + _ID + " INTEGER PRIMARY KEY,"
                    + TITLE + " TEXT,"
                    + DESCRIPTION + " TEXT,"
                    + CREATED_DATE + " INTEGER,"
                    + MODIFIED_DATE + " INTEGER"
                    + ");");
        }
        
        public static final void upgrade(SQLiteDatabase db) {
        	db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        	create(db);
        }
    }
}
