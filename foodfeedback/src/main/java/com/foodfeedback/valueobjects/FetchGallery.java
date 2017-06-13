package com.foodfeedback.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class FetchGallery implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -828422421068212108L;
	private int status;
	@SerializedName("messagetext")
	private String messageText;
	private ArrayList<FoodImagePost> data;

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

	public ArrayList<FoodImagePost> getData() {
		return data;
	}

	public void setData(ArrayList<FoodImagePost> data) {
		this.data = data;
	}

}
