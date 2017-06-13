package com.foodfeedback.photo;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodfeedback.cachemanager.InteractedWithCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.networking.GalleryNetworkingServices;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.InteractedWith;
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class MultipleShare extends Activity implements OnClickListener {
	private LocalyticsSession localyticsSession;
	// private LinearLayout scrollCoachesList;
	private LinearLayout leftButton;
	private Button continueButton;
	private RelativeLayout sendToMyFood;
	private TextView centerTxt,Sendtomyfoodnotsharing_txt;
	private LinearLayout rightButton;
	String[] selectedIDsOfCoachesArray;
	int[] allIds;
	int loader = R.drawable.loader;
	int count = 0;
	private String imageSharePath, selectedIdTxt;
	private final static String TAG_SAVE_WITHOUT_SHARING = "Image added to gallery only";
	private final static String TAG_IMAGE_UPLOADED = "Image Uploaded";
	static final String TAG_CARRIER = "Carrier";
	static final String TAG_CONNECTION_TYPE = "Connection Type";
	Typeface tfNormal, tfSpecial;
	private ListView allCoachesListView;
	private MultiCoachListAdapter multiCoachListAdapter;
	InteractedWith interactedWithObj;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.select_coach);

		tfNormal =Typeface.createFromAsset(
				this.getAssets(),
				this.getResources().getString(R.string.font_standard));
		tfSpecial = Typeface.createFromAsset(
				this.getAssets(),
				this.getResources().getString(R.string.app_font_style_medium));
		
		
		
		Bundle bundle = getIntent().getExtras();
		imageSharePath = bundle.getString("imagepath");
		selectedIdTxt = bundle.getString("SelectedId_txt");
		selectedIDsOfCoachesArray = selectedIdTxt.split(",");

		allCoachesListView = (ListView) findViewById(R.id.coaches_listView);
		setUpViews();
		updateCoaches();

		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources

		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("All others");
		this.localyticsSession.upload(); // upload any datat
	}

	private void setUpViews() {

		leftButton = (LinearLayout) findViewById(R.id.leftbutton);
		leftButton.setOnClickListener(this);
		rightButton = (LinearLayout) findViewById(R.id.rightbutton);
		rightButton.setVisibility(View.GONE);
		centerTxt = (TextView) findViewById(R.id.center_txt);
		centerTxt.setText(getResources().getString(R.string.select_coaches));
		continueButton = (Button) findViewById(R.id.btnContinue);
		continueButton.setOnClickListener(this);
		continueButton.setBackgroundResource(R.drawable.unfocus_redimage);
		// continueButton.setEnabled(false);
		sendToMyFood = (RelativeLayout) findViewById(R.id.Sendtomyfoodnotsharing);
		sendToMyFood.setOnClickListener(this);
		Sendtomyfoodnotsharing_txt =  (TextView) findViewById(R.id.Sendtomyfoodnotsharing_txt);
		
		centerTxt.setTypeface(tfSpecial);
		continueButton.setTypeface(tfSpecial);
		Sendtomyfoodnotsharing_txt.setTypeface(tfSpecial);
		
	}

	private void updateCoaches() {
		try {
			interactedWithObj = InteractedWithCacheManager.getObject(Controller
					.getAppBackgroundContext());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (interactedWithObj != null && interactedWithObj.getData() != null
				&& interactedWithObj.getData().size() > 0) {

			multiCoachListAdapter = new MultiCoachListAdapter(this,
					interactedWithObj.getData(), continueButton, leftButton,
					selectedIDsOfCoachesArray);
			allCoachesListView.setAdapter(multiCoachListAdapter);
		}

	}

	@Override
	public void onBackPressed() {

		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.Sendtomyfoodnotsharing:
			try {
				checkConnectivityForAddImageGallery(
						v.getContext(),
						UserDetailsCacheManager.getObject(
								Controller.getAppBackgroundContext())
								.getUserID(),
						UserDetailsCacheManager.getObject(
								Controller.getAppBackgroundContext())
								.getPassword(), imageSharePath);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				System.out.println("Localytics save without sending");
				this.localyticsSession
						.tagEvent(MultipleShare.TAG_SAVE_WITHOUT_SHARING);

				// Uploaded image properties - Localytics
				TelephonyManager manager = (TelephonyManager) this
						.getSystemService(this.TELEPHONY_SERVICE);
				String carrierName = manager.getNetworkOperatorName();
				Map value_image_Uploaded = new HashMap();
				ConnectivityManager cm = (ConnectivityManager) this
						.getSystemService(this.CONNECTIVITY_SERVICE);
				String connection_type;
				if (cm.getActiveNetworkInfo() != null)
					connection_type = cm.getActiveNetworkInfo().getTypeName();
				else
					connection_type = "NO ACTIVE CONNECTION";
				value_image_Uploaded.put(TAG_CARRIER, carrierName);
				value_image_Uploaded.put(TAG_CONNECTION_TYPE, connection_type);
				this.localyticsSession
						.tagEvent(MultipleShare.TAG_IMAGE_UPLOADED);

			} catch (Exception ex) {
				ex.printStackTrace();
			}

			break;

		default:
			break;
		}
	}

	private void checkConnectivityForAddImageGallery(Context ctx,
			String userId, String password, String imagePath) {

		if (Utilities.isConnectingToInternet(ctx.getApplicationContext())) {

			GalleryNetworkingServices.getInstance()
					.requestAddImageGalleryService(ctx, userId, password,
							imagePath, this,
							ServiceKeys.ADD_IMAGE_GALLERY_SERVICE);

		} else {
			Utilities.showErrorToast(
					getResources().getString(R.string.internet_connection),
					this);
		}

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
