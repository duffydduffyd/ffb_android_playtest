package com.foodfeedback.valueobjects;

import com.google.gson.annotations.SerializedName;

public class Answer {

	private String answer;
	private int id;
	@SerializedName("answer_detail")
	private String answerDetail;
	
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAnswer_detail() {
		return answerDetail;
	}
	public void setAnswer_detail(String answer_detail) {
		this.answerDetail = answer_detail;
	}
}
