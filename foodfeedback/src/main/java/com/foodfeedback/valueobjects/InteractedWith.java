package com.foodfeedback.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class InteractedWith implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3855798209418698168L;
	
	private int status;
	@SerializedName("messagetext")
	private String messagetext;
	private ArrayList<MemberInfo> data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessagetext() {
		return messagetext;
	}

	public void setMessagetext(String messagetext) {
		this.messagetext = messagetext;
	}

	public ArrayList<MemberInfo> getData() {
		return data;
	}

	public void setData(ArrayList<MemberInfo> data) {
		this.data = data;
	}

}
