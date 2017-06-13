package com.foodfeedback.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class Invitations implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4079630172485213997L;
	private int status;
	@SerializedName("messagetext")
	private String messageText;
	private ArrayList<InvitationItem> data;

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

	public ArrayList<InvitationItem> getData() {
		return data;
	}

	public void setData(ArrayList<InvitationItem> invitationArray) {
		this.data = invitationArray;
	}

}
