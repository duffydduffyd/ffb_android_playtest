package com.foodfeedback.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class FetchWeight implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1904448352813812204L;
	private int status;
	@SerializedName("messagetext")
	private String messageText;
	private ArrayList<FetchWeightObj> data;
	
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
	public ArrayList<FetchWeightObj> getData() {
		return data;
	}
	public void setData(ArrayList<FetchWeightObj> data) {
		this.data = data;
	}
}
