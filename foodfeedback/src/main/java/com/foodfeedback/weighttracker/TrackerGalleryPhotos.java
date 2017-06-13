package com.foodfeedback.weighttracker;

import java.util.ArrayList;

import android.content.Context;
import android.widget.ScrollView;

public class TrackerGalleryPhotos extends ScrollView{

	public TrackerGalleryPhotos(Context context) {
		super(context);
	}

	public TrackerGalleryPhotos(Context context,ArrayList<GroupDefinition> groupsToShow) {
		super(context);
	}

}
