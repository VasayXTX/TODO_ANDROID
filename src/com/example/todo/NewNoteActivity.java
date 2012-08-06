package com.example.todo;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.todo.NoteProviderMetaData.NoteTable;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewNoteActivity extends Activity implements OnClickListener {
	
	private static final String TAG = "NewNoteActivity";
	
	private boolean mIsNewNote;
	private int mNoteId;
	
	// UI elements
	Button mAddUpdateNoteButton;
	EditText mTitleEditText;
	EditText mDescriptionEditText;
	
	// Service interconnection
	INoteService mService;
	NoteServiceConnection mConnection;

	class NoteServiceConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = INoteService.Stub.asInterface((IBinder) service);
			Log.d(TAG, "onServiceConnected() connected");
			Toast.makeText(NewNoteActivity.this, R.string.info_service_connected,
					Toast.LENGTH_SHORT).show();
			mAddUpdateNoteButton.setEnabled(true);
		}

		public void onServiceDisconnected(ComponentName name) {
			mService = null;
			Log.d(TAG, "onServiceDisconnected() disconnected");
			Toast.makeText(NewNoteActivity.this, R.string.info_service_disconnected,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void initService() {
		mConnection = new NoteServiceConnection();
		Intent intent = new Intent();
		intent.setClassName("com.example.todo",
				com.example.todo.NoteService.class.getName());
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	private void releaseService() {
		unbindService(mConnection);
		mConnection = null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_note_activity);
		
		initService();
		
		// Init UI elements
		mAddUpdateNoteButton = (Button) findViewById(R.id.addNoteButton);
		mAddUpdateNoteButton.setOnClickListener(this);
		mAddUpdateNoteButton.setEnabled(false);
		
		mTitleEditText = (EditText) findViewById(R.id.titleTextView);
		mDescriptionEditText = (EditText) findViewById(R.id.descriptionTextView);
		
		// Detect type of action ("add new note" || "edit existing note")
		Bundle extras = getIntent().getExtras();
		mIsNewNote = extras.getBoolean("isNewNote");
		if (!mIsNewNote) {
			mNoteId = extras.getInt(NoteTable._ID);
			mTitleEditText.setText(extras.getString(NoteTable.TITLE));
			mDescriptionEditText.setText(extras.getString(NoteTable.DESCRIPTION));
			mAddUpdateNoteButton.setText(R.string.update_note);
		}
	}
	
	@Override
	protected void onDestroy() {
		releaseService();
		super.onDestroy();
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addNoteButton:
			addNote();
			break;

		default:
			break;
		}
	}
	
	private void addNote() {
		if (mService == null) return;
		
		String title = mTitleEditText.getText().toString();
		String description = mDescriptionEditText.getText().toString();
		
		if (TextUtils.isEmpty(title)) {
			Toast.makeText(this, R.string.err_title_present, Toast.LENGTH_SHORT).show();
			return;
		}
		
		try {
			JSONObject noteJson = new JSONObject();
			noteJson.put(NoteTable.TITLE, title);
			noteJson.put(NoteTable.DESCRIPTION, description);
			int msgResId;
			if (mIsNewNote) {
				msgResId = R.string.info_note_added;
				mService.addNote(noteJson.toString());
			} else {
				msgResId = R.string.info_note_updated;
				noteJson.put(NoteTable._ID, mNoteId);
				mService.updateNote(noteJson.toString());
			}
			Toast.makeText(this, msgResId, Toast.LENGTH_SHORT).show();
			finish();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
