package com.foodfeedback.valueobjects;

import com.google.gson.annotations.SerializedName;

public class PartiallyMatchingCoaches {
	
	private String bio;
	@SerializedName("first_name")
	private String firstName;
	@SerializedName("last_name")
	private String lastName;
	@SerializedName("profile_image_url")
	private String profileImageUrl;
	@SerializedName("profile_image_thumb_url")
	private String profileImageThumbUrl;
	private int coach;
	private int id;

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getFirst_name() {
		return firstName;
	}

	public void setFirst_name(String first_name) {
		this.firstName = first_name;
	}

	public String getLast_name() {
		return lastName;
	}

	public void setLast_name(String last_name) {
		this.lastName = last_name;
	}

	public String getProfile_image_url() {
		return profileImageUrl;
	}

	public void setProfile_image_url(String profile_image_url) {
		this.profileImageUrl = profile_image_url;
	}

	public String getProfile_image_thumb_url() {
		return profileImageThumbUrl;
	}

	public void setProfile_image_thumb_url(String profile_image_thumb_url) {
		this.profileImageThumbUrl = profile_image_thumb_url;
	}

	public int getCoach() {
		return coach;
	}

	public void setCoach(int coach) {
		this.coach = coach;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
