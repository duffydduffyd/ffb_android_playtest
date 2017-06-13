package com.foodfeedback.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.foodfeedback.cachemanager.CoachOrderingCacheManager;
import com.foodfeedback.cachemanager.CoachStatusNumberCacheManager;
import com.foodfeedback.cachemanager.FetchGalleryCacheManager;
import com.foodfeedback.cachemanager.FetchWeightCacheManager;
import com.foodfeedback.cachemanager.InteractedWithCacheManager;
import com.foodfeedback.cachemanager.InvitationsCacheManager;
import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.MessagesFromUserCacheManager;
import com.foodfeedback.cachemanager.UnReadCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.cachemanager.UserPreferencesCacheManager;
import com.foodfeedback.myfood.ImageLoader;
import com.foodfeedback.networking.OnboardingNetworkingServices;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.LoginActivity;
import com.foodfeedback.onboarding.MySwitch;
import com.foodfeedback.onboarding.MySwitch.OnChangeAttemptListener;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.utilities.RecommendAnimationHelper;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.AccountOperations;
import com.foodfeedback.valueobjects.UserDetails;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gcm.GCMRegistrar;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.localytics.android.LocalyticsSession;

public class SettingsActivity extends Activity implements OnClickListener,
		ImageChooserListener, OnChangeAttemptListener, OnCheckedChangeListener {
	private LocalyticsSession localyticsSession;

	// title
	private LinearLayout layoutRightHeader;
	private LinearLayout layoutLeftHeader;
	private TextView txtTitle, txtVersion;
	private RelativeLayout logoutDialog, profilePhoto, profileImageLayout;
	private TextView logoutOkButton, logoutCancelButton, takePhotoButton,
			choosePhotoButton, cancelBtn;
	private View viewTakePic;
	private ViewFlipper viewflipperSettings;
	private ViewFlipper viewflipperLegalAndPrivacy;

	// Settings
	private RelativeLayout layoutEditProfile;
	private RelativeLayout layoutPreferences;
	private RelativeLayout layoutAboutFoodFeedback;
	private RelativeLayout layoutInviteFriend;
	private RelativeLayout layoutSendFeedback;
	private RelativeLayout layoutLegalAndPrivacy;
	private RelativeLayout layoutLogout;

	// edit profile
	private EditText editFirstName;
	private EditText editLastName;
	private EditText editFoodIntGoals;
	private MySwitch togglebtnPrivateCoach;
	private TextView txt_privatecoach_desc,txt_profileimage,txt_firstname,txt_lastname,txt_foodinterestgoals,txt_privatecoach;
	private int togglePrivateCoach = 0, toggleSendWeekly = 0;
	private String selectedImagePath = null;
	int loader = R.drawable.loader;

	//About us
	TextView aboutUsText; 
	// preferences
	private MySwitch togglebtnSaveToCameraRoll;
	private MySwitch togglebtnSendWeeklyRemainders;
	private TextView txt_savetocamerarolldesc_desc,txt_savetocameraroll,txt_sendweeklyremainder,txt_sendweeklyremainderdesc;

	// legacy & privacy
	private RelativeLayout layoutTermsOfService;
	private RelativeLayout layoutPrivacyPolicy;
	TextView txt_termsofservice,txt_privacypolicy;
	private WebView webViewTermsOfService;
	private WebView webViewLegacyPolicy;

	private ImageView profileImageView;

	// button
	Button btnTemp;

	private int nMainIndex = 0;
	private int SETTINGS = 0;
	private int EDIT_PROFILE = 1;
	private int PREFERENCES = 2;
	private int ABOUT_FOOD_FEEDBACK = 3;
	private int LEGAL_AND_PRIVACY = 4;

	private int nSubIndex = 0;
	private int LEGAL_POLICY_MAIN = 0;
	private int TERMS_OF_SERVICE = 1;
	private int PRIVACY_POLICY = 2;

	private String sTermsOfServiceUrl = "file:///android_asset/terms_of_service.html";
	private String sPrivacyPolicyUrl = "file:///android_asset/privacypolicy.html";
	private Animation slideUpInAnim;
	AccountOperations accountOperationsObj;
	UserDetails userDetails;
	private String thumbUrl;
	UserDetails userDetailsObj;
	//Settings screen - text views
	TextView txt_edit_profile, txt_preferences_profile, txt_about_profile,
			txt_about_food_feedback, txt_invite_friend, txt_send_feedback,
			txt_legal_privacy, txt_logout;
	private ImageView imageLeftHeader;

	// choose picture

	private int chooserType;
	private ImageChooserManager imageChooserManager;
	private String filePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings_tab);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		try {
			accountOperationsObj = LoginDetailsCacheManager
					.getObject(Controller.getAppBackgroundContext());
			userDetails = UserDetailsCacheManager.getObject(Controller
					.getAppBackgroundContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setUpViewIds();
		setupFonts();
		addOnClickListeners();

		if (accountOperationsObj != null) {
			ImageLoader imgLoader = new ImageLoader(getApplicationContext());
			thumbUrl = accountOperationsObj.getData().getUser()
					.getProfileImageThumbUrl();
			if (!thumbUrl.equals("")) {
				imgLoader.DisplayImage(thumbUrl, loader, profileImageView,false);
			}
		}

		if (accountOperationsObj != null) {

			editFirstName.setText(""
					+ accountOperationsObj.getData().getUser().getFirstName());
			editLastName.setText(""
					+ accountOperationsObj.getData().getUser().getLastName());
			editFoodIntGoals.setText(""
					+ accountOperationsObj.getData().getUser().getBio());

			if (accountOperationsObj.getData().getUser().getPrivateCoach()
					.equals("0")) {
				togglebtnPrivateCoach.setText("");
				togglebtnPrivateCoach.setChecked(false);
				togglePrivateCoach = 0;
			} else if (accountOperationsObj.getData().getUser()
					.getPrivateCoach().equals("1")) {
				togglebtnPrivateCoach.setText("");
				togglebtnPrivateCoach.setChecked(true);
				togglePrivateCoach = 1;
			}

			if (accountOperationsObj.getData().getUser()
					.getNotificationRemenderWeek().equals("0")) {
				togglebtnSendWeeklyRemainders.setText("");
				togglebtnSendWeeklyRemainders.setChecked(false);
				toggleSendWeekly = 0;
			} else if (accountOperationsObj.getData().getUser()
					.getNotificationRemenderWeek().equals("1")) {
				togglebtnSendWeeklyRemainders.setText("");
				togglebtnSendWeeklyRemainders.setChecked(true);
				toggleSendWeekly = 1;
			}

			if (userDetails.getCameraRoll() == 0) {
				togglebtnSaveToCameraRoll.setText("");
				togglebtnSaveToCameraRoll.setChecked(false);
			} else if (userDetails.getCameraRoll() == 1) {
				togglebtnSaveToCameraRoll.setText("");
				togglebtnSaveToCameraRoll.setChecked(true);
			}

			/*
			 * if (userDetails.getSendWeekRemainder() == 0)
			 * togglebtnSendWeeklyRemainders.setText(""); else if
			 * (userDetails.getSendWeekRemainder() == 1)
			 * togglebtnSendWeeklyRemainders.setText("");
			 */

		}
		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources

		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("Settings");
		this.localyticsSession.upload(); // upload any data
	}

	private void setUpViewIds() {

		// title views
		layoutRightHeader = (LinearLayout) findViewById(R.id.rightbutton);
		layoutLeftHeader = (LinearLayout) findViewById(R.id.leftbutton);
		imageLeftHeader = (ImageView) findViewById(R.id.left_button);
		imageLeftHeader.setImageResource(R.drawable.close);
		txtTitle = (TextView) findViewById(R.id.center_txt);
		txtTitle.setText(getResources().getString(R.string.settings_title));
		txtVersion = (TextView) findViewById(R.id.txt_version);
		try {
			txtVersion
					.setText(getResources()
							.getString(R.string.settings_version)
							+ ": "
							+ getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		logoutDialog = (RelativeLayout) findViewById(R.id.logout);
		profileImageLayout = (RelativeLayout) findViewById(R.id.profileimage_layout);

		profilePhoto = (RelativeLayout) findViewById(R.id.profilePhoto);
		viewflipperSettings = (ViewFlipper) findViewById(R.id.viewFlipper_settingstab);
		viewflipperLegalAndPrivacy = (ViewFlipper) findViewById(R.id.viewFlipper_legal_and_privacy);

		logoutOkButton = (TextView) findViewById(R.id.logout_okbutton);
		logoutCancelButton = (TextView) findViewById(R.id.logout_cancelbutton);
		viewTakePic = (View) findViewById(R.id.takepicc);
		takePhotoButton = (TextView) findViewById(R.id.takePhoto);
		choosePhotoButton = (TextView) findViewById(R.id.chooseLibrary);
		cancelBtn = (TextView) findViewById(R.id.cancelBtn);

		// settings
		layoutEditProfile = (RelativeLayout) findViewById(R.id.edit_profile_layout);
		layoutPreferences = (RelativeLayout) findViewById(R.id.preferences_layout);
		layoutAboutFoodFeedback = (RelativeLayout) findViewById(R.id.about_food_feedback_layout);
		layoutInviteFriend = (RelativeLayout) findViewById(R.id.invite_friend_layout);
		layoutSendFeedback = (RelativeLayout) findViewById(R.id.send_feedback_layout);
		layoutLegalAndPrivacy = (RelativeLayout) findViewById(R.id.legal_privacy_layout);
		layoutLogout = (RelativeLayout) findViewById(R.id.logout_layout);

		txt_edit_profile = (TextView) findViewById(R.id.txt_edit_profile);
		txt_preferences_profile = (TextView) findViewById(R.id.txt_preferences_profile);
		txt_about_profile = (TextView) findViewById(R.id.txt_about_profile);
		txt_about_food_feedback = (TextView) findViewById(R.id.txt_about_food_feedback);
		txt_invite_friend = (TextView) findViewById(R.id.txt_invite_friend);
		txt_send_feedback = (TextView) findViewById(R.id.txt_send_feedback);
		txt_legal_privacy = (TextView) findViewById(R.id.txt_legal_privacy);
		txt_logout = (TextView) findViewById(R.id.txt_logout);
		
		
		
		

		// edit profile
		editFirstName = (EditText) findViewById(R.id.edit_firstname);
		editLastName = (EditText) findViewById(R.id.edit_lastname);
		editFoodIntGoals = (EditText) findViewById(R.id.edit_food_interests);
		togglebtnPrivateCoach = (MySwitch) findViewById(R.id.togglebutton_privatecoach);
		txt_privatecoach_desc = (TextView) findViewById(R.id.txt_privatecoach_desc);
		txt_profileimage = (TextView) findViewById(R.id.txt_profileimage);
		txt_firstname = (TextView) findViewById(R.id.txt_firstname);
		txt_lastname = (TextView) findViewById(R.id.txt_lastname);
		txt_foodinterestgoals = (TextView) findViewById(R.id.txt_foodinterestgoals);
		txt_privatecoach = (TextView) findViewById(R.id.txt_privatecoach);
		
		
		
		
		
		profileImageView = (ImageView) findViewById(R.id.imageViewThumb);
		// preferences
		togglebtnSaveToCameraRoll = (MySwitch) findViewById(R.id.togglebutton_savetocameraroll);
		togglebtnSendWeeklyRemainders = (MySwitch) findViewById(R.id.togglebutton_sendweeklyremainder);
		txt_savetocameraroll = (TextView)findViewById(R.id.txt_savetocameraroll);
		txt_savetocamerarolldesc_desc= (TextView)findViewById(R.id.txt_savetocamerarolldesc_desc);
		txt_sendweeklyremainder = (TextView)findViewById(R.id.txt_sendweeklyremainder);
		txt_sendweeklyremainderdesc = (TextView)findViewById(R.id.txt_sendweeklyremainderdesc);
		
		// AboutUs
		aboutUsText = (TextView) findViewById(R.id.aboutUsText);
		// legacy & privacy
		layoutTermsOfService = (RelativeLayout) findViewById(R.id.termsofservice_layout);
		layoutPrivacyPolicy = (RelativeLayout) findViewById(R.id.privacypolicy_layout);
		txt_privacypolicy = (TextView) findViewById(R.id.txt_privacypolicy);
		txt_termsofservice = (TextView) findViewById(R.id.txt_termsofservice);
		webViewLegacyPolicy = (WebView) findViewById(R.id.webViewLP);
		webViewTermsOfService = (WebView) findViewById(R.id.webViewTermsofservice);

		webViewLegacyPolicy.loadUrl(sPrivacyPolicyUrl);
		webViewTermsOfService.loadUrl(sTermsOfServiceUrl);

		layoutRightHeader.removeAllViewsInLayout();
		btnTemp = new Button(this);

		btnTemp.setTextColor(getResources().getColor(R.color.setting_buttontxt));
		btnTemp.setBackgroundColor(Color.TRANSPARENT);
		btnTemp.setTextSize(getResources().getDimension(R.dimen.temp_button));
		layoutRightHeader.addView(btnTemp);
		layoutRightHeader.setVisibility(View.GONE);
		layoutLeftHeader.setVisibility(View.VISIBLE);

	}

	private void setupFonts() {
		Typeface tfSpecial,tfNormal;
		tfSpecial = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.app_font_style_medium));
		tfNormal = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.font_standard));
		txtTitle.setTypeface(tfSpecial);
		txtVersion.setTypeface(tfNormal);
		btnTemp.setTypeface(tfNormal);
		
		//main Settings page
		txt_edit_profile.setTypeface(tfNormal);
		txt_preferences_profile.setTypeface(tfNormal);
		txt_about_profile.setTypeface(tfNormal);
		txt_about_food_feedback.setTypeface(tfNormal);
		txt_invite_friend.setTypeface(tfNormal);
		txt_send_feedback.setTypeface(tfNormal);
		txt_legal_privacy.setTypeface(tfNormal);
		txt_logout.setTypeface(tfNormal);
		
		//Edit profile
		editFirstName.setTypeface(tfNormal);
		editLastName.setTypeface(tfNormal);
		editFoodIntGoals.setTypeface(tfNormal);
		txt_privatecoach_desc.setTypeface(tfNormal);
		txt_profileimage.setTypeface(tfNormal,Typeface.BOLD);
		
		txt_firstname.setTypeface(tfNormal,Typeface.BOLD);
		txt_lastname.setTypeface(tfNormal,Typeface.BOLD);
		txt_foodinterestgoals.setTypeface(tfNormal,Typeface.BOLD);
		txt_privatecoach.setTypeface(tfNormal,Typeface.BOLD);
		
		// Preferences
		
		txt_savetocameraroll.setTypeface(tfNormal,Typeface.BOLD);
		txt_savetocamerarolldesc_desc.setTypeface(tfNormal);
		txt_sendweeklyremainder.setTypeface(tfNormal,Typeface.BOLD);
		txt_sendweeklyremainderdesc.setTypeface(tfNormal);
		
		
		//About us
		aboutUsText.setTypeface(tfNormal);
		
		//legal and Privacy
		txt_privacypolicy.setTypeface(tfNormal);
		txt_termsofservice.setTypeface(tfNormal);
		btnTemp.setTypeface(tfSpecial,Typeface.BOLD);
		
		
		
	}

	private void addOnClickListeners() {
		// title
		layoutRightHeader.setOnClickListener(this);
		layoutLeftHeader.setOnClickListener(this);

		// profileImageView.setOnClickListener(this);

		// settings
		profileImageLayout.setOnClickListener(this);
		layoutEditProfile.setOnClickListener(this);
		layoutPreferences.setOnClickListener(this);
		layoutAboutFoodFeedback.setOnClickListener(this);
		layoutInviteFriend.setOnClickListener(this);
		layoutSendFeedback.setOnClickListener(this);
		layoutLegalAndPrivacy.setOnClickListener(this);
		layoutLogout.setOnClickListener(this);

		// legacy & policy
		layoutTermsOfService.setOnClickListener(this);

		layoutPrivacyPolicy.setOnClickListener(this);
		;

		btnTemp.setOnClickListener(this);

		layoutPrivacyPolicy.setOnClickListener(this);

		logoutOkButton.setOnClickListener(this);
		logoutCancelButton.setOnClickListener(this);
		viewTakePic.setOnClickListener(this);
		takePhotoButton.setOnClickListener(this);
		choosePhotoButton.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);

		togglebtnSaveToCameraRoll.setOnCheckedChangeListener(this);
		togglebtnSendWeeklyRemainders.setOnCheckedChangeListener(this);
		togglebtnPrivateCoach.setOnCheckedChangeListener(this);
	}

	private void checkConnectivityForUpdateAccountService(Context ctx,
			int userID, String password, String firstName, String lastName,
			String email, String bio, String privateCoach, String coach,
			String notificationRemainderWeek, String profileImage) {

		if (Utilities.isConnectingToInternet(ctx.getApplicationContext())) {

			new OnboardingNetworkingServices(ctx).requestUpdateAccountService(
					ctx, userID, password, firstName, lastName, email, bio,
					coach, privateCoach, notificationRemainderWeek,
					profileImage, this, ServiceKeys.ACCOUNT_UPDATE_SERVICE);

		} else {
			Utilities.showErrorToast(
					getResources().getString(R.string.internet_connection),
					this);
		}

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {

		case R.id.rightbutton:
			System.out.println("clicked " + btnTemp.getText());
			// Toast.makeText(context, "Testing", duration)

			break;
		case R.id.leftbutton:
			if (nMainIndex == SETTINGS)
				finish();
			else {
				imageLeftHeader.setImageResource(R.drawable.close);
				if (nMainIndex == LEGAL_AND_PRIVACY) {
					if (nSubIndex == LEGAL_POLICY_MAIN) {
						nMainIndex = SETTINGS;
						flipUI();
					} else {
						nSubIndex = LEGAL_POLICY_MAIN;
						flipUI();
					}
				} else {
					nMainIndex = SETTINGS;
					flipUI();
				}
			}
			break;

		case R.id.edit_profile_layout:
			imageLeftHeader.setImageResource(R.drawable.backarrow);
			nMainIndex = EDIT_PROFILE;
			flipUI();
			break;
		case R.id.preferences_layout:
			imageLeftHeader.setImageResource(R.drawable.backarrow);
			nMainIndex = PREFERENCES;
			flipUI();
			break;
		case R.id.about_food_feedback_layout:
			imageLeftHeader.setImageResource(R.drawable.backarrow);
			nMainIndex = ABOUT_FOOD_FEEDBACK;
			flipUI();
			break;
		case R.id.invite_friend_layout:
			// sendEmail(getResources().getString(R.string.email_invite_friend_subject));
			sendMailInvite("Join Food Feedback!");
			break;
		case R.id.send_feedback_layout:
			// sendEmail(getResources().getString(R.string.email_feedback_subject));
			sendFeedback(getResources().getString(R.string.food_feedback));
			break;
		case R.id.legal_privacy_layout:
			imageLeftHeader.setImageResource(R.drawable.backarrow);
			nMainIndex = LEGAL_AND_PRIVACY;
			flipUI();
			break;
		case R.id.logout_layout:

			slideUpInAnim = AnimationUtils.loadAnimation(this,
					R.anim.slide_upin_fast);
			logoutDialog.setVisibility(View.VISIBLE);
			logoutDialog.startAnimation(slideUpInAnim);

			break;

		case R.id.profileimage_layout:

			slideUpInAnim = AnimationUtils.loadAnimation(this,
					R.anim.slide_upin_fast);
			profilePhoto.setVisibility(View.VISIBLE);
			profilePhoto.startAnimation(slideUpInAnim);

			break;

		case R.id.takePhoto:
			takePicture();
			break;

		case R.id.takepicc:
			takePicture();
			break;

		case R.id.chooseLibrary:
			chooseImage();
			break;

		case R.id.cancelBtn:
			profilePhoto.setVisibility(View.GONE);
			break;

		case R.id.logout_okbutton:

			new ImageLoader(getApplicationContext()).clearCache();
			try {
				CoachStatusNumberCacheManager.saveObject(
						Controller.getAppBackgroundContext(), null);
				System.out.println("logout_okbutton");

				LoginDetailsCacheManager.saveObject(
						Controller.getAppBackgroundContext(), null);
				FetchGalleryCacheManager.saveObject(
						Controller.getAppBackgroundContext(), null);
				// CoachListCacheManager.saveObject(
				// Controller.getAppBackgroundContext(), null);
				InteractedWithCacheManager.saveObject(
						Controller.getAppBackgroundContext(), null);
				InvitationsCacheManager.saveObject(
						Controller.getAppBackgroundContext(), null);
				MessagesFromUserCacheManager.saveObject(
						Controller.getAppBackgroundContext(), null, null);

				UnReadCacheManager.saveObject(
						Controller.getAppBackgroundContext(), null);
				FetchWeightCacheManager.saveObject(
						Controller.getAppBackgroundContext(), null);

				CoachOrderingCacheManager.saveObject(
						Controller.getAppBackgroundContext(), null);
				UserPreferencesCacheManager.saveObject(
						Controller.getAppBackgroundContext(), null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// BitmapCacheManager.saveObject(MyApplication.getAppBackgroundContext(),
			// null);

			GCMRegistrar.unregister(this);

			try {
				logoutSuccess();
			} catch (Exception e) {
				e.printStackTrace();
			}

			startActivity(new Intent(this, LoginActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			break;

		case R.id.logout_cancelbutton:
			logoutDialog.setVisibility(View.GONE);
			break;

		case R.id.termsofservice_layout:
			nSubIndex = TERMS_OF_SERVICE;
			flipUI();
			break;
		case R.id.privacypolicy_layout:
			nSubIndex = PRIVACY_POLICY;
			flipUI();
			break;

		default:
			if (arg0 instanceof Button) {
				Button tmp = (Button) arg0;
				// tmp.setWidth(LayoutParams.MATCH_PARENT);
				if (tmp.getText().equals(
						getResources()
								.getString(R.string.settings_profile_save))) {
					System.out.println("save clicked");

					// AccountOperations accountOperationsObj =
					// LoginDetailsCacheManager.getObject(MyApplication.getAppBackgroundContext());

					if (accountOperationsObj != null
							&& accountOperationsObj.getData() != null) {
						ServiceKeys.PROFILE_UPDATE = ServiceKeys.PROFILE_UPDATE_EDIT;
						checkConnectivityForUpdateAccountService(
								tmp.getContext(), accountOperationsObj
										.getData().getUser().getId(),
								userDetails.getPassword(), editFirstName
										.getText().toString(), editLastName
										.getText().toString(),
								accountOperationsObj.getData().getUser()
										.getEmail(), editFoodIntGoals.getText()
										.toString(),
								Integer.toString(togglePrivateCoach),
								accountOperationsObj.getData().getUser()
										.getCoach(),
								Integer.toString(toggleSendWeekly),
								selectedImagePath);
					}
					break;
				} else if (tmp.getText().equals(
						getResources().getString(R.string.settings_done))) {
					System.out.println("done clicked");
					nSubIndex = LEGAL_POLICY_MAIN;
					flipUI();
				}
			}
			break;
		}
	}

	private void takePicture() {
		chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
		imageChooserManager = new ImageChooserManager(this,
				ChooserType.REQUEST_CAPTURE_PICTURE, "myfolder", true);
		imageChooserManager.setImageChooserListener(this);
		try {
			// pbar.setVisibility(View.VISIBLE);
			filePath = imageChooserManager.choose();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void chooseImage() {

		chooserType = ChooserType.REQUEST_PICK_PICTURE;
		imageChooserManager = new ImageChooserManager(this,
				ChooserType.REQUEST_PICK_PICTURE, "myfolder", true);
		imageChooserManager.setImageChooserListener(this);
		try {
			// pbar.setVisibility(View.VISIBLE);
			filePath = imageChooserManager.choose();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK
				&& (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
			if (imageChooserManager == null) {
				reinitializeImageChooser();
			}
			imageChooserManager.submit(requestCode, data);
		} else {
			// pbar.setVisibility(View.GONE);
		}
	}

	// Should be called if for some reason the ImageChooserManager is null (Due
	// to destroying of activity for low memory situations)
	private void reinitializeImageChooser() {
		imageChooserManager = new ImageChooserManager(this, chooserType,
				"myfolder", true);
		imageChooserManager.setImageChooserListener(this);
		imageChooserManager.reinitialize(filePath);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("chooser_type", chooserType);
		outState.putString("media_path", filePath);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("chooser_type")) {
				chooserType = savedInstanceState.getInt("chooser_type");
			}

			if (savedInstanceState.containsKey("media_path")) {
				filePath = savedInstanceState.getString("media_path");
			}
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	private void flipUI() {
		if (nMainIndex == SETTINGS) {
			txtTitle.setText(getResources().getString(R.string.settings_title));
			layoutRightHeader.setVisibility(View.GONE);
			layoutLeftHeader.setVisibility(View.VISIBLE);
			viewflipperSettings.setInAnimation(RecommendAnimationHelper
					.inFromRightAnimation());
			viewflipperSettings.setOutAnimation(RecommendAnimationHelper
					.outToLeftAnimation());
			viewflipperSettings.setDisplayedChild(nMainIndex);
			;
		} else if (nMainIndex == EDIT_PROFILE) {
			txtTitle.setText(getResources().getString(
					R.string.settings_edit_ur_profile));
			layoutRightHeader.setVisibility(View.VISIBLE);
			layoutLeftHeader.setVisibility(View.VISIBLE);
			btnTemp.setText(getResources().getString(
					R.string.settings_profile_save));
			viewflipperSettings.setInAnimation(RecommendAnimationHelper
					.inFromRightAnimation());
			viewflipperSettings.setOutAnimation(RecommendAnimationHelper
					.outToLeftAnimation());
			viewflipperSettings.setDisplayedChild(nMainIndex);
		} else if (nMainIndex == PREFERENCES) {
			txtTitle.setText(getResources().getString(
					R.string.settings_preferences));
			layoutRightHeader.setVisibility(View.GONE);
			layoutLeftHeader.setVisibility(View.VISIBLE);
			viewflipperSettings.setInAnimation(RecommendAnimationHelper
					.inFromRightAnimation());
			viewflipperSettings.setOutAnimation(RecommendAnimationHelper
					.outToLeftAnimation());
			viewflipperSettings.setDisplayedChild(nMainIndex);
		} else if (nMainIndex == ABOUT_FOOD_FEEDBACK) {
			txtTitle.setText(getResources().getString(
					R.string.settings_about_us));
			layoutRightHeader.setVisibility(View.GONE);
			layoutLeftHeader.setVisibility(View.VISIBLE);
			viewflipperSettings.setInAnimation(RecommendAnimationHelper
					.inFromRightAnimation());
			viewflipperSettings.setOutAnimation(RecommendAnimationHelper
					.outToLeftAnimation());
			viewflipperSettings.setDisplayedChild(nMainIndex);
		} else if (nMainIndex == LEGAL_AND_PRIVACY) {
			txtTitle.setText(getResources().getString(
					R.string.settings_legal_and_privacy));
			viewflipperSettings.setInAnimation(RecommendAnimationHelper
					.inFromRightAnimation());
			viewflipperSettings.setOutAnimation(RecommendAnimationHelper
					.outToLeftAnimation());
			viewflipperSettings.setDisplayedChild(nMainIndex);
			if (nSubIndex == LEGAL_POLICY_MAIN) {
				layoutRightHeader.setVisibility(View.GONE);
				layoutLeftHeader.setVisibility(View.VISIBLE);
				txtTitle.setText(getResources().getString(
						R.string.settings_legal_and_privacy));
				viewflipperLegalAndPrivacy
						.setInAnimation(RecommendAnimationHelper
								.inFromRightAnimation());
				viewflipperLegalAndPrivacy
						.setOutAnimation(RecommendAnimationHelper
								.outToLeftAnimation());
				viewflipperLegalAndPrivacy.setDisplayedChild(LEGAL_POLICY_MAIN);
			} else if (nSubIndex == TERMS_OF_SERVICE) {
				layoutRightHeader.setVisibility(View.VISIBLE);
				layoutLeftHeader.setVisibility(View.GONE);
				btnTemp.setText(getResources()
						.getString(R.string.settings_done));
				btnTemp.setTextSize(getResources().getDimension(
						R.dimen.temp_button));
				txtTitle.setText(getResources().getString(
						R.string.settings_terms_of_service));
				viewflipperLegalAndPrivacy
						.setInAnimation(RecommendAnimationHelper
								.inFromRightAnimation());
				viewflipperLegalAndPrivacy
						.setOutAnimation(RecommendAnimationHelper
								.outToLeftAnimation());
				viewflipperLegalAndPrivacy.setDisplayedChild(TERMS_OF_SERVICE);
			} else if (nSubIndex == PRIVACY_POLICY) {
				layoutRightHeader.setVisibility(View.VISIBLE);
				layoutLeftHeader.setVisibility(View.GONE);
				btnTemp.setText(getResources()
						.getString(R.string.settings_done));
				txtTitle.setText(getResources().getString(
						R.string.settings_privacy_policy));
				viewflipperLegalAndPrivacy
						.setInAnimation(RecommendAnimationHelper
								.inFromRightAnimation());
				viewflipperLegalAndPrivacy
						.setOutAnimation(RecommendAnimationHelper
								.outToLeftAnimation());
				viewflipperLegalAndPrivacy.setDisplayedChild(PRIVACY_POLICY);
			}
		}
	}

	private void sendFeedback(String mailBody) {

		Locale current = getResources().getConfiguration().locale;
		mailBody = mailBody
				.replace("##LOCALE##", "" + current.getDisplayName());
		mailBody = mailBody.replace("##OSVERSION##",
				android.os.Build.VERSION.SDK_INT + "");
		mailBody = mailBody.replace("##MODEL##", android.os.Build.MANUFACTURER
				+ " " + android.os.Build.MODEL);
		String mailSubject = getResources()
				.getString(R.string.feedback_subject);
		try {
			mailBody = mailBody
					.replace(
							"##APPVERSION##",
							getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName
									+ " (Build "
									+ getPackageManager().getPackageInfo(
											getPackageName(), 0).versionCode
									+ ")");
			mailSubject = mailSubject
					+ " (v"
					+ getPackageManager().getPackageInfo(getPackageName(), 0).versionName
					+ ")";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			mailBody = mailBody.replace("##APPVERSION##", "UNKNOWN");
		}

		Intent email = new Intent(Intent.ACTION_SEND);
		email.setType("plain/text");
		email.putExtra(Intent.EXTRA_EMAIL,
				new String[] { "feedback@foodfeedback.com" });
		email.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
		email.putExtra(Intent.EXTRA_TEXT, mailBody);
		startActivity(Intent.createChooser(email, "Choose an Email client :"));
	}

	private void sendMailInvite(String mailBody) {

		FileOutputStream outStream;
		File file;
		Bitmap bm = BitmapFactory.decodeResource(getResources(),
				R.drawable.send_feedback);
		String extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString();

		file = new File(extStorageDirectory, "ImageFood.PNG");
		try {
			outStream = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Intent email = new Intent(Intent.ACTION_SEND);
		email.setType("application/octet-stream");
		email.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
		email.putExtra(Intent.EXTRA_SUBJECT, mailBody);
		email.putExtra(Intent.EXTRA_TEXT,
				getResources().getString(R.string.invite_friend));
		email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		startActivity(Intent.createChooser(email, "Choose an Email client :"));
	}

	@Override
	public void onBackPressed() {

		System.out.println("onBackPressed");
		finish();
	}

	private void logoutSuccess() throws Exception {

		UserDetails userLoginDetails = new UserDetails();
		UserDetails checkUserDetails = UserDetailsCacheManager
				.getObject(Controller.getAppBackgroundContext());
		userLoginDetails.setCameraRoll(checkUserDetails.getCameraRoll());
		userLoginDetails.setUserID(checkUserDetails.getUserID());
		userLoginDetails.setPassword(checkUserDetails.getPassword());
		userLoginDetails.setCheckOutLogin("logout_success");
		UserDetailsCacheManager.saveObject(
				Controller.getAppBackgroundContext(), null);
		UserDetailsCacheManager.saveObject(
				Controller.getAppBackgroundContext(), userLoginDetails);
		userLoginDetails = null;

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
	public void onError(final String reason) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// pbar.setVisibility(View.GONE);
				Utilities.showToast(reason, SettingsActivity.this);
				
			}
		});
	}

	@Override
	public void onImageChosen(final ChosenImage image) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// pbar.setVisibility(View.GONE);
				if (image != null) {
					selectedImagePath = image.getFilePathOriginal().toString();
					profileImageView.setImageURI(Uri.parse(new File(image
							.getFileThumbnail()).toString()));
					profileImageView.setScaleType(ScaleType.CENTER_CROP);
					profilePhoto.setVisibility(View.GONE);
				}
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();

		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();

		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	@Override
	public void onChangeAttempted(boolean isChecked) {
		Log.d("FoodFeedback", "onChangeAttemped(checked = " + isChecked + ")");
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		switch (buttonView.getId()) {
		case R.id.togglebutton_savetocameraroll:
			Log.d("FoodFeedback", "savetoCamera(checked = " + isChecked + ")");
			userDetailsObj = new UserDetails();

			if (togglebtnSaveToCameraRoll.isChecked())
				userDetailsObj.setCameraRoll(1);
			else
				userDetailsObj.setCameraRoll(0);

			userDetailsObj.setUserID(userDetails.getUserID());
			userDetailsObj.setPassword(userDetails.getPassword());
			try {
				UserDetailsCacheManager.saveObject(
						Controller.getAppBackgroundContext(), null);
				UserDetailsCacheManager.saveObject(
						Controller.getAppBackgroundContext(), userDetailsObj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			userDetailsObj = null;
			// System.out.println(" settingTTEst getCameraRoll "+userDetails.getCameraRoll());

			break;

		case R.id.togglebutton_sendweeklyremainder:
			Log.d("FoodFeedback", "SendWeeklyreminder(checked = " + isChecked
					+ ")");
			if (togglebtnSendWeeklyRemainders.isChecked())
				toggleSendWeekly = 1;
			else
				toggleSendWeekly = 0;

			break;

		case R.id.togglebutton_privatecoach:

			if (togglebtnPrivateCoach.isChecked())
				togglePrivateCoach = 1;
			else
				togglePrivateCoach = 0;

			break;

		default:
			break;
		}
	}
}
