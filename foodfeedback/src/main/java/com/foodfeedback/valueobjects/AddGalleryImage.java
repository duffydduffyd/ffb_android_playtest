package com.foodfeedback.valueobjects;

import com.google.gson.annotations.SerializedName;

public class AddGalleryImage {
	
	private int status;
	@SerializedName("messagetext")
	private String messageText;
	private AddImage data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public AddImage getData() {
		return data;
	}

	public void setData(AddImage data) {
		this.data = data;
	}

}
