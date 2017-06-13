package com.foodfeedback.weighttracker;

import java.util.ArrayList;

import com.foodfeedback.valueobjects.FoodImagePost;

public class GroupDefinition {
	private long groupStartTime;
	private long groupEndTime;
	private long groupDisplayStartTime;
	private long groupDisplayEndTime;
	private ArrayList<FoodImagePost> imagesInGroup;

	public long getGroupStartTime() {
		return groupStartTime;
	}

	public void setGroupStartTime(long groupStartTime) {
		this.groupStartTime = groupStartTime;
	}

	public long getGroupEndTime() {
		return groupEndTime;
	}

	public void setGroupEndTime(long groupEndTime) {
		this.groupEndTime = groupEndTime;
	}

	public long getGroupDisplayStartTime() {
		return groupDisplayStartTime;
	}

	public void setGroupDisplayStartTime(long groupDisplayStartTime) {
		this.groupDisplayStartTime = groupDisplayStartTime;
	}

	public long getGroupDisplayEndTime() {
		return groupDisplayEndTime;
	}

	public void setGroupDisplayEndTime(long groupDisplayEndTime) {
		this.groupDisplayEndTime = groupDisplayEndTime;
	}

	public ArrayList<FoodImagePost> getImagesInGroup() {
		return imagesInGroup;
	}

	public void setImagesInGroup(ArrayList<FoodImagePost> imagesInGroup) {
		this.imagesInGroup = imagesInGroup;
	}

}