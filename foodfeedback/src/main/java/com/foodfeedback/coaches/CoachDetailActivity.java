package com.foodfeedback.coaches;

import android.app.Activity;
import android.content.Context;
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
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class CoachDetailActivity extends Activity {
	private LocalyticsSession localyticsSession;
	private TextView coachBio;
	private LinearLayout leftButton, rightButton;
	private TextView centreTitleTxt;
	private TextView txtAcceptInvitation;
	private TextView txtRejectInvitation;
	public String coachID, coachBioStr, userThumbUrl, userProfileUrl;
	public static String coachName;
	public int userId;
	private RelativeLayout goodMatch, notForMe;
	String password;
	private ImageView layoutProfileBg;
	private ImageView userThumbImage;
	int loader = R.drawable.loader;

	public CoachDetailActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.coaches_detail_screen);

		Bundle bundle = getIntent().getExtras();

		coachName = bundle.getString("coachName");
		coachBioStr = bundle.getString("coachBio");
		userId = bundle.getInt("userId");
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
		this.localyticsSession.tagScreen("Coach Detail");

		this.localyticsSession.upload();
	}

	private void addOnClickListeners() {

	}

	private void setupFonts() {
		
		Typeface tfSpecial,tfNormal;
		tfSpecial = Typeface.createFromAsset(this.getAssets(),
				this.getResources().getString(R.string.app_font_style_medium));
		tfNormal = Typeface.createFromAsset(this.getAssets(),
				this.getResources().getString(R.string.font_standard));
		centreTitleTxt.setTypeface(tfSpecial);
		txtAcceptInvitation.setTypeface(tfSpecial);
		txtRejectInvitation.setTypeface(tfSpecial);
		coachBio.setTypeface(tfNormal);
	}

	private void setUpViewIds() {

		goodMatch = (RelativeLayout) findViewById(R.id.goodmatch);
		notForMe = (RelativeLayout) findViewById(R.id.notforme);
		layoutProfileBg = (ImageView) findViewById(R.id.layout_photobg);
		userThumbImage = (ImageView) findViewById(R.id.layout_photoprofile);

		txtAcceptInvitation = (TextView) findViewById(R.id.accept_invitation);
		txtRejectInvitation = (TextView) findViewById(R.id.reject_invitation);

		notForMe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		goodMatch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				try {
					checkConnectivityForInviteCoach(
							v.getContext(),
							Integer.toString(LoginDetailsCacheManager
									.getObject(
											Controller
													.getAppBackgroundContext())
									.getData().getUser().getId()),
							UserDetailsCacheManager.getObject(
									Controller.getAppBackgroundContext())
									.getPassword(), Integer.toString(userId));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		centreTitleTxt = (TextView) findViewById(R.id.center_txt);
		centreTitleTxt.setText(coachName);
		leftButton = (LinearLayout) findViewById(R.id.leftbutton);
		leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		rightButton = (LinearLayout) findViewById(R.id.rightbutton);
		rightButton.setVisibility(View.GONE);
		coachBio = (TextView) findViewById(R.id.coach_bio);
		coachBio.setText(coachBioStr);
	}

	private void checkConnectivityForInviteCoach(Context ctx, String userId,
			String password, String coachId) {

		if (Utilities.isConnectingToInternet(ctx.getApplicationContext())) {

			new OnboardingNetworkingServices(ctx)
					.requestInviteCoachService(ctx, userId, password, coachId,
							this, ServiceKeys.INVITE_COACH_SERVICE);

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
