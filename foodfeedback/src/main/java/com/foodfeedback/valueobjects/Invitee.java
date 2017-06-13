package com.foodfeedback.valueobjects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Invitee implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5746262699956795305L;
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
// {
// "status": 0,
// "messagetext": "Fetched invites successfully.",
// "data": [
// {
// "status": -1,
// "timestamp": 1385016542,
// "invitee": {
// "bio": "",
// "first_name": "Mohammed",
// "last_name": "Gouse",
// "profile_image_url": "",
// "profile_image_thumb_url": "",
// "coach": 0,
// "id": 96
// },
// "id": 84,
// "sender": {
// "bio": "",
// "first_name": "Ghouse",
// "last_name": "Mohammed",
// "profile_image_url": "",
// "profile_image_thumb_url": "",
// "coach": 0,
// "id": 97
// }
// }
// ]
// }