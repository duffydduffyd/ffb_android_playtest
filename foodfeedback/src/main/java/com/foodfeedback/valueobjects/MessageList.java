package com.foodfeedback.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class MessageList implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8072211284572332372L;
	private int status;
	@SerializedName("messagetext")
	private String messageText;
	private ArrayList<MessageItem> data;

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

	public ArrayList<MessageItem> getData() {
		return data;
	}

	public void setData(ArrayList<MessageItem> data) {
		this.data = data;
	}

}
