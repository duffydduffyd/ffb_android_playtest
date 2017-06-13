package com.foodfeedback.valueobjects;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class SuggestedCoachesData {
	
	@SerializedName("partially_matching_coaches")
	private ArrayList<PartiallyMatchingCoaches> partiallyMatchingCoaches;
	@SerializedName("matching_coaches")
	private ArrayList<MatchingCoaches> matchingCoaches;
	
	public ArrayList<PartiallyMatchingCoaches> getPartially_matching_coaches() {
		return partiallyMatchingCoaches;
	}
	public void setPartially_matching_coaches(ArrayList<PartiallyMatchingCoaches> partially_matching_coaches) {
		this.partiallyMatchingCoaches = partially_matching_coaches;
	}
	public ArrayList<MatchingCoaches> getMatching_coaches() {
		return matchingCoaches;
	}
	public void setMatching_coaches(ArrayList<MatchingCoaches> matching_coaches) {
		this.matchingCoaches = matching_coaches;
	}
	
}
