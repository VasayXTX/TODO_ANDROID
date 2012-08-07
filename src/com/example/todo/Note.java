package com.example.todo;

public class Note {
	private int mId;
	private String mTitle;
	private String mDescription;
	private int mType;

	public Note() {}

	public Note(int id, String title, String description, int type) {
		mId = id;
		mTitle = title;
		mDescription = description;
		mType = type;
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

	public int getType() {
		return mType;
	}

	public void setType(int type) {
		mType = type;
	}
}
