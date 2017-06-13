package com.foodfeedback.valueobjects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class AccountOperations implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1138948318106715993L;
	private int status;
	@SerializedName("messagetext")
	private String messageText;
	private AccountOperationsData data;

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

	public AccountOperationsData getData() {
		return data;
	}

	public void setData(AccountOperationsData data) {
		this.data = data;
	}

}
