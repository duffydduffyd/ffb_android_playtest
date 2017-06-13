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

public class LoginActivity extends Activity {
	private LocalyticsSession localyticsSession;
	private LinearLayout forgotPasswordBTN;
	private Button loginBTN, createAccountBTN;
	private EditText emailEditText, passwordEditText;
	private TextView topbarTxt, forgotPasswordTxt;
	private LinearLayout leftBackbutton;
	private LinearLayout rightSideButton;
	Typeface tfSpecial, tfNormal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_screen);

		tfSpecial = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.app_font_style_medium));
		tfNormal = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.font_standard));

		setupViewIds();
		setupFonts();
		addOnClickListeners();
		addOnChangeListeners();

		// Instantiate the object
		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources

		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("Login");
		this.localyticsSession.upload(); // upload any data

	}

	private void setupFonts() {
		forgotPasswordTxt.setTypeface(tfSpecial);
		topbarTxt.setTypeface(tfSpecial);
		loginBTN.setTypeface(tfSpecial);
		createAccountBTN.setTypeface(tfSpecial);
		emailEditText.setTypeface(tfNormal);
		passwordEditText.setTypeface(tfNormal);

	}

	/**
	 * This method is used to handle Events for Button click, Image click and so
	 * on.
	 */
	private void addOnClickListeners() {

		createAccountBTN.setOnClickListener(new LoginActivityListener(this));
		forgotPasswordBTN.setOnClickListener(new LoginActivityListener(this));
		loginBTN.setOnClickListener(new LoginActivityListener(emailEditText,
				passwordEditText, this));
	}

	/**
	 * This method is used to handle change listeners like textChange listeners
	 * for EditText.
	 */
	private void addOnChangeListeners() {

		emailEditText.addTextChangedListener(new WatcherListener());
		passwordEditText.addTextChangedListener(new WatcherListener());

	}

	public class WatcherListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			ValidateAndActivateLogin();
		}

	}

	/**
	 * This method is used to validate and activate the login button.
	 */
	private void ValidateAndActivateLogin() {

		String emailPattern = "[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\\.[a-zA-Z]{2,4}";
		// String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

		if (emailEditText.getText().toString().trim().length() != 0
				&& passwordEditText.getText().toString().trim().length() != 0
				&& emailEditText.getText().toString().trim()
						.matches(emailPattern)
				&& passwordEditText.getText().toString().trim().length() >= 3) {
			loginBTN.setBackgroundResource(R.drawable.redbar_button);
			loginBTN.setEnabled(true);
		} else {
			loginBTN.setBackgroundResource(R.drawable.unfocus_redimage);
			loginBTN.setEnabled(false);
		}

	}

	/**
	 * This method is used to get reference for individual UI components
	 * [views].
	 */
	private void setupViewIds() {

		leftBackbutton = (LinearLayout) findViewById(R.id.leftbutton);
		rightSideButton = (LinearLayout) findViewById(R.id.rightbutton);
		rightSideButton.setVisibility(View.GONE);
		leftBackbutton.setVisibility(View.GONE);

		topbarTxt = (TextView) findViewById(R.id.center_txt);
		topbarTxt.setText(getResources().getString(R.string.login_topbar));

		createAccountBTN = (Button) findViewById(R.id.createaccountButton);
		forgotPasswordTxt = (TextView) findViewById(R.id.forgotpassword_txt);

		emailEditText = (EditText) findViewById(R.id.email_edittxt);
		passwordEditText = (EditText) findViewById(R.id.password_edittxt);

		forgotPasswordBTN = (LinearLayout) findViewById(R.id.forget_password);

		loginBTN = (Button) findViewById(R.id.loginButton);
		loginBTN.setBackgroundResource(R.drawable.unfocus_redimage);
		loginBTN.setEnabled(false);
	}

	@Override
	public void onBackPressed() {

		/*
		 * System.out.println("onBackPressed systemexits");
		 * android.os.Process.killProcess(android.os.Process.myPid());
		 * System.exit(0); finish();
		 */

		startActivity(new Intent(this, IntroActivity.class)
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
