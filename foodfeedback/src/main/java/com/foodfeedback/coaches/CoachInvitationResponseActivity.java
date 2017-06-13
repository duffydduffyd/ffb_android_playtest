package com.foodfeedback.coaches;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.myfood.ImageLoader;
import com.foodfeedback.networking.OnboardingNetworkingServices;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.AccountOperations;
import com.foodfeedback.valueobjects.UserDetails;
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class CoachInvitationResponseActivity extends Activity {
	private LocalyticsSession localyticsSession;

	private TextView coachBio, acceptInvitation, rejectInvitation;
	private LinearLayout leftButton, rightButton;
	private TextView centreTitleTxt;
	public static String coachNameStr, coachBioStr;
	public String userThumbUrl, userProfileUrl;
	public static int invitationId;
	private RelativeLayout goodMatch, notForMe;
	String password;
	AccountOperations accountOperationsObj;
	UserDetails userDetailsObj;
	int loader = R.drawable.loader;
	private ImageView layoutProfileBg;
	private ImageView userThumbImage;

	public CoachInvitationResponseActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.coaches_detail_screen);
		setResult(RESULT_CANCELED);
		try {
			accountOperationsObj = LoginDetailsCacheManager
					.getObject(Controller.getAppBackgroundContext());
			userDetailsObj = UserDetailsCacheManager.getObject(Controller
					.getAppBackgroundContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Bundle bundle = getIntent().getExtras();

		coachNameStr = bundle.getString("coachName");
		coachBioStr = bundle.getString("coachBio");
		invitationId = bundle.getInt("invitationId");
		userThumbUrl = bundle.getString("thumbUrl");
		userProfileUrl = bundle.getString("profileUrl");

		setUpViewIds();
		setupFonts();
		addOnClickListeners();

		ImageLoader imgLoader = new ImageLoader(getApplicationContext());

		if (!userThumbUrl.equals("")) {
			imgLoader.DisplayImage(userThumbUrl, loader, userThumbImage,false);
		}

		if (!userProfileUrl.equals("")) {
			imgLoader.DisplayImage(userProfileUrl, loader, layoutProfileBg,true);
		}
		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources

		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("Coaches");

		this.localyticsSession.upload(); // upload any data
	}

	private void addOnClickListeners() {

	}

	private void setupFonts() {
		centreTitleTxt.setTypeface(Typeface.createFromAsset(this.getAssets(),
				this.getResources().getString(R.string.app_font_style_medium)));
		acceptInvitation.setTypeface(Typeface.createFromAsset(this.getAssets(),
				this.getResources().getString(R.string.font_standard)));
		rejectInvitation.setTypeface(Typeface.createFromAsset(this.getAssets(),
				this.getResources().getString(R.string.font_standard)));

	}

	private void setUpViewIds() {

		goodMatch = (RelativeLayout) findViewById(R.id.goodmatch);
		notForMe = (RelativeLayout) findViewById(R.id.notforme);

		layoutProfileBg = (ImageView) findViewById(R.id.layout_photobg);
		userThumbImage = (ImageView) findViewById(R.id.layout_photoprofile);

		notForMe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				checkConnectivityForInvitationResponse(
						v.getContext(),
						Integer.toString(accountOperationsObj.getData()
								.getUser().getId()),
						userDetailsObj.getPassword(),
						Integer.toString(invitationId), "0");
				// finish();
			}
		});

		goodMatch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				checkConnectivityForInvitationResponse(
						v.getContext(),
						Integer.toString(accountOperationsObj.getData()
								.getUser().getId()),
						userDetailsObj.getPassword(),
						Integer.toString(invitationId), "1");
			}
		});

		centreTitleTxt = (TextView) findViewById(R.id.center_txt);
		centreTitleTxt.setText(coachNameStr);
		leftButton = (LinearLayout) findViewById(R.id.leftbutton);
		leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});
		rightButton = (LinearLayout) findViewById(R.id.rightbutton);
		rightButton.setVisibility(View.GONE);
		coachBio = (TextView) findViewById(R.id.coach_bio);
		acceptInvitation = (TextView) findViewById(R.id.accept_invitation);
		acceptInvitation.setText(getResources().getString(
				R.string.yes_accept_invitation));
		rejectInvitation = (TextView) findViewById(R.id.reject_invitation);
		rejectInvitation.setText(getResources().getString(
				R.string.no_decline_invitation));
		coachBio.setText(coachBioStr);

	}

	private void checkConnectivityForInvitationResponse(Context ctx,
			String userId, String password, String invitationId, String status) {

		if (Utilities.isConnectingToInternet(ctx.getApplicationContext())) {

			new OnboardingNetworkingServices(ctx)
					.requestInvitationResponseService(ctx, userId, password,
							invitationId, status, this,
							ServiceKeys.INVITATION_RESPONSE_SERVICE);

		} else {
			Utilities.showErrorToast(
					getResources().getString(R.string.internet_connection),
					this);
		}

	}

	@Override
	public void onBackPressed() {

		System.out.println("onBackPressed");
		Intent intent = new Intent();
		setResult(RESULT_CANCELED, intent);
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