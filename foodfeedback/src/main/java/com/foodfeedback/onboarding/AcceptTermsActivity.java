package com.foodfeedback.onboarding;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foodfeedback.networking.OnboardingNetworkingServices;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.TransferObject;
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class AcceptTermsActivity extends Activity implements OnClickListener {
	private LocalyticsSession localyticsSession;

	private WebView termsAcceptedWebView;
	private LinearLayout leftBackbutton;
	private LinearLayout rightSideButton;
	private TextView centerTxt;
	private Button iAcceptButton;
	Typeface tfNormal, tfSpecial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.accept_terms_screen);
		tfNormal = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.font_standard));
		tfSpecial = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.app_font_style_medium));
		setUpViewIds();
		setupFonts();
		addOnClickListeners();
		addTermsAcceptedWebView();
		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources

		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("All others");
		this.localyticsSession.upload(); // upload any data
	}

	private void addTermsAcceptedWebView() {

		termsAcceptedWebView
				.loadUrl("file:///android_asset/terms_of_service.html");
		
	}

	private void addOnClickListeners() {

		leftBackbutton.setOnClickListener(this);
		iAcceptButton.setOnClickListener(this);
	}

	private void setupFonts() {

		centerTxt.setTypeface(tfSpecial);
		iAcceptButton.setTypeface(tfSpecial);
	}

	private void setUpViewIds() {

		termsAcceptedWebView = (WebView) findViewById(R.id.webViewTerms);
		leftBackbutton = (LinearLayout) findViewById(R.id.leftbutton);
		rightSideButton = (LinearLayout) findViewById(R.id.rightbutton);
		rightSideButton.setVisibility(View.GONE);
		centerTxt = (TextView) findViewById(R.id.center_txt);
		centerTxt.setText(getResources().getString(
				R.string.settings_terms_of_service));
		iAcceptButton = (Button) findViewById(R.id.iaccept);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.leftbutton:
			finish();
			break;

		case R.id.iaccept:

			checkConnectivityForCreateAccount(v.getContext(),
					TransferObject.getCreateAccountFirstName(),
					TransferObject.getCreateAccountLastName(),
					TransferObject.getCreateAccountEmail(),
					TransferObject.getLoginPassword());

			break;

		}
	}

	private void checkConnectivityForCreateAccount(Context context,
			String firstNameStr, String lastNameStr, String emailStr,
			String choosePasswordStr) {

		if (Utilities.isConnectingToInternet(context.getApplicationContext())) {

			new OnboardingNetworkingServices(context)
					.requestCreateAccountService(context, firstNameStr,
							lastNameStr, emailStr, choosePasswordStr, this,
							ServiceKeys.CREATE_ACCOUNT_SERVICE);

		} else {
			Utilities.showErrorToast(
					getResources().getString(R.string.internet_connection),
					this);
		}

	}

	@Override
	public void onBackPressed() {

		System.out.println("onBackPressed");
		finish();
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
