package com.foodfeedback.valueobjects;


public class GalleryArrayPhotoDetails {
	
	private String timeStamp;
	private String imageThumbUrl;
	private String imageUrl;
	private String active;
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