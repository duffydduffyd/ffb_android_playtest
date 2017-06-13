package com.foodfeedback.valueobjects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class AccountUser implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7587787078219082074L;
	private String bio;
	@SerializedName("first_name")
	private String firstName;
	@SerializedName("last_name")
	private String lastName;
	@SerializedName("private_coach")
	private String privateCoach;
	private String birthday;
	@SerializedName("profile_image_url")
	private String profileImageUrl;
	private String email;
	@SerializedName("profile_image_thumb_url")
	private String profileImageThumbUrl;
	private String coach;
	@SerializedName("completed_questionnaire")
	private String completedQuestionnaire;
	private int id;
	@SerializedName("notification_reminder_week")
	private String notificationRemenderWeek;
	
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPrivateCoach() {
		return privateCoach;
	}
	public void setPrivateCoach(String privateCoach) {
		this.privateCoach = privateCoach;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getProfileImageThumbUrl() {
		return profileImageThumbUrl;
	}
	public void setProfileImageThumbUrl(String profileImageThumbUrl) {
		this.profileImageThumbUrl = profileImageThumbUrl;
	}
	public String getCoach() {
		return coach;
	}
	public void setCoach(String coach) {
		this.coach = coach;
	}
	public String getCompletedQuestionnaire() {
		return completedQuestionnaire;
	}
	public void setCompletedQuestionnaire(String completedQuestionnaire) {
		this.completedQuestionnaire = completedQuestionnaire;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNotificationRemenderWeek() {
		return notificationRemenderWeek;
	}
	public void setNotificationRemenderWeek(String notificationRemenderWeek) {
		this.notificationRemenderWeek = notificationRemenderWeek;
	}
}