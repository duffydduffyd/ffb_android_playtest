package com.foodfeedback.photo;

import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodfeedback.cachemanager.InteractedWithCacheManager;
import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.cachemanager.UserPreferencesCacheManager;
import com.foodfeedback.myfood.ImageLoader;
import com.foodfeedback.networking.MessagesNetworkingServices;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.InteractedWith;
import com.foodfeedback.valueobjects.UserPreferences;
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class ShareTakePicture extends Activity implements OnClickListener {

	private LocalyticsSession localyticsSession;
	private TextView shareTakePhotoButton;
	private EditText initialMessage;
	private RelativeLayout coachItemName;
	LinearLayout shareButtonLayout,allContentLayout;
	private TextView coachNameShare, share_withtitle, foodPhotoComments,
			backButton;
	private String imageSharePath;
	private LinearLayout leftButton;
	private TextView txtTitle;
	InteractedWith interactedWithObj;
	private ImageView coachImage;
	int loader = R.drawable.loader;
	private static final int MULTISHARE = 55;
	Bundle bundle;
	String selectedIdtxt = "";
	private final static String TAG_Message_SENT = "Message sent";
	private final static String TAG_IMAGE_UPLOADED = "Image Uploaded";
	static final String TAG_MESSAGE_WITH_IMAGE = "with image";
	static final String TAG_CARRIER = "Carrier";
	static final String TAG_CONNECTION_TYPE = "Connection Type";
	ImageLoader imageLoader;
	Typeface tfNormal, tfSpecial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.share_foodpicture);
		tfNormal = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.font_standard));
		tfSpecial = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.app_font_style_medium));

		Bundle bundle = getIntent().getExtras();
		imageSharePath = bundle.getString("imagepath");
		selectedIdtxt = bundle.getString("SelectedId_txt");
		imageLoader = new ImageLoader(getApplicationContext());
		// Set up views
		coachImage = (ImageView) findViewById(R.id.coach_image);
		coachItemName = (RelativeLayout) findViewById(R.id.share_fooditem);
		leftButton = (LinearLayout) findViewById(R.id.leftbutton_share);
		backButton = (TextView) findViewById(R.id.backButton);
		shareTakePhotoButton = (TextView) findViewById(R.id.share_button);
		allContentLayout= (LinearLayout) findViewById(R.id.allContentLayout);
		
		setBackground();
		
	   
		shareButtonLayout = (LinearLayout) findViewById(R.id.shareButtonLayout);
		initialMessage = (EditText) findViewById(R.id.initial_message);
		coachNameShare = (TextView) findViewById(R.id.share_coach_name);
		share_withtitle = (TextView) findViewById(R.id.share_withtitle);
		foodPhotoComments = (TextView) findViewById(R.id.foodPhotoComments);
		txtTitle = (TextView) findViewById(R.id.center_txt);

		shareTakePhotoButton.setTypeface(tfSpecial);
		txtTitle.setTypeface(tfSpecial);
		share_withtitle.setTypeface(tfSpecial);
		coachNameShare.setTypeface(tfNormal);
		foodPhotoComments.setTypeface(tfSpecial);
		initialMessage.setTypeface(tfNormal);
		backButton.setTypeface(tfSpecial);

		shareButtonLayout.setOnClickListener(this);
		leftButton.setOnClickListener(this);
		coachItemName.setOnClickListener(this);
		backButton.setOnClickListener(this);
		if (selectedIdtxt != null && !selectedIdtxt.equals("")) {
			// received a set of selected IDs - make sure you save them
			UserPreferences fetchUserPreference;
			try {
				fetchUserPreference = UserPreferencesCacheManager
						.getObject(Controller.getAppBackgroundContext());
				if (fetchUserPreference == null) {
					fetchUserPreference = new UserPreferences();
				}
				fetchUserPreference.setPreviouslySelectedCoaches(selectedIdtxt);
				UserPreferencesCacheManager.saveObject(
						Controller.getAppBackgroundContext(),
						fetchUserPreference);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else {
			UserPreferences fetchUserPreference;
			try {
				fetchUserPreference = UserPreferencesCacheManager
						.getObject(Controller.getAppBackgroundContext());
				if (fetchUserPreference != null) {
					selectedIdtxt = fetchUserPreference
							.getPreviouslySelectedCoaches();
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext());
		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("Share Photo");
		this.localyticsSession.upload(); // upload any data

		try {
			interactedWithObj = InteractedWithCacheManager.getObject(Controller
					.getAppBackgroundContext());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (interactedWithObj != null && interactedWithObj.getData() != null
				&& interactedWithObj.getData().size() > 0) {
			if (selectedIdtxt == null)
				selectedIdtxt = "";
			if (selectedIdtxt.equals("")) {
				System.out.println("No Coaches are already selected ");
				// Set the coach name with the first record
				coachNameShare.setText(interactedWithObj.getData().get(0)
						.getFirst_name());
				selectedIdtxt = String.valueOf(interactedWithObj.getData()
						.get(0).getId());
				// If profile image is available for the first record then go
				// ahead and add this
				if (!interactedWithObj.getData().get(0)
						.getProfile_image_thumb_url().toString().equals("")) {
					imageLoader.DisplayImage(interactedWithObj.getData().get(0)
							.getProfile_image_thumb_url(), loader, coachImage,false);
					// Set the selected ID as the ID of the first record in my
					// list
					
				}
			} else {
				System.out
						.println("Coaches are already selected and the list = "
								+ selectedIdtxt);
				displaySelectedNames();
			}
		}
	}

	@TargetApi(16)
	public void setBackground(){
		if (Build.VERSION.SDK_INT >= 16){
			Bitmap myBitmap = BitmapFactory.decodeFile(imageSharePath);
			Drawable d = new BitmapDrawable(getResources(),Utilities.fastblur(myBitmap, 20));
			allContentLayout.setBackground(d);
		}
	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.share_fooditem:

			Intent intent = new Intent(this, MultipleShare.class);
			intent.putExtra("imagepath", imageSharePath);
			intent.putExtra("SelectedId_txt", selectedIdtxt);
			startActivityForResult(intent, MULTISHARE);
			break;

		case R.id.shareButtonLayout:

			if (!selectedIdtxt.equals("")) {
				try {
					checkConnectivityForSendMessage(
							v.getContext(),
							Integer.toString(LoginDetailsCacheManager
									.getObject(
											Controller
													.getAppBackgroundContext())
									.getData().getUser().getId()),
							UserDetailsCacheManager.getObject(
									Controller.getAppBackgroundContext())
									.getPassword(), selectedIdtxt,
							initialMessage.getText().toString(), imageSharePath);

				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					System.out.println("Localytics save with message");
					ConnectivityManager cm = (ConnectivityManager) this
							.getSystemService(Context.CONNECTIVITY_SERVICE);
					Map<String, String> values = new HashMap<String, String>();
					values.put(TAG_MESSAGE_WITH_IMAGE, "Yes");
					this.localyticsSession.tagEvent(TAG_Message_SENT, values);
					// Image Uploaded Locatilycs
					TelephonyManager manager = (TelephonyManager) this
							.getSystemService(Context.TELEPHONY_SERVICE);
					String carrierName = manager.getNetworkOperatorName();
					Map<String, String> value_image_Uploaded = new HashMap<String, String>();
					String connection_type;
					if (cm.getActiveNetworkInfo() != null)
						connection_type = cm.getActiveNetworkInfo()
								.getTypeName();
					else
						connection_type = "NO ACTIVE CONNECTION";
					value_image_Uploaded.put(TAG_CARRIER, carrierName);
					value_image_Uploaded.put(TAG_CONNECTION_TYPE,
							connection_type);
					if (android.os.Build.VERSION.SDK_INT >= 11) {
						this.localyticsSession.tagEvent(TAG_IMAGE_UPLOADED,
								value_image_Uploaded);
						this.localyticsSession
								.tagEvent(ShareTakePicture.TAG_IMAGE_UPLOADED);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}

			break;

		case R.id.leftbutton_share:
			// finish();
			startActivity(new Intent(this, TakePhotoActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			break;
		case R.id.backButton:
			// finish();
			startActivity(new Intent(this, TakePhotoActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			break;
		}
	}

	private void displaySelectedNames() {
		String[] selectedIDsOfCoachesArray = selectedIdtxt.split(",");

		String namesOfCoachesToDisplay = "";
		boolean updatedPhoto = false;

		coachImage.setImageResource(R.drawable.placeholder_bio);

		int countOfAdditionalCoaches = 0;

		for (int i = 0; i < selectedIDsOfCoachesArray.length; i++) {
			for (int j = 0; j < interactedWithObj.getData().size(); j++) {
				if (String.valueOf(interactedWithObj.getData().get(j).getId())
						.equals(selectedIDsOfCoachesArray[i])) {
					// MATCHED THE USER - TAKE THE FIRST NAME
					if (namesOfCoachesToDisplay.equals("")) {
						if (!updatedPhoto) {
							// Update the photo too
							if (interactedWithObj.getData().get(j)
									.getProfile_image_thumb_url() != null
									&& !interactedWithObj.getData().get(j)
											.getProfile_image_thumb_url()
											.equals("")) {
								imageLoader.DisplayImage(interactedWithObj
										.getData().get(j)
										.getProfile_image_thumb_url(), loader,
										coachImage,false);
								updatedPhoto = true;
							}

						}

						namesOfCoachesToDisplay = interactedWithObj.getData()
								.get(j).getFirst_name();
						selectedIdtxt = selectedIDsOfCoachesArray[i];
					} else {
						if (i <= 1) {
							// o - this is the first name
							// 1 - ths is the second name
							// 2 - this is the third name
							namesOfCoachesToDisplay = namesOfCoachesToDisplay
									+ ", "
									+ interactedWithObj.getData().get(j)
											.getFirst_name();
						} else {
							// Dont update the name of coaches. Just
							// increase the count of
							countOfAdditionalCoaches = countOfAdditionalCoaches + 1;
						}

						selectedIdtxt = selectedIdtxt + ","
								+ selectedIDsOfCoachesArray[i];
					}
				}
			}
		}
		if (namesOfCoachesToDisplay.equals("")) {

			coachNameShare.setText("Please select a coach");
		} else {
			if (countOfAdditionalCoaches > 0)
				coachNameShare.setText(namesOfCoachesToDisplay + " + "
						+ countOfAdditionalCoaches + " others");
			else
				coachNameShare.setText(namesOfCoachesToDisplay);

			if (selectedIdtxt != null && !selectedIdtxt.equals("")) {
				// received a set of selected IDs - make sure you save them
				UserPreferences fetchUserPreference;
				try {
					fetchUserPreference = UserPreferencesCacheManager
							.getObject(Controller.getAppBackgroundContext());
					if (fetchUserPreference == null) {
						fetchUserPreference = new UserPreferences();
					}
					fetchUserPreference
							.setPreviouslySelectedCoaches(selectedIdtxt);
					UserPreferencesCacheManager.saveObject(
							Controller.getAppBackgroundContext(),
							fetchUserPreference);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private void checkConnectivityForSendMessage(Context ctx, String userId,
			String password, String receiverId, String messageText,
			String imagePath) {

		if (Utilities.isConnectingToInternet(ctx.getApplicationContext())) {

			if (receiverId != null && !receiverId.trim().equals("")) {

				new MessagesNetworkingServices(ctx).requestSendMessageService(
						ctx, userId, password, receiverId, messageText,
						imagePath, ServiceKeys.SEND_MESSAGE_SERVICE);
			} else {
				Utilities.showErrorToast(
						getResources().getString(R.string.no_receiver), this);
			}

		} else {
			Utilities.showErrorToast(
					getResources().getString(R.string.internet_connection),
					this);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == MULTISHARE && resultCode == RESULT_OK) {

			if (data != null) {
				selectedIdtxt = data.getStringExtra("SelectedIds");
				displaySelectedNames();
			}

		} else if (requestCode == MULTISHARE && resultCode == RESULT_CANCELED) {
			// Log.i("User didn't take an image");
			// updateGallery();
		}
	}

	@Override
	public void onBackPressed() {

		System.out.println("onBackPressed");
		startActivity(new Intent(this, TakePhotoActivity.class)
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
