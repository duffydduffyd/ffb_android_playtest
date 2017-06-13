package com.foodfeedback.valueobjects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class FoodImagePost implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8115914233678186534L;
	@SerializedName("timestamp")
	private String timeStamp;
	@SerializedName("image_thumb_url")
	private String imageThumbUrl;
	@SerializedName("image_url")
	private String imageUrl;
	private String active;
	@SerializedName("owner_id")
	private String ownerId;
	private String id;
	
	public String getTimestamp() {
		return timeStamp;
	}
	public void setTimestamp(String timestamp) {
		this.timeStamp = timestamp;
	}
	public String getImage_thumb_url() {
		return imageThumbUrl;
	}
	public void setImage_thumb_url(String image_thumb_url) {
		this.imageThumbUrl = image_thumb_url;
	}
	public String getImage_url() {
		return imageUrl;
	}
	public void setImage_url(String image_url) {
		this.imageUrl = image_url;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getOwner_id() {
		return ownerId;
	}
	public void setOwner_id(String owner_id) {
		this.ownerId = owner_id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
}
