package com.foodfeedback.myfood;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foodfeedback.cachemanager.FetchGalleryCacheManager;
import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.networking.GalleryNetworkingServices;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.settings.SettingsActivity;
import com.foodfeedback.valueobjects.AccountOperations;
import com.foodfeedback.valueobjects.FetchGallery;
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class MyFoodActivity extends Activity implements OnClickListener,
		OnItemClickListener {
	private LocalyticsSession localyticsSession;

	private static final int UPDATE_GALLERY = 44;
	private LinearLayout leftButton, rightButton;
	private TextView centreTitleTxt;
	private ImageView rightButtonImage;
	GridView gridviewFoodGallery;
	String thumbPhotoUrl[];
	public TextView helpText;
	public ImageView helpArrow;
	FetchGallery fetchGalleryObj = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//				.detectDiskReads().detectDiskWrites().detectNetwork() // or
//																		// .detectAll()
//																		// for
//																		// all
//																		// detectable
//																		// problems
//				.penaltyLog().build());
//		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
//				.penaltyLog().penaltyDeath().build());

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.myfood_screen);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		setUpViewIds();
		addOnClickListeners();
		new Thread(getCachedData).start();
		
		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources

		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("My Food");

		this.localyticsSession.upload(); // upload any data

		new Thread(getlatestData).start();

	}

	Runnable getCachedData = new Runnable() {
		@Override
		public void run() {
			try {
				fetchGalleryObj = FetchGalleryCacheManager.getObject(Controller
						.getAppBackgroundContext());
				runOnUiThread(new Thread(new Runnable() {
					public void run() {
						updateGallery();
					}
				}));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};
	
	Runnable getlatestData = new Runnable() {
		@Override
		public void run() {
			try {
				AccountOperations accountObject = LoginDetailsCacheManager
						.getObject(getApplicationContext());
				GalleryNetworkingServices.getInstance()
						.fetchGalleryHttpGetRequest(
								Integer.toString(accountObject.getData()
										.getUser().getId()),
								UserDetailsCacheManager.getObject(
										Controller.getAppBackgroundContext())
										.getPassword(), "1");
				fetchGalleryObj = FetchGalleryCacheManager.getObject(Controller
						.getAppBackgroundContext());
				runOnUiThread(new Thread(new Runnable() {
					public void run() {
						updateGallery();
					}
				}));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPostCreate(Bundle icicle) {
		super.onPostCreate(icicle);
	}

	@Override
	protected void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.

	}

	private void updateGallery() {
		if (fetchGalleryObj != null && fetchGalleryObj.getData() != null
				&& fetchGalleryObj.getData().size() > 0) {

			helpArrow.setVisibility(View.GONE);
			helpText.setVisibility(View.GONE);
			thumbPhotoUrl = new String[fetchGalleryObj.getData().size()];
			gridviewFoodGallery.setAdapter(new ImageAdapter(this,
					fetchGalleryObj.getData()));
		} else {
			gridviewFoodGallery.setAdapter(null);
			setUpHelpUi();
		}

	}

	private void setUpHelpUi() {

		helpArrow.setVisibility(View.VISIBLE);
		helpText.setVisibility(View.VISIBLE);
	}

	private void addOnClickListeners() {

		gridviewFoodGallery.setOnItemClickListener(this);
		leftButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
	}

	@Override
	public void onBackPressed() {

		System.out.println("onBackPressed");

	}

	private void setUpViewIds() {

		gridviewFoodGallery = (GridView) findViewById(R.id.Food_gridview);
		rightButtonImage = (ImageView) findViewById(R.id.right_button);
		rightButtonImage.setImageResource(R.drawable.setting);
		centreTitleTxt = (TextView) findViewById(R.id.center_txt);
		centreTitleTxt.setText(getResources().getString(R.string.my_food));
		leftButton = (LinearLayout) findViewById(R.id.leftbutton);
		rightButton = (LinearLayout) findViewById(R.id.rightbutton);
		leftButton.setVisibility(View.GONE);
		helpArrow = (ImageView) findViewById(R.id.imageViewhelp);
		helpText = (TextView) findViewById(R.id.textViewhelp);
		centreTitleTxt.setTypeface(Typeface.createFromAsset(this.getAssets(),
				this.getResources().getString(R.string.app_font_style_medium)));
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.rightbutton:
			startActivity(new Intent(this, SettingsActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			break;

		case R.id.leftbutton:

			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		int i = 1 + arg2;
		Intent intent = new Intent(this, FoodGalleryFlipper.class);
		intent.putExtra("clickedPicture", i);
		startActivityForResult(intent, UPDATE_GALLERY);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		new Thread(getCachedData).start();
//		if (requestCode == UPDATE_GALLERY && resultCode == RESULT_OK) {
//			
//
//			new Thread(getCachedData).start();
//
//		} else if (requestCode == UPDATE_GALLERY
//				&& resultCode == RESULT_CANCELED) {
//			new Thread(getCachedData).start();
//		}
	}

	public void onPause() {
		this.localyticsSession.close();
		this.localyticsSession.upload();
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();

		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}
}
