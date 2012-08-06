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
import android.util.Log;

import com.example.todo.NoteProviderMetaData.NoteTable;

public class NoteService extends Service {
	private static final String TAG = "NoteService";

	public class NoteServiceImpl extends INoteService.Stub {
		public String getNotes() {
			return NoteService.this.getNotes();
		}
		
		public void addNote(String title, String description) {
			NoteService.this.addNote(title, description);
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

	private String getNotes() {
		Uri uri = NoteTable.CONTENT_URI;
		ContentResolver cr = getContentResolver();
		String[] projection = new String[] { NoteTable._ID, NoteTable.TITLE,
				NoteTable.MODIFIED_DATE };
		Cursor c = cr.query(uri, projection, null, null, null);

		int iId = c.getColumnIndex(NoteTable._ID);
		int iTitle = c.getColumnIndex(NoteTable.TITLE);
//		int iModifiedDate = c.getColumnIndex(NoteTable.MODIFIED_DATE);

		JSONArray res = new JSONArray();
		try {
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", c.getString(iId));
				jsonObject.put("title", c.getString(iTitle));
				res.put(jsonObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return res.toString();
	}
	
	private void addNote(String title, String description) {
		ContentValues cv = new ContentValues();
		cv.put(NoteTable.TITLE, title);
		cv.put(NoteTable.DESCRIPTION, description);
		
		ContentResolver cr = getContentResolver();
		Uri uri = NoteTable.CONTENT_URI;
		cr.insert(uri, cv);
	}
}
