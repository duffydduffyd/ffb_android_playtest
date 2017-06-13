package com.foodfeedback.weighttracker;

import com.google.gson.annotations.SerializedName;

public class WeightUpdates {

	private int status;
	@SerializedName("messagetext")
	private String messageText;
	private WeightObject data;
	
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
	public WeightObject getData() {
		return data;
	}
	public void setData(WeightObject data) {
		this.data = data;
	}
}
