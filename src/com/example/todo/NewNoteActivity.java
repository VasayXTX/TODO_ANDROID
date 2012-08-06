package com.example.todo;

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
import android.widget.TextView;
import android.widget.Toast;

public class NewNoteActivity extends Activity implements OnClickListener {
	
	private static final String TAG = "NewNoteActivity";
	
	Button mAddNoteButton;
	
	// Service interconnection
	INoteService mService;
	NoteServiceConnection mConnection;

	class NoteServiceConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = INoteService.Stub.asInterface((IBinder) service);
			Log.d(TAG, "onServiceConnected() connected");
			Toast.makeText(NewNoteActivity.this, R.string.info_service_connected,
					Toast.LENGTH_SHORT).show();
			mAddNoteButton.setEnabled(true);
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
		
		mAddNoteButton = (Button) findViewById(R.id.addNoteButton);
		mAddNoteButton.setOnClickListener(this);
		mAddNoteButton.setEnabled(false);
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
		
		String title = ((TextView) findViewById(R.id.titleTextView)).getText().toString();
		String description = ((TextView) findViewById(R.id.titleTextView)).getText().toString();
		if (TextUtils.isEmpty(title)) {
			Toast.makeText(this, R.string.err_title_present, Toast.LENGTH_SHORT).show();
		} else {
			try {
				mService.addNote(title, description);
				Toast.makeText(this, R.string.info_note_added, Toast.LENGTH_SHORT).show();
				finish();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
