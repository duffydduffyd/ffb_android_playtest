package com.foodfeedback.weighttracker;

import com.google.gson.annotations.SerializedName;

public class WeightObject {

	@SerializedName("timestamp")
	private double timeStamp;
	private int id;
	private double kilos;
	
	public double getTimestamp() {
		return timeStamp;
	}
	public void setTimestamp(double timestamp) {
		this.timeStamp = timestamp;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getKilos() {
		return kilos;
	}
	public void setKilos(double kilos) {
		this.kilos = kilos;
	}
}
