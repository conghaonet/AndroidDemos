package com.app2m.demo.mymp4parser;

/**
 * Created by Administrator on 2015/6/6.
 */
public class MediaEntity {
	private int duration;
	private String filepath;
	private int size;
	private String mimeType;

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
}
