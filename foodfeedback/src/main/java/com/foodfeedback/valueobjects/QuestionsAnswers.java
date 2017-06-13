package com.foodfeedback.valueobjects;

import com.google.gson.annotations.SerializedName;

public class QuestionsAnswers {
	
	private int status;
	@SerializedName("messagetext")
	private String messageText;
	private QuestionsAnswersData data;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessagetext() {
		return messageText;
	}
	public void setMessagetext(String messagetext) {
		this.messageText = messagetext;
	}
	public QuestionsAnswersData getData() {
		return data;
	}
	public void setData(QuestionsAnswersData data) {
		this.data = data;
	}
}
