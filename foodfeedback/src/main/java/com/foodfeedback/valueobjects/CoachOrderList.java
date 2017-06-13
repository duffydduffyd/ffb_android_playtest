package com.foodfeedback.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;

public class CoachOrderList implements Serializable{
	
	private static final long serialVersionUID = -3308664580926985245L;
	private ArrayList<Integer> coachOrderList;

	public ArrayList<Integer> getcoachOrderList() {
		return coachOrderList;
	}

	public void setcoachOrderList(ArrayList<Integer> coachOrderList) {
		this.coachOrderList = coachOrderList;
	}

}
