package com.example.todo;

import java.util.ArrayList;
import java.util.List;

import com.example.todo.NoteProviderMetaData.NoteTable;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity implements OnClickListener{
	
	private static final String TAG = "TODO.MainActivity";
	
	private ListView mNotesListView;
	private Button mNewNoteButton;
	private Button mRemoveNotesButton;
	
	private NoteArrayAdapter mAdapter;
	private boolean[] mSavedSelectedNotes;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Init UI elements
        mNewNoteButton = (Button) findViewById(R.id.newNoteButton);
        mRemoveNotesButton = (Button) findViewById(R.id.removeNotesButton);
        mNewNoteButton.setOnClickListener(this);
        mRemoveNotesButton.setOnClickListener(this);
        
        mNotesListView = (ListView) findViewById(R.id.notesListView);
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
    	
    	boolean[] selectedNotes = mAdapter.getSelectedNotes(); 
    	outState.putBooleanArray(TAG, selectedNotes);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	
    	// It will be optimized
    	if (savedInstanceState.containsKey(TAG)) {
    		mSavedSelectedNotes = savedInstanceState.getBooleanArray(TAG);
    		updateNotesList();
    	}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.testNoteProvider:
			Intent intent = new Intent(MainActivity.this, TesterNoteProviderActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
    	return super.onOptionsItemSelected(item);
    }
    
    public void onClick(View v) {
    	switch (v.getId()) {
		case R.id.newNoteButton:
			break;
			
		case R.id.removeNotesButton:
			break;

		default:
			break;
		}
    	
    }
    
    private void updateNotesList() {
    	Log.d(TAG, "Update list");
    	
    	Uri uri = NoteTable.CONTENT_URI;
		Cursor c = managedQuery(uri, new String[] { NoteTable.TITLE }, null, null, null);
		int iTitle = c.getColumnIndex(NoteTable.TITLE);
		List<Note> values = new ArrayList<Note>();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			Note note = new Note();
			note.setTitle(c.getString(iTitle));
			values.add(note);
		}
    	
    	if (mSavedSelectedNotes != null) {
    		if (values.size() != mSavedSelectedNotes.length) {
    			mSavedSelectedNotes = null;
    			mAdapter = new NoteArrayAdapter(this, values);
    		} else {
    			mAdapter = new NoteArrayAdapter(this, values, mSavedSelectedNotes);
    		}
    	} else {
        	mAdapter = new NoteArrayAdapter(this, values);
    	}
		mNotesListView.setAdapter(mAdapter);
    }
}
