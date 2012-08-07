package com.example.todo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.example.todo.NoteProviderMetaData.NoteTable;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class NoteArrayAdapter extends ArrayAdapter<Note> {
	private final Activity context;
	 private final int[] ICONS = { R.drawable.it, R.drawable.home,
	 R.drawable.work, R.drawable.other};

	private List<Note> list;
	private boolean[] selectedNotes;
	
	// Events
	private OnCheckedChangeListener checkChangeListener = new OnCheckedChangeListener() {
		
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			CheckBox chbx = (CheckBox) buttonView;
			int pos = (Integer) chbx.getTag();
			selectedNotes[pos] = chbx.isChecked();
		}
	};
	
	private OnClickListener clickListener = new OnClickListener() {
		
		public void onClick(View v) {
			int pos = (Integer) v.getTag();
			Note note = list.get(pos);
			Intent intent = new Intent(context, NewNoteActivity.class);
			intent.putExtra("isNewNote", false);
			intent.putExtra(NoteTable._ID, note.getId());
			intent.putExtra(NoteTable.TITLE, note.getTitle());
			intent.putExtra(NoteTable.DESCRIPTION, note.getDescription());
			intent.putExtra(NoteTable.TYPE, note.getType());
			context.startActivity(intent);
		}
	};

	public NoteArrayAdapter(Activity context, List<Note> list,
			boolean[] selectedNotes) {
		super(context, R.layout.note_list_item, list);
		this.context = context;
		this.list = list;
		if (selectedNotes == null || selectedNotes.length != list.size()) {
			this.selectedNotes = new boolean[list.size()];
		} else {
			this.selectedNotes = selectedNotes;
		}
	}

	static class ViewHolder {
		protected TextView textView;
		protected CheckBox checkbox;
		public ImageView icon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;

		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.note_list_item, null);

			final ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
			
			viewHolder.textView = (TextView) view
					.findViewById(R.id.noteTitleTextView);
			viewHolder.textView.setOnClickListener(clickListener);
			viewHolder.textView.setTag(position);
			
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.checkBox);
			viewHolder.checkbox.setOnCheckedChangeListener(checkChangeListener);
			viewHolder.checkbox.setTag(position);
			
			view.setTag(viewHolder);
		} else {
			view = convertView;
			ViewHolder viewHolder = ((ViewHolder) view.getTag()); 
			viewHolder.checkbox.setTag(position);
			viewHolder.textView.setTag(position);
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		Note note = list.get(position);
		holder.textView.setText(note.getTitle());
		holder.icon.setImageResource(ICONS[note.getType()]);
		holder.checkbox.setChecked(selectedNotes[position]);

		return view;
	}

	public boolean[] getSelection() {
		return selectedNotes;
	}
	
	public Set<Integer> getSelectedNotes() {
		Set<Integer> res = new HashSet<Integer>();
		int i = 0;
		Iterator<Note> it = list.iterator();
		while (it.hasNext()) {
			Note note = it.next();
			if (!selectedNotes[i++]) continue;
			res.add(note.getId());
		}
		return res;
	}
}