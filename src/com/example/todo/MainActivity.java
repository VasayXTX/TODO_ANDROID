package com.example.todo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = "TODO.MainActivity";
	private static final String TAG_SELECTED_ITEMS = "TODO.SelectedItems";
	private static final String TAG_SORT_ORDER = "TODO.SortOrder";
	
	
	private String mSortOrder = null;

	// UI elements
	private ListView mNotesListView;
	private Button mNewNoteButton;
	private Button mRemoveNotesButton;

	// Components to work with list
	private NoteArrayAdapter mAdapter;
	private boolean[] mSelectedItems;

	// Service interconnection
	INoteService mService;
	NoteServiceConnection mConnection;

	class NoteServiceConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = INoteService.Stub.asInterface((IBinder) service);
			Log.d(TAG, "onServiceConnected() connected");
			Toast.makeText(MainActivity.this, R.string.info_service_connected,
					Toast.LENGTH_SHORT).show();
			updateNotesList();
		}

		public void onServiceDisconnected(ComponentName name) {
			mService = null;
			Log.d(TAG, "onServiceDisconnected() disconnected");
			Toast.makeText(MainActivity.this, R.string.info_service_disconnected,
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initService();

		// Init UI elements
		mNewNoteButton = (Button) findViewById(R.id.newNoteButton);
		mRemoveNotesButton = (Button) findViewById(R.id.removeNotesButton);
		mNewNoteButton.setOnClickListener(this);
		mRemoveNotesButton.setOnClickListener(this);

		mNotesListView = (ListView) findViewById(R.id.notesListView);
	}

	@Override
	protected void onDestroy() {
		releaseService();
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateNotesList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mSelectedItems = mAdapter.getSelection();
		outState.putBooleanArray(TAG_SELECTED_ITEMS, mSelectedItems);
		outState.putString(TAG_SORT_ORDER, mSortOrder);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(TAG_SELECTED_ITEMS)) {
			mSelectedItems = savedInstanceState.getBooleanArray(TAG_SELECTED_ITEMS);
		}
		if (savedInstanceState.containsKey(TAG_SORT_ORDER)) {
			mSortOrder = savedInstanceState.getString(TAG_SORT_ORDER);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
//		case R.id.testNoteProvider:
//			Intent intent = new Intent(MainActivity.this,
//					TesterNoteProviderActivity.class);
//			startActivity(intent);
//			break;
		
		case R.id.sortByTitleAscMenuItem:
			mSortOrder = NoteTable.TITLE + " ASC";
			break;
			
		case R.id.sortByTitleDescMenuItem:
			mSortOrder = NoteTable.TITLE + " DESC";
			break;
			
		case R.id.sortByDateAscMenuItem:
			mSortOrder = NoteTable.MODIFIED_DATE + " ASC";
			break;
			
		case R.id.sortByDateDescMenuItem:
			mSortOrder = NoteTable.MODIFIED_DATE + " DESC";
			break;
		
		default:
			break;
		}
		updateNotesList();
		
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.newNoteButton:
			Intent intent = new Intent(MainActivity.this, NewNoteActivity.class);
			intent.putExtra("isNewNote", true);
			startActivity(intent);
			break;

		case R.id.removeNotesButton:
			deleteNotes();
			break;

		default:
			break;
		}
	}

	private void updateNotesList() {
		if (mService == null) return;
		
		List<Note> values = new ArrayList<Note>();
		Log.d(TAG, "Update list");
		
		// Parse json and fill values
		try {
			String notesJson = mService.getNotes(mSortOrder);
			JSONArray notesJsonArray = new JSONArray(notesJson);
			for (int i = 0; i < notesJsonArray.length(); ++i) {
				JSONObject noteJsonObject = notesJsonArray.getJSONObject(i);
				int id = noteJsonObject.getInt(NoteTable._ID);
				String title = noteJsonObject.getString(NoteTable.TITLE);
				String description = noteJsonObject.getString(NoteTable.DESCRIPTION);
				values.add(new Note(id, title, description));
			}
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		mAdapter = new NoteArrayAdapter(this, values, mSelectedItems);
		mNotesListView.setAdapter(mAdapter);
	}
	
	private void deleteNotes() {
		if (mService == null) return;
		
		Set<Integer> selectedNotes = mAdapter.getSelectedNotes();
		JSONArray notesJson = new JSONArray();
		Iterator<Integer> it = selectedNotes.iterator();
		while (it.hasNext()) {
			notesJson.put(it.next());
		}
		try {
			mService.deleteNotes(notesJson.toString());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		updateNotesList();
	}
}
