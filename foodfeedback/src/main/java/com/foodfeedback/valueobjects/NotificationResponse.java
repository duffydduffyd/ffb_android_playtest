package com.foodfeedback.valueobjects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class NotificationResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7203332957323802327L;
	@SerializedName("content-available")
	private String contentAvailable;
	private NotificationAlert alert;
	private String sound;
	private int badge;
	
	public String getContentAvailable() {
		return contentAvailable;
	}
	public void setContentAvailable(String contentAvailable) {
		this.contentAvailable = contentAvailable;
	}
	public NotificationAlert getAlert() {
		return alert;
	}
	public void setAlert(NotificationAlert alert) {
		this.alert = alert;
	}
	public String getSound() {
		return sound;
	}
	public void setSound(String sound) {
		this.sound = sound;
	}
	public int getBadge() {
		return badge;
	}
	public void setBadge(int badge) {
		this.badge = badge;
	}
}
