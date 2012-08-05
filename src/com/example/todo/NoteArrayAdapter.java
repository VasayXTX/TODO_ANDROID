package com.example.todo;

import java.util.List;

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
	private final Activity context;
//	private final int[] ICONS = { R.drawable.git, R.drawable.android,
//			R.drawable.apple, R.drawable.coffee };

	private List<Note> list;
	private boolean[] selectedNotes;
 
	public NoteArrayAdapter(Activity context, List<Note> list) {
		this(context, list, new boolean[list.size()]);
	}

	public NoteArrayAdapter(Activity context, List<Note> list,
			boolean[] selectedNotes) {
		super(context, R.layout.note_list_item, list);
		this.context = context;
		this.list = list;
		this.selectedNotes = selectedNotes;
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
			viewHolder.text = (TextView) view.findViewById(R.id.noteTitleTextView);
			viewHolder.icon = (ImageView) view.findViewById(R.id.icon);
			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.checkBox);
			viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							int pos = (Integer) viewHolder.checkbox.getTag();
							selectedNotes[pos] = viewHolder.checkbox
									.isChecked();
						}
					});

			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(position);
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(position);
		}

		Note note = list.get(position);
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.text.setText(note.getTitle());
		holder.icon.setImageResource(R.drawable.main_icon);
//		holder.icon.setImageResource(ICONS[listItem.getIconIndex()]);
		holder.checkbox.setChecked(selectedNotes[position]);

		return view;
	}

	public boolean[] getSelectedNotes() {
		return selectedNotes;
	}
}