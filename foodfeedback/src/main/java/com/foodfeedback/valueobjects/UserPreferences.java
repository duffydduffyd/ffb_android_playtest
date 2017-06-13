package com.foodfeedback.valueobjects;

import java.io.Serializable;

import android.hardware.Camera;

public class UserPreferences implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object flashMode;
	private Object weightUnitPreference;
	private Object flash_mode;
	private String previouslySelectedCoaches;
	
	public String getPreviouslySelectedCoaches() {
		if (previouslySelectedCoaches != null)
			return previouslySelectedCoaches;
		else
			this.previouslySelectedCoaches = "";
			return previouslySelectedCoaches;
	}
	public void setPreviouslySelectedCoaches(String previouslySelectedCoaches) {
		if (this.previouslySelectedCoaches != null)
			this.previouslySelectedCoaches = previouslySelectedCoaches;
		else
			this.previouslySelectedCoaches = "";
	}
	
	public Object getFlashMode() {
		System.out.println("#flash mode - "+flashMode);
		try{
			if (flashMode !=null){
				flash_mode= flashMode;
			}else{
				flash_mode= Camera.Parameters.FOCUS_MODE_AUTO;
			}
		}catch(NullPointerException ex){
			ex.printStackTrace();
		}
		return flash_mode;
		
	}
	public void setFlashMode(String flashMode) {
		this.flashMode = flashMode;
	}
	public Object getWeightUnitPreference() {
		return weightUnitPreference;
	}
	public void setWeightUnitPreference(Object weightUnitPreference) {
		this.weightUnitPreference = weightUnitPreference;
	}

	
}
