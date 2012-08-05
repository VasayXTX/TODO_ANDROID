package com.example.todo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.todo.NoteProviderMetaData.NoteTable;

public class TesterNoteProviderActivity extends Activity implements OnClickListener{
	private static final String TAG = "TesterNoteProviderActivity";
	
	private TextView mTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tester_note_provider_activity);
		
		mTextView = (TextView) findViewById(R.id.textView);
		int[] idButtons = new int[] {R.id.showButton, R.id.addButton, R.id.removeButton, R.id.clearButton};
		for (int id: idButtons) {
			((Button) findViewById(id)).setOnClickListener(this);
		}
		
		showNotes();
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.showButton:
			showNotes();
			break;
		
		case R.id.addButton:
			addNote();
			break;
			
		case R.id.removeButton:
			removeNote();
			break;
			
		case R.id.clearButton:
			clearDisplay();
			break;
			
		default:
			break;
		}
		
	}

	private void clearDisplay() {
		mTextView.setText("");
	}

	private void removeNote() {
		int curNotesCount = getNotesCount();
		Uri delUri = Uri.withAppendedPath(NoteTable.CONTENT_URI, Integer.toString(curNotesCount));
		ContentResolver cr = getContentResolver();
		cr.delete(delUri, null, null);
		
		Log.d(TAG,"deleted uri:" + delUri);
		
		showNotes();
	}

	private void addNote() {
		Log.d(TAG, "Adding a book");
		
		int curNotesCount = getNotesCount();
		
		ContentValues cv = new ContentValues();
		cv.put(NoteTable.TITLE, "note" + curNotesCount);
		cv.put(NoteTable.DESCRIPTION, "description" + curNotesCount);
		
		ContentResolver cr = getContentResolver();
		Uri uri = NoteTable.CONTENT_URI;
		Uri insertedUri = cr.insert(uri, cv);
		
		Log.d(TAG,"book insert uri:" + uri);
		Log.d(TAG,"inserted uri:" + insertedUri);
		
		showNotes();
	}

	private void showNotes() {
		Uri uri = NoteTable.CONTENT_URI;
		Cursor c = managedQuery(uri, null, null, null, null);
		
		int iTitle = c.getColumnIndex(NoteTable.TITLE);
		int iDescription = c.getColumnIndex(NoteTable.DESCRIPTION);
		
		String res = "";
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			res += "TITLE:" + c.getString(iTitle) + "\tDESCRIPTION: " + c.getString(iDescription) + "\n"; 
		}
		
		mTextView.setText(res);
		
		c.close();
	}
	
	private int getNotesCount() {
		Uri uri = NoteTable.CONTENT_URI;
		Cursor c = managedQuery(uri, null, null, null, null);
		int res = c.getCount();
		c.close();
		return res;
	}
}
