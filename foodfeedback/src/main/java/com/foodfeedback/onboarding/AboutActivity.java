package com.foodfeedback.onboarding;

import android.app.Activity;
import android.os.Bundle;

import com.localytics.android.*;
import com.google.analytics.tracking.android.EasyTracker;

public class AboutActivity extends Activity {
    private LocalyticsSession localyticsSession;

    public AboutActivity() {
	// TODO Auto-generated constructor stub
    }

    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	// Activity Creation Code

	// Instantiate the object
	this.localyticsSession = new LocalyticsSession(
		this.getApplicationContext()); // Context used to access device
					       // resources

	this.localyticsSession.open(); // open the session
	this.localyticsSession.tagScreen("All others");
	this.localyticsSession.upload(); // upload any data

	// At this point, Localytics Initialization is done. After uploads
	// complete nothing
	// more will happen due to Localytics until the next time you call it.
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	// TODO Auto-generated method stub

    }

    public void onResume() {
	super.onResume();
	this.localyticsSession.open();
    }

    public void onPause() {
	this.localyticsSession.close();
	this.localyticsSession.upload();
	super.onPause();
    }
    @Override
	  public void onStart() {
	    super.onStart();
	    System.out.println("google Analytics Start");
		   
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	  }

	  @Override
	  public void onStop() {
	    super.onStop();
	    System.out.println("google Analytics Stop");
		   
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	  }


}
