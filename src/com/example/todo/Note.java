package com.example.todo;

public class Note {
	private int mId;
	private String mTitle;
	private String mDescription;

	public Note() {}

	public Note(int id, String title) {
		mId = id;
		mTitle = title;
	}

	public Note(int id, String title, String description) {
		mId = id;
		mTitle = title;
		mDescription = description;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setDescription(String description) {
		mDescription = description;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		mId = id;
	}
}
