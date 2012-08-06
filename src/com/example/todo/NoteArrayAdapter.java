package com.example.todo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class NoteArrayAdapter extends ArrayAdapter<Note> {
	private static final String TAG = "NoteArrayAdapter";

	private final Activity context;
	// private final int[] ICONS = { R.drawable.git, R.drawable.android,
	// R.drawable.apple, R.drawable.coffee };

	private List<Note> list;
	private boolean[] selectedNotes;

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
		protected TextView text;
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
			viewHolder.text = (TextView) view
					.findViewById(R.id.noteTitleTextView);
			viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.checkBox);
			viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							int pos = (Integer) viewHolder.checkbox.getTag();
							selectedNotes[pos] = viewHolder.checkbox.isChecked();
						}
					});

			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(position);
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(position);
		}

		ViewHolder holder = (ViewHolder) view.getTag();
		Note note = list.get(position);
		holder.text.setText(note.getTitle());
		holder.icon.setImageResource(R.drawable.main_icon);
		// holder.icon.setImageResource(ICONS[listItem.getIconIndex()]);
		holder.checkbox.setChecked(selectedNotes[position]);

		// Log.d(TAG, Arrays.toString(selectedNotes.toArray()));

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