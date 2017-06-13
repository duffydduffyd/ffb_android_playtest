package com.foodfeedback.valueobjects;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class QuestionsAnswersData {

	private int id;
	@SerializedName("questions")
	private ArrayList<Question> questionsObj;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ArrayList<Question> getQuestions() {
		return questionsObj;
	}
	public void setQuestions(ArrayList<Question> questions) {
		this.questionsObj = questions;
	}
}
