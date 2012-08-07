package com.example.todo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.todo.NoteProviderMetaData.NoteTable;

public class NoteService extends Service {
	private static final String TAG = "NoteService";

	// Implementation of Android Interface
	public class NoteServiceImpl extends INoteService.Stub {
		public String getNotes(String sortOrder) {
			return NoteService.this.getNotes(sortOrder);
		}

		public void addNote(String note) {
			NoteService.this.addNote(note);
		}

		public void deleteNotes(String notes) {
			NoteService.this.deleteNotes(notes);
		}

		public void updateNote(String note) {
			NoteService.this.updateNote(note);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new NoteServiceImpl();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate()");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy()");
	}

	private String getNotes(String sortOrder) {
		Uri uri = NoteTable.CONTENT_URI;
		ContentResolver cr = getContentResolver();
		String[] projection = new String[] { NoteTable._ID, NoteTable.TITLE,
				NoteTable.DESCRIPTION, NoteTable.MODIFIED_DATE };
		Cursor c = cr.query(uri, projection, null, null, sortOrder);

		int iId = c.getColumnIndex(NoteTable._ID);
		int iTitle = c.getColumnIndex(NoteTable.TITLE);
		int iDescription = c.getColumnIndex(NoteTable.DESCRIPTION);

		JSONArray res = new JSONArray();
		try {
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(NoteTable._ID, c.getString(iId));
				jsonObject.put(NoteTable.TITLE, c.getString(iTitle));
				jsonObject.put(NoteTable.DESCRIPTION, c.getString(iDescription));
				res.put(jsonObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res.toString();
	}

	private void addNote(String note) {
		ContentValues cv = new ContentValues();

		try {
			JSONObject noteJson = new JSONObject(note);
			cv.put(NoteTable.TITLE, noteJson.getString(NoteTable.TITLE));
			cv.put(NoteTable.DESCRIPTION,
					noteJson.getString(NoteTable.DESCRIPTION));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ContentResolver cr = getContentResolver();
		Uri uri = NoteTable.CONTENT_URI;
		cr.insert(uri, cv);
	}

	private void deleteNotes(String notes) {
		ContentResolver cr = getContentResolver();
		try {
			JSONArray notesJsonArray = new JSONArray(notes);
			for (int i = 0; i < notesJsonArray.length(); ++i) {
				String id = notesJsonArray.getString(i);
				Uri delUri = Uri.withAppendedPath(NoteTable.CONTENT_URI, id);
				cr.delete(delUri, null, null);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void updateNote(String note) {
		ContentValues cv = new ContentValues();
		int id = -1;

		try {
			JSONObject noteJsonObject = new JSONObject(note);
			id = noteJsonObject.getInt(NoteTable._ID);
			cv.put(NoteTable.TITLE, noteJsonObject.getString(NoteTable.TITLE));
			cv.put(NoteTable.DESCRIPTION, noteJsonObject.getString(NoteTable.DESCRIPTION));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ContentResolver cr = getContentResolver();
		Uri uri = Uri.withAppendedPath(NoteTable.CONTENT_URI,
				Integer.toString(id));
		cr.update(uri, cv, null, null);
	}
}
