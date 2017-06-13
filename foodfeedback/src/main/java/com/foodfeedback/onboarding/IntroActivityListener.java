package com.foodfeedback.onboarding;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class IntroActivityListener implements OnClickListener {

    private Activity activityContext;

    public IntroActivityListener(Activity introActivity) {

	this.activityContext = introActivity;

    }

    @Override
    public void onClick(View v) {
	// TODO Auto-generated method stub
	int ViewId = v.getId();

	switch (ViewId) {

	case R.id.getstartedButton:

	    v.getContext().startActivity(
		    new Intent(v.getContext(), LoginActivity.class));

	    break;
	}
    }
}
