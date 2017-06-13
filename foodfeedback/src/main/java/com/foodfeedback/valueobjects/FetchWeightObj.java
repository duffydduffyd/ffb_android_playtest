package com.foodfeedback.valueobjects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class FetchWeightObj implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5193255337157485445L;
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
