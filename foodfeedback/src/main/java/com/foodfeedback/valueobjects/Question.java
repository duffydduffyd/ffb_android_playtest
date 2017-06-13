package com.foodfeedback.valueobjects;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class Question {

	@SerializedName("selected_answer_id")
	private String selectedAnswerId;
	private int type;
	private String question;
	private int id;
	private ArrayList<Answer> answers;
	
	public String getSelectedAnswerId() {
		return selectedAnswerId;
	}
	public void setSelectedAnswerId(String selectedAnswerId) {
		this.selectedAnswerId = selectedAnswerId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ArrayList<Answer> getAnswers() {
		return answers;
	}
	public void setAnswers(ArrayList<Answer> answers) {
		this.answers = answers;
	}
}
