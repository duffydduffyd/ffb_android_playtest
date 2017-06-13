package com.foodfeedback.valueobjects;

import java.io.Serializable;

public class CoachStatusNumber implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5214499851669182008L;
	
	private int coachStatusNo;

	public int getCoachStatusNo() {
	    return coachStatusNo;
	}

	public void setCoachStatusNo(int coachStatusNo) {
	    this.coachStatusNo = coachStatusNo;
	}

	
}
