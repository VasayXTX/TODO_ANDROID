package com.example.todo;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.example.todo.NoteProviderMetaData.NoteTable;

public class NoteProvider extends ContentProvider {
	// Initialize Notes projcetion map
	private static HashMap<String, String> sNotesProjectionMap = new HashMap<String, String>();
	static {
		sNotesProjectionMap.put(NoteTable._ID, NoteTable._ID);
		sNotesProjectionMap.put(NoteTable.TITLE, NoteTable.TITLE);
		sNotesProjectionMap.put(NoteTable.DESCRIPTION, NoteTable.DESCRIPTION);
		sNotesProjectionMap.put(NoteTable.CREATED_DATE, NoteTable.CREATED_DATE);
		sNotesProjectionMap.put(NoteTable.MODIFIED_DATE,
				NoteTable.MODIFIED_DATE);
	}

	// Provide a mechanism to identify all the incoming uri patterns.
	private static final UriMatcher sUriMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	private static final int INCOMING_NOTE_COLLECTION_URI_INDICATOR = 1;
	private static final int INCOMING_SINGLE_NOTE_URI_INDICATOR = 2;
	static {
		sUriMatcher.addURI(NoteProviderMetaData.AUTHORITY, "notes",
				INCOMING_NOTE_COLLECTION_URI_INDICATOR);
		sUriMatcher.addURI(NoteProviderMetaData.AUTHORITY, "notes/#",
				INCOMING_SINGLE_NOTE_URI_INDICATOR);
	}

	// Class to create, open and upgrade the database file
	private class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, NoteProviderMetaData.DATABASE_NAME, null,
					NoteProviderMetaData.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			NoteTable.create(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			NoteTable.upgrade(db);
		}
	}

	private DatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case INCOMING_NOTE_COLLECTION_URI_INDICATOR:
			return NoteTable.CONTENT_TYPE;

		case INCOMING_SINGLE_NOTE_URI_INDICATOR:
			return NoteTable.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case INCOMING_NOTE_COLLECTION_URI_INDICATOR:
			qb.setTables(NoteTable.TABLE_NAME);
			qb.setProjectionMap(sNotesProjectionMap);
			break;

		case INCOMING_SINGLE_NOTE_URI_INDICATOR:
			qb.setTables(NoteTable.TABLE_NAME);
			qb.setProjectionMap(sNotesProjectionMap);
			qb.appendWhere(NoteTable._ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		String orderBy = TextUtils.isEmpty(sortOrder) ? NoteTable.DEFAULT_SORT_ORDER
				: sortOrder;

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);
		// ???
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		if (sUriMatcher.match(uri) != INCOMING_NOTE_COLLECTION_URI_INDICATOR) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values = initialValues != null ? new ContentValues(
				initialValues) : new ContentValues();

		long now = System.currentTimeMillis();

		if (!values.containsKey(NoteTable.TITLE)) {
			throw new SQLException(
					"Failed to insert row because title of note is needed "
							+ uri);
		}
		if (!values.containsKey(NoteTable.CREATED_DATE)) {
			values.put(NoteTable.CREATED_DATE, now);
		}
		if (!values.containsKey(NoteTable.MODIFIED_DATE)) {
			values.put(NoteTable.MODIFIED_DATE, now);
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		long insertedId = db.insert(NoteTable.TABLE_NAME, NoteTable.TITLE,
				values);
		if (insertedId <= 0) {
			throw new SQLException("Failed to insert row into " + uri);
		}
		Uri insertedNoteUri = ContentUris.withAppendedId(NoteTable.CONTENT_URI,
				insertedId);
		// ???
		getContext().getContentResolver().notifyChange(insertedNoteUri, null);
		return insertedNoteUri;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		
		if (!values.containsKey(NoteTable.MODIFIED_DATE)) {
			values.put(NoteTable.MODIFIED_DATE, System.currentTimeMillis());
		}

		switch (sUriMatcher.match(uri)) {
		case INCOMING_NOTE_COLLECTION_URI_INDICATOR:
			count = db.update(NoteTable.TABLE_NAME, values, where, whereArgs);
			break;

		case INCOMING_SINGLE_NOTE_URI_INDICATOR:
			String rowId = uri.getPathSegments().get(1);
			count = db.update(NoteTable.TABLE_NAME, values, NoteTable._ID + "=" + rowId
					+ (TextUtils.isEmpty(where) ? "" : " AND (" + where + ")"),
					whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;

		switch (sUriMatcher.match(uri)) {
		case INCOMING_NOTE_COLLECTION_URI_INDICATOR:
			count = db.delete(NoteTable.TABLE_NAME, where, whereArgs);
			break;

		case INCOMING_SINGLE_NOTE_URI_INDICATOR:
			String rowId = uri.getPathSegments().get(1);
			count = db.delete(NoteTable.TABLE_NAME, NoteTable._ID + "=" + rowId
					+ (TextUtils.isEmpty(where) ? "" : " AND (" + where + ")"),
					whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}
}
