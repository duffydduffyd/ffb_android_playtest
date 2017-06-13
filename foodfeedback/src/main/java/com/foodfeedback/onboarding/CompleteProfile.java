package com.foodfeedback.onboarding;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.networking.OnboardingNetworkingServices;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.AccountOperations;
import com.foodfeedback.valueobjects.UserDetails;
import com.google.analytics.tracking.android.EasyTracker;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.localytics.android.LocalyticsSession;

public class CompleteProfile extends Activity implements OnClickListener,
		ImageChooserListener {
	private LocalyticsSession localyticsSession;
	private EditText foodInterestEditField;
	private ImageView profileImageView;
	private Button continueButton;
	private TextView centreTitleTxt, labelTxt, profileImageTxt,
			foodInterestTxt;
	private LinearLayout leftButton, rightButton;
	private RelativeLayout hideKeyword;
	private static final int COMPLETE_PICTURE = 2;
	private String selectedImagePath = null;

	private int chooserType;
	private ImageChooserManager imageChooserManager;
	private String filePath;

	private RelativeLayout profilePhoto;
	private RelativeLayout profileImageLayout;
	private TextView takePhotoButton, choosePhotoButton, cancelBtn;
	private Animation slideUpInAnim;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.complete_profile);

		setUpViewIds();
		setupFonts();
		addOnClickListeners();
		try {
			loginSuccess();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources

		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("New User Registeration");

		this.localyticsSession.upload(); // upload any data

	}

	private void loginSuccess() throws Exception {

		UserDetails userLoginDetails = new UserDetails();
		UserDetails checkUserDetails = UserDetailsCacheManager
				.getObject(Controller.getAppBackgroundContext());
		userLoginDetails.setCameraRoll(checkUserDetails.getCameraRoll());
		userLoginDetails.setUserID(checkUserDetails.getUserID());
		userLoginDetails.setPassword(checkUserDetails.getPassword());
		userLoginDetails.setCheckOutLogin("login_success");
		UserDetailsCacheManager.saveObject(
				Controller.getAppBackgroundContext(), null);
		UserDetailsCacheManager.saveObject(
				Controller.getAppBackgroundContext(), userLoginDetails);
		userLoginDetails = null;
	}

	private void addOnClickListeners() {

		profileImageLayout.setOnClickListener(this);

		takePhotoButton.setOnClickListener(this);
		choosePhotoButton.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);

		// profileImageView.setOnClickListener(this);
		continueButton.setOnClickListener(this);
		hideKeyword.setOnClickListener(this);
		foodInterestEditField.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				validateField();
			}
		});
	}

	private void validateField() {
		if (foodInterestEditField.getText().toString().trim().length() != 0
				&& foodInterestEditField.getText().toString().trim().length() >= 3) {
			continueButton.setBackgroundResource(R.drawable.redbar_button);
			continueButton.setEnabled(true);
		} else {
			continueButton.setBackgroundResource(R.drawable.unfocus_redimage);
			continueButton.setEnabled(false);
		}
	}

	private void setupFonts() {
		Typeface tfSpecial = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.app_font_style_medium));
		Typeface tfNormal = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.font_standard));
		centreTitleTxt.setTypeface(tfSpecial);
		labelTxt.setTypeface(tfNormal);
		profileImageTxt.setTypeface(tfNormal);
		foodInterestTxt.setTypeface(tfNormal);
		continueButton.setTypeface(tfSpecial);
	}

	private void setUpViewIds() {

		profileImageLayout = (RelativeLayout) findViewById(R.id.profileimage_layout);
		profilePhoto = (RelativeLayout) findViewById(R.id.profilePhoto);

		takePhotoButton = (TextView) findViewById(R.id.takePhoto);
		choosePhotoButton = (TextView) findViewById(R.id.chooseLibrary);
		cancelBtn = (TextView) findViewById(R.id.cancelBtn);

		hideKeyword = (RelativeLayout) findViewById(R.id.hidekeyboard);
		foodInterestEditField = (EditText) findViewById(R.id.food_interests);

		labelTxt = (TextView) findViewById(R.id.labelTxt);
		profileImageTxt = (TextView) findViewById(R.id.profileimage_txt);
		foodInterestTxt = (TextView) findViewById(R.id.foodinterests_txt);

		foodInterestEditField = (EditText) findViewById(R.id.food_interests);
		profileImageView = (ImageView) findViewById(R.id.profile_image);
		continueButton = (Button) findViewById(R.id.continue_profile);
		continueButton.setBackgroundResource(R.drawable.unfocus_redimage);
		continueButton.setEnabled(false);
		centreTitleTxt = (TextView) findViewById(R.id.center_txt);
		centreTitleTxt.setText(getResources().getString(
				R.string.complete_your_profile));
		leftButton = (LinearLayout) findViewById(R.id.leftbutton);
		leftButton.setVisibility(View.GONE);
		rightButton = (LinearLayout) findViewById(R.id.rightbutton);
		rightButton.setVisibility(View.GONE);
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

	@Override
	public void onError(final String reason) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// pbar.setVisibility(View.GONE);
				Utilities.showToast(reason, CompleteProfile.this);
				
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
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.profileimage_layout:

			slideUpInAnim = AnimationUtils.loadAnimation(this,
					R.anim.slide_upin_fast);
			profilePhoto.setVisibility(View.VISIBLE);
			profilePhoto.startAnimation(slideUpInAnim);

			break;

		case R.id.takePhoto:
			takePicture();
			break;

		case R.id.chooseLibrary:
			chooseImage();
			break;

		case R.id.cancelBtn:
			profilePhoto.setVisibility(View.GONE);
			break;

		/*
		 * case R.id.profile_image:
		 * 
		 * Intent intent = new Intent( Intent.ACTION_PICK,
		 * android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //
		 * intent.setType("image/*"); //
		 * intent.setAction(Intent.ACTION_GET_CONTENT); startActivityForResult(
		 * Intent.createChooser(intent, "Select Picture"), COMPLETE_PICTURE);
		 * 
		 * break;
		 */

		case R.id.food_interests:
			// finish();
			break;

		case R.id.continue_profile:

			/*
			 * if(TransferObject.getInteractedWith() != null &&
			 * TransferObject.getInteractedWith().getData() != null &&
			 * TransferObject.getInteractedWith().getData().size() > 0){ test }
			 */
			AccountOperations accountOperationsObj = null;
			try {
				accountOperationsObj = LoginDetailsCacheManager
						.getObject(Controller.getAppBackgroundContext());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (accountOperationsObj != null
					&& accountOperationsObj.getData() != null) {

				ServiceKeys.PROFILE_UPDATE = ServiceKeys.PROFILE_UPDATE_REGISTRATION;

				try {
					checkConnectivityForUpdateAccountService(
							v.getContext(),
							accountOperationsObj.getData().getUser().getId(),
							UserDetailsCacheManager.getObject(
									Controller.getAppBackgroundContext())
									.getPassword(), accountOperationsObj
									.getData().getUser().getFirstName(),
							accountOperationsObj.getData().getUser()
									.getLastName(), accountOperationsObj
									.getData().getUser().getEmail(),
							foodInterestEditField.getText().toString(),
							accountOperationsObj.getData().getUser()
									.getPrivateCoach(), accountOperationsObj
									.getData().getUser().getCoach(),
							accountOperationsObj.getData().getUser()
									.getNotificationRemenderWeek(),
							selectedImagePath);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// System.out.println(" 1111111 coming ");
				// checkConnectivityForQuestionnarie(v.getContext(),
				// LoginDetailsCacheManager.getObject(MyApplication.getAppBackgroundContext()).getData().getUser().getId(),
				// TransferObject.getLoginPassword());
			} else {
				Utilities.showToast(v.getContext().getResources().getString(
						R.string.no_user_error), v.getContext());
				
			}

			// LoginDetailsCacheManager.getObject(MyApplication.getAppBackgroundContext()).getData().getUser().getCompletedQuestionnaire());
			// System.out.println(" 222222222 coming ");
			break;
		case R.id.hidekeyboard:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(foodInterestEditField.getWindowToken(),
					0);
			break;

		}
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
