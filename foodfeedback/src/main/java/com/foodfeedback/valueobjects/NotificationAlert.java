package com.foodfeedback.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class NotificationAlert implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7983921977486446058L;
	@SerializedName("loc-args")
	private ArrayList<String> locArgs;
	@SerializedName("loc-key")
	private String locKey;
	
	public ArrayList<String> getLocArgs() {
		return locArgs;
	}
	public void setLocArgs(ArrayList<String> locArgs) {
		this.locArgs = locArgs;
	}
	public String getLocKey() {
		return locKey;
	}
	public void setLocKey(String locKey) {
		this.locKey = locKey;
	}
	
}
