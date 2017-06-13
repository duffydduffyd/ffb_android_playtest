package com.foodfeedback.onboarding;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.foodfeedback.cachemanager.ChatStatusNumberCacheManager;
import com.foodfeedback.cachemanager.CoachStatusNumberCacheManager;
import com.foodfeedback.cachemanager.InteractedWithCacheManager;
import com.foodfeedback.cachemanager.UnReadCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.coaches.CoachesActivity;
import com.foodfeedback.messages.MessagesActivity;
import com.foodfeedback.myfood.MyFoodActivity;
import com.foodfeedback.photo.TakePhotoActivity;
import com.foodfeedback.valueobjects.ChatNumberUpdate;
import com.foodfeedback.valueobjects.CoachStatusNumber;
import com.foodfeedback.valueobjects.InteractedWith;
import com.foodfeedback.valueobjects.UserDetails;
import com.foodfeedback.weighttracker.WeightTrackerActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gcm.GCMRegistrar;
import com.localytics.android.LocalyticsSession;

@SuppressWarnings("deprecation")
public class FoodTabActivity extends TabActivity {

	private LocalyticsSession localyticsSession;
	public TabHost tabHost;
	Controller aController;
	AsyncTask<Void, Void, Void> mRegisterTask;
	public TextView statusUpdateChat;
	int numberUpdateMessages, numberUpdateCoaches = 0;

