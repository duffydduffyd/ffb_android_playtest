package com.foodfeedback.onboarding;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class RegisterActivity extends Activity {
	private LocalyticsSession localyticsSession;
	public LinearLayout backBTN, rightSideButton;
	private EditText firstNameEditText, lastNameEditText, enterEmailEditText,
			choosePasswordEditText;
	private Button registerBTN;
	private TextView createAcccountTxt;

	Typeface tfSpecial, tfNormal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.create_account);

		tfSpecial = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.app_font_style_medium));
		tfNormal = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.font_standard));
		setUpViewIds();
		setupFonts();
		addOnChangeListeners();
		addOnClickListeners();
		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources
		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("New User Registeration");

		this.localyticsSession.upload(); // upload any data
	}

	private void setupFonts() {

		createAcccountTxt.setTypeface(tfSpecial);
		registerBTN.setTypeface(tfSpecial);

	}

	/**
	 * This method is used to handle view change listeners like textChange
	 * Listener for EditText.
	 */
	private void addOnChangeListeners() {

		firstNameEditText.addTextChangedListener(new WatcherListener());
		lastNameEditText.addTextChangedListener(new WatcherListener());
		enterEmailEditText.addTextChangedListener(new WatcherListener());
		choosePasswordEditText.addTextChangedListener(new WatcherListener());
	}

	public class WatcherListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			ValidateAndActivateLogin();
		}

	}

	public void ValidateAndActivateLogin() {

		String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

		if (firstNameEditText.getText().toString().trim().length() != 0
				&& lastNameEditText.getText().toString().trim().length() != 0
				&& enterEmailEditText.getText().toString().trim().length() != 0
				&& choosePasswordEditText.getText().toString().trim().length() != 0
				&& enterEmailEditText.getText().toString().trim()
						.matches(emailPattern)
				&& choosePasswordEditText.getText().toString().trim().length() >= 3) {
			registerBTN.setBackgroundResource(R.drawable.redbar_button);
			registerBTN.setEnabled(true);
		} else {
			registerBTN.setBackgroundResource(R.drawable.unfocus_redimage);
			registerBTN.setEnabled(false);
		}

	}

	/**
	 * This method is used to handle Events for Button click, Image click and so
	 * on.
	 */
	private void addOnClickListeners() {

		backBTN.setOnClickListener(new RegisterActivityListener());
		registerBTN.setOnClickListener(new RegisterActivityListener(
				firstNameEditText, lastNameEditText, enterEmailEditText,
				choosePasswordEditText, this));
	}

	/**
	 * This method is used to get reference for individual UI components
	 * [views].
	 */
	private void setUpViewIds() {

		createAcccountTxt = (TextView) findViewById(R.id.center_txt);
		createAcccountTxt.setText(getResources().getString(
				R.string.create_an_account));
		backBTN = (LinearLayout) findViewById(R.id.leftbutton);
		rightSideButton = (LinearLayout) findViewById(R.id.rightbutton);
		rightSideButton.setVisibility(View.GONE);
		firstNameEditText = (EditText) findViewById(R.id.firstname_txt);
		lastNameEditText = (EditText) findViewById(R.id.lastname_txt);
		enterEmailEditText = (EditText) findViewById(R.id.enter_email_txt);
		choosePasswordEditText = (EditText) findViewById(R.id.choose_password_txt);

		registerBTN = (Button) findViewById(R.id.register_btn);
		registerBTN.setBackgroundResource(R.drawable.unfocus_redimage);
		registerBTN.setEnabled(false);
	}

	@Override
	public void onBackPressed() {

		System.out.println("onBackPressed");
		startActivity(new Intent(this, LoginActivity.class)
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

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

		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		System.out.println("google Analytics Stop");

		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

}