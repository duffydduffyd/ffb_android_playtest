package com.foodfeedback.coaches;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class CoachDeleteActivity extends Activity implements OnClickListener {
	private LocalyticsSession localyticsSession;

	private TextView coachBio, removeDialog, requestRemoveCoach;
	private LinearLayout leftButton, rightButton;
	private TextView centreTitleTxt;
	public String coachNameStr, coachBioStr, userThumbUrl, userProfileUrl;
	public int userId;
	private RelativeLayout removeCoach;
	private RelativeLayout removeCoachLayout;
	private Animation slideUpInAnim;
	private TextView txttextView2;
	private ImageView layoutProfileBg;
	private ImageView userThumbImage;
	ImageLoader imgLoader;
	int loader = R.drawable.loader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.coach_delete);
		setResult(RESULT_CANCELED);

		imgLoader = new ImageLoader(getApplicationContext());
		Bundle bundle = getIntent().getExtras();
		coachNameStr = bundle.getString("coachName");
		coachBioStr = bundle.getString("coachBio");
		userId = bundle.getInt("userID");
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
			imgLoader.DisplayImage(userProfileUrl, R.drawable.placeholder_bio, layoutProfileBg,true);
		}
		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources

		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("Coaches");

		this.localyticsSession.upload(); // upload any data

	}

	private void addOnClickListeners() {
		removeCoach.setOnClickListener(this);
		removeDialog.setOnClickListener(this);
		requestRemoveCoach.setOnClickListener(this);
		leftButton.setOnClickListener(this);
	}

	private void setupFonts() {
		Typeface tfSpecial,tfNormal;
		tfSpecial = Typeface.createFromAsset(this.getAssets(),
				this.getResources().getString(R.string.app_font_style_medium));
		tfNormal = Typeface.createFromAsset(this.getAssets(),
				this.getResources().getString(R.string.font_standard));
		centreTitleTxt.setTypeface(tfSpecial);
		coachBio.setTypeface(tfNormal);
		txttextView2.setTypeface(tfSpecial);
	}

	private void setUpViewIds() {

		removeCoach = (RelativeLayout) findViewById(R.id.remove_coach);
		removeCoachLayout = (RelativeLayout) findViewById(R.id.dialog_delete);

		centreTitleTxt = (TextView) findViewById(R.id.center_txt);
		centreTitleTxt.setText(coachNameStr);
		txttextView2 = (TextView) findViewById(R.id.textView2);
		leftButton = (LinearLayout) findViewById(R.id.leftbutton);

		rightButton = (LinearLayout) findViewById(R.id.rightbutton);
		rightButton.setVisibility(View.GONE);
		coachBio = (TextView) findViewById(R.id.coach_bio);
		coachBio.setText(coachBioStr);

		requestRemoveCoach = (TextView) findViewById(R.id.request_removecoach);
		removeDialog = (TextView) findViewById(R.id.removedialog);
		removeDialog.setText(getResources().getString(R.string.dialog_cancel));
		layoutProfileBg = (ImageView) findViewById(R.id.layout_photobg);
		userThumbImage = (ImageView) findViewById(R.id.layout_photoprofile);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.remove_coach:
			// show animated dialog.
			slideUpInAnim = AnimationUtils.loadAnimation(this,
					R.anim.slide_upin_fast);
			removeCoachLayout.setVisibility(View.VISIBLE);
			removeCoachLayout.startAnimation(slideUpInAnim);

			break;

		case R.id.leftbutton:
			Intent intent = new Intent();
			setResult(RESULT_CANCELED, intent);
			finish();
			break;
		case R.id.removedialog:
			removeCoachLayout.setVisibility(View.GONE);
			break;
		case R.id.request_removecoach:

			AccountOperations accountOperationsObj = null;
			try {
				accountOperationsObj = LoginDetailsCacheManager
						.getObject(Controller.getAppBackgroundContext());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (accountOperationsObj != null
					&& accountOperationsObj.getData() != null) {
				try {
					checkConnectivityForRemoveCoach(
							v.getContext(),
							Integer.toString(accountOperationsObj.getData()
									.getUser().getId()),
							UserDetailsCacheManager.getObject(
									Controller.getAppBackgroundContext())
									.getPassword(), Integer.toString(userId));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			break;
		}
	}

	private void checkConnectivityForRemoveCoach(Context ctx, String userId,
			String password, String coachId) {

		if (Utilities.isConnectingToInternet(ctx.getApplicationContext())) {

			new OnboardingNetworkingServices(ctx)
					.requestRemoveCoachService(ctx, userId, password, coachId,
							this, ServiceKeys.REMOVE_COACH_SERVICE);

		} else {
			Utilities.showErrorToast(
					getResources().getString(R.string.internet_connection),
					this);
		}

	}

	@Override
	public void onBackPressed() {

		// System.out.println("onBackPressed");
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
