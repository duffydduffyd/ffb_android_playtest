package com.foodfeedback.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class CoachList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2127242072940134303L;
	private int status;
	@SerializedName("messagetext")
	private String messageText;
	private ArrayList<MemberInfo> data;

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

	public ArrayList<MemberInfo> getData() {
		return data;
	}

	public void setData(ArrayList<MemberInfo> data) {
		this.data = data;
	}

}