	/** Called when the activity is first created. */

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_composer);
		setTabs();

		// Instantiate the object
		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources

		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("All others");

		this.localyticsSession.upload(); // upload any data

		aController = (Controller) getApplicationContext();

		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest permissions was properly set
		GCMRegistrar.checkManifest(this);

		// Register custom Broadcast receiver to show messages on activity
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Config.DISPLAY_REGISTRATION_MESSAGE_ACTION));

		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (regId.equals("")) {
			// Register with GCM
			GCMRegistrar.register(this, Config.GOOGLE_SENDER_ID);

		} else {

			// Device is already registered on GCM Server
			if (!GCMRegistrar.isRegisteredOnServer(this)) {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;

				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						// Register on our server
						// On server creates a new user
						aController.register(context, regId);

						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;

						finish();
					}
				};
				// execute AsyncTask
				mRegisterTask.execute(null, null, null);
			}
		}
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				System.out.println(" setOnTabChangedListener ");
				new updateCoachUiTab().execute();
				new updateStatusNumbers().execute();

			}
		});

	}

	private void loginSuccess() {
		try {
			UserDetails checkUserDetails = UserDetailsCacheManager
					.getObject(Controller.getAppBackgroundContext());
			checkUserDetails.setCheckOutLogin("login_success");
			UserDetailsCacheManager.saveObject(
					Controller.getAppBackgroundContext(), checkUserDetails);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		loginSuccess();
		System.out.println(" JanTest onresume() TAB ");
		new updateCoachUiTab().execute();
		new updateStatusNumbers().execute();
		super.onResume();
		this.localyticsSession.open();
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

	CoachStatusNumber statusNumber = null;

	private class updateCoachUiTab extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			try {
				statusNumber = CoachStatusNumberCacheManager
						.getObject(Controller.getAppBackgroundContext());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			if (statusNumber != null) {

				if (statusNumber.getCoachStatusNo() == 0) {
					((TextView) tabHost.getTabWidget().getChildAt(3)
							.findViewById(R.id.statusUpdateCoach))
							.setVisibility(View.GONE);
				} else {
					((TextView) tabHost.getTabWidget().getChildAt(3)
							.findViewById(R.id.statusUpdateCoach))
							.setVisibility(View.VISIBLE);
					((TextView) tabHost.getTabWidget().getChildAt(3)
							.findViewById(R.id.statusUpdateCoach)).setText(""
							+ statusNumber.getCoachStatusNo());
				}
			}
		}
	}

	private class updateStatusNumbers extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			if (Controller.getAppBackgroundContext() != null) {
				try {
					if (UnReadCacheManager.getObject(Controller
							.getAppBackgroundContext()) != null) {
						if (UnReadCacheManager.getObject(
								Controller.getAppBackgroundContext()).getData() != null)
							try {
								if (UnReadCacheManager.getObject(
										Controller.getAppBackgroundContext())
										.getData() != null) {
									numberUpdateMessages = UnReadCacheManager
											.getObject(
													Controller
															.getAppBackgroundContext())
											.getData().size();
								}

								InteractedWith interactedWith = InteractedWithCacheManager
										.getObject(Controller
												.getAppBackgroundContext());

								if (interactedWith != null) {

									for (int i = 0; i < interactedWith
											.getData().size(); i++) {

										ChatNumberUpdate chatNumberUpdate = ChatStatusNumberCacheManager
												.getObject(
														Controller
																.getAppBackgroundContext(),
														interactedWith
																.getData()
																.get(i).getId()
																+ "");
										if (chatNumberUpdate != null)
											numberUpdateMessages = numberUpdateMessages
													+ chatNumberUpdate
															.getNumberUpdate();
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						return "SUCCESS";
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return "FAILURE";
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.endsWith("SUCCESS")) {
				if (numberUpdateMessages == 0)
					statusUpdateChat.setVisibility(View.GONE);
				else {
					statusUpdateChat.setText(numberUpdateMessages + "");
					statusUpdateChat.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private void setTabs() {
		addTab(getResources().getString(R.string.my_food),
				R.drawable.tab_myfood, MyFoodActivity.class);
		addTabChatNotify(getResources().getString(R.string.messages),
				R.drawable.tab_messages, MessagesActivity.class);
		addTab(getResources().getString(R.string.take_picture),
				R.drawable.tab_takepicture, TakePhotoActivity.class);
		addTabCoachNotify(getResources().getString(R.string.coaches),
				R.drawable.tab_coaches, CoachesActivity.class);
		addTab(getResources().getString(R.string.tracker),
				R.drawable.tab_tracker, WeightTrackerActivity.class);
	}

	private void addTab(String labelId, int drawableId, Class<?> c) {
		tabHost = getTabHost();
		Intent intent = new Intent(this, c);
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);

		View tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.tab_indicator, getTabWidget(), false);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);

		spec.setIndicator(tabIndicator);
		spec.setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		tabHost.addTab(spec);
	}

	private void addTabChatNotify(String labelId, int drawableId, Class<?> c) {
		tabHost = getTabHost();
		Intent intent = new Intent(this, c);
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);

		View tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.tab_indicator_notify, getTabWidget(), false);
		statusUpdateChat = (TextView) tabIndicator
				.findViewById(R.id.statusUpdateChat);
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);

		spec.setIndicator(tabIndicator);
		spec.setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		tabHost.addTab(spec);
	}

	private void addTabCoachNotify(String labelId, int drawableId, Class<?> c) {
		tabHost = getTabHost();
		Intent intent = new Intent(this, c);
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);

		View tabIndicator = LayoutInflater.from(this).inflate(
				R.layout.tab_coach_notify, getTabWidget(), false);
		TextView statusUpdateCoach = (TextView) tabIndicator
				.findViewById(R.id.statusUpdateCoach);

		CoachStatusNumber statusNumber = null;
		try {
			statusNumber = CoachStatusNumberCacheManager.getObject(Controller
					.getAppBackgroundContext());
			if (statusNumber == null) {
				CoachStatusNumber st = new CoachStatusNumber();
				st.setCoachStatusNo(0);
				CoachStatusNumberCacheManager.saveObject(
						Controller.getAppBackgroundContext(), st);
				statusNumber = CoachStatusNumberCacheManager
						.getObject(Controller.getAppBackgroundContext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (statusNumber.getCoachStatusNo() == 0) {
			statusUpdateCoach.setVisibility(View.GONE);
		} else {
			statusUpdateCoach.setVisibility(View.VISIBLE);
			statusUpdateCoach.setText("" + statusNumber.getCoachStatusNo());
		}

		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);

		spec.setIndicator(tabIndicator);
		spec.setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		tabHost.addTab(spec);
	}

	// Class with extends AsyncTask class

	// Create a broadcast receiver to get message and show on screen
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String newMessage = intent.getExtras().getString(
					Config.EXTRA_MESSAGE);

			// Waking up mobile if it is sleeping
			aController.acquireWakeLock(getApplicationContext());
			System.out.println(" Testing broadcast ");
			// Display message on the screen
			// lblMessage.append(newMessage + "\n");

			// Toast.makeText(getApplicationContext(),
			// "Got Message: ghouse " + newMessage,
			// Toast.LENGTH_LONG).show();

			// Releasing wake lock
			aController.releaseWakeLock();
		}
	};

	@Override
	protected void onDestroy() {
		// Cancel AsyncTask

		// updateChatCountTab();
		// updateCoachUiTab();

		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			// Unregister Broadcast Receiver
			unregisterReceiver(mHandleMessageReceiver);
			// Clear internal resources.
			GCMRegistrar.onDestroy(this);

		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

	public void onPause() {
		this.localyticsSession.close();
		this.localyticsSession.upload();
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		System.out.println("google Analytics Stop");
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

}
