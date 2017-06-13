package com.foodfeedback.onboarding;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.UserDetails;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.localytics.android.LocalyticsSession;

public class IntroActivity extends Activity {
	private LocalyticsSession localyticsSession;
	private Button getStartedButton;
	private Animation slideUpInAnim;
	AsyncTask<Void, Void, Void> checkForLogin;
	Typeface tfSpecial,tfNormal;
	TextView descLineOne, descLineTwo, descLineThree;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.intro_splash_screen);
		tfSpecial = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.app_font_style_medium));
		tfNormal  = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.font_standard));
		setupViewIds();
		setupFonts();
		animationToButton();

		if (checkPlayServices()) {
			new checkForValidLogin().execute();
		}

		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources
		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("Splash");
		this.localyticsSession.upload(); // upload any data

	}

	static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
	static final int REQUEST_CODE_PICK_ACCOUNT = 1002;

	private boolean checkPlayServices() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (status != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
				showErrorDialog(status);
			} else {
				Utilities.showErrorToast(getResources().getString(R.string.device_not_supported), this);
				finish();
			}
			getStartedButton.setEnabled(false);
			return false;
		}

		// getStartedButton.setText("Device not supported");
		addOnClickListeners();
		return true;
	}

	void showErrorDialog(int code) {
		GooglePlayServicesUtil.getErrorDialog(code, this,
				REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
	}

	private class checkForValidLogin extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			UserDetails checkUserDetails;
			try {
				checkUserDetails = UserDetailsCacheManager.getObject(Controller
						.getAppBackgroundContext());
				if (checkUserDetails != null) {
					if (checkUserDetails.getCheckOutLogin() != null) {
						if (checkUserDetails.getCheckOutLogin().equals(
								"login_success")) {
							return "LOGINSUCCESS";
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return "NOLOGIN";
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.endsWith("LOGINSUCCESS")) {
				Intent intentFood = new Intent(getApplicationContext(),
						FoodTabActivity.class)
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intentFood);
			} else {

			}
		}
	}

	private void setupFonts() {

		getStartedButton.setTypeface(tfSpecial);
		descLineOne.setTypeface(tfNormal);
		descLineTwo.setTypeface(tfNormal);
		descLineThree.setTypeface(tfNormal);

	}

	private void animationToButton() {

		slideUpInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_upin);
		getStartedButton.startAnimation(slideUpInAnim);
	}

	/**
	 * This method is used to handle Events for Button click, Image click and so
	 * on.
	 */
	private void addOnClickListeners() {

		getStartedButton.setOnClickListener(new IntroActivityListener(this));
	}

	/**
	 * This method is used to get reference for individual UI components
	 * [views].
	 */
	private void setupViewIds() {

		getStartedButton = (Button) findViewById(R.id.getstartedButton);
		descLineOne = (TextView) findViewById(R.id.desc_lineone);
		descLineTwo = (TextView) findViewById(R.id.desc_linetwo);
		descLineThree = (TextView) findViewById(R.id.desc_linethree);
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
	public void onBackPressed() {
		finish();
	}

	@Override
	public void onStart() {
		super.onStart();
		System.out.println("google Analytics Start");

		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		System.out.println("google Analytics Stop");

		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}
}
