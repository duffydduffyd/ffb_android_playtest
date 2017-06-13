package com.foodfeedback.valueobjects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class MessageItem implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1504664432368659322L;
	@SerializedName("gallery_image_id")
	private String galleryImageId;
	private String active;
	@SerializedName("recipient_id")
	private String recipientId;
	@SerializedName("image_url")
	private String imageUrl;
	private String text;
	private String read;
	@SerializedName("timestamp")
	private String timeStamp;
	@SerializedName("image_thumb_url")
	private String imageThumbUrl;
	@SerializedName("sender_id")
	private String senderId;
	private String id;
	
	public String getGallery_image_id() {
		return galleryImageId;
	}
	public void setGallery_image_id(String gallery_image_id) {
		this.galleryImageId = gallery_image_id;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String getRecipient_id() {
		return recipientId;
	}
	public void setRecipient_id(String recipient_id) {
		this.recipientId = recipient_id;
	}
	public String getImage_url() {
		return imageUrl;
	}
	public void setImage_url(String image_url) {
		this.imageUrl = image_url;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getRead() {
		return read;
	}
	public void setRead(String read) {
		this.read = read;
	}
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
	public String getSender_id() {
		return senderId;
	}
	public void setSender_id(String sender_id) {
		this.senderId = sender_id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
