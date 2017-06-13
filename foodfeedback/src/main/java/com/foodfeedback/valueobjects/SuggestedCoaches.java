package com.foodfeedback.valueobjects;

import com.google.gson.annotations.SerializedName;

public class SuggestedCoaches {
	
	private int status;
	@SerializedName("messagetext")
	private String messageText;
	private SuggestedCoachesData data;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessagetext() {
		return messageText;
	}
	public void setMessagetext(String messagetext) {
		this.messageText = messagetext;
	}
	public SuggestedCoachesData getData() {
		return data;
	}
	public void setData(SuggestedCoachesData data) {
		this.data = data;
	}
	
}
