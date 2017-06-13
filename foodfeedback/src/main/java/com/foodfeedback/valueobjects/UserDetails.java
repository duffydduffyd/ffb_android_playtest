package com.foodfeedback.valueobjects;

import java.io.Serializable;


public class UserDetails implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7587787078219082074L;
	private String userID;
	private String password;
	private int oneQuestion;
	private int twoQuestion;
	private int threeQuestion;
	private int fourQuestion;
	private int fiveQuestion;
	private int cameraRoll;
	private String checkOutLogin;
	
	
	public String getCheckOutLogin() {
		return checkOutLogin;
	}
	public void setCheckOutLogin(String checkOutLogin) {
		this.checkOutLogin = checkOutLogin;
	}
	public int getCameraRoll() {
		return cameraRoll;
	}
	public void setCameraRoll(int cameraRoll) {
		this.cameraRoll = cameraRoll;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getOneQuestion() {
		return oneQuestion;
	}
	public void setOneQuestion(int oneQuestion) {
		this.oneQuestion = oneQuestion;
	}
	public int getTwoQuestion() {
		return twoQuestion;
	}
	public void setTwoQuestion(int twoQuestion) {
		this.twoQuestion = twoQuestion;
	}
	public int getThreeQuestion() {
		return threeQuestion;
	}
	public void setThreeQuestion(int threeQuestion) {
		this.threeQuestion = threeQuestion;
	}
	public int getFourQuestion() {
		return fourQuestion;
	}
	public void setFourQuestion(int fourQuestion) {
		this.fourQuestion = fourQuestion;
	}
	public int getFiveQuestion() {
		return fiveQuestion;
	}
	public void setFiveQuestion(int fiveQuestion) {
		this.fiveQuestion = fiveQuestion;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
//	public String getFlashMode() {
//		System.out.println("#flash mode - "+flashMode);
//		try{
//			if (flashMode !=null){
//				flash_mode= flashMode;
//			}else{
//				flash_mode= Camera.Parameters.FOCUS_MODE_AUTO;
//			}
//		}catch(NullPointerException ex){
//			ex.printStackTrace();
//		}
//		return flash_mode;
//		
//	}
//	public void setFlashMode(String flashMode) {
//		this.flashMode = flashMode;
//	}
	
	}