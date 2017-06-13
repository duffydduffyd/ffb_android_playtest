package com.foodfeedback.valueobjects;

import com.google.gson.annotations.SerializedName;

public class StatusUpdate {
	
	private int status;
	@SerializedName("messagetext")
	private String messageText;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int id) {
		this.status = id;
	}
	public String getMessagetext() {
		return messageText;
	}
	public void setMessagetext(String messagetext) {
		this.messageText = messagetext;
	}
}
