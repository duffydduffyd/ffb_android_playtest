package com.foodfeedback.onboarding;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;

import com.foodfeedback.cachemanager.ChatStatusNumberCacheManager;
import com.foodfeedback.cachemanager.CoachStatusNumberCacheManager;
import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.networking.MessagesNetworkingServices;
import com.foodfeedback.networking.OnboardingNetworkingServices;
import com.foodfeedback.valueobjects.AccountOperations;
import com.foodfeedback.valueobjects.ChatNumberUpdate;
import com.foodfeedback.valueobjects.CoachStatusNumber;
import com.foodfeedback.valueobjects.NotificationResponse;
import com.foodfeedback.valueobjects.UserDetails;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	static NotificationResponse sendMessageObj;
	private Controller aController = null;
	AccountOperations accountOperationsObj;
	static AsyncTask<Void, Void, Void> updateCoachesTab;
	String message = null, fromUser = null, sid = null;
	int count = 0;

	public GCMIntentService() {
		// Call extended class Constructor GCMBaseIntentService
		super(Config.GOOGLE_SENDER_ID);
	}

	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId) {

		// Get Global Controller Class object (see application tag in
		// AndroidManifest.xml)
		if (aController == null)
			aController = (Controller) getApplicationContext();

		Log.i(TAG, "---------- onRegistered -------------");
		Log.i(TAG, "Device registered: regId = " + registrationId);
		aController.displayRegistrationMessageOnScreen(context,
				"Your device registred with GCM");

		aController.register(context, registrationId);

	}

	/**
	 * Method called on device unregistred
	 * */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		if (aController == null)
			aController = (Controller) getApplicationContext();
		Log.i(TAG, "---------- onUnregistered -------------");
		Log.i(TAG, "Device unregistered");
		aController.displayRegistrationMessageOnScreen(context,
				getString(R.string.gcm_unregistered));
		aController.unregister(context, registrationId);
	}

	/**
	 * Method called on Receiving a new message from GCM server
	 * */
	@Override
	protected void onMessage(Context context, Intent intent) {
		if (aController == null)
			aController = (Controller) getApplicationContext();

		Log.i(TAG, "---------- onMessage -------------");
		try {
			accountOperationsObj = LoginDetailsCacheManager
					.getObject(Controller.getAppBackgroundContext());
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		String notiRes1 = intent.getExtras().getString("aps");
		Log.i("NotificationResponse", "" + notiRes1);
		InputStream is = new ByteArrayInputStream(intent.getExtras()
				.getString("aps").getBytes());

		Gson gson = new Gson();
		sendMessageObj = gson.fromJson(new InputStreamReader(is),
				NotificationResponse.class);

		UserDetails checkUserDetails = null;
		try {
			checkUserDetails = UserDetailsCacheManager.getObject(Controller
					.getAppBackgroundContext());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (isAppInForeground()) {
			// Update the UI
			if (checkUserDetails != null) {
				if (checkUserDetails.getCheckOutLogin() != null) {
					if (checkUserDetails.getCheckOutLogin().equals(
							"login_success")) {

						if (sendMessageObj.getAlert().getLocKey()
								.equals(Config.APNS_MESSAGE)) {
							message = sendMessageObj.getAlert().getLocArgs()
									.get(2);// intent.getExtras().getString("aps");
							fromUser = getResources().getString(
									R.string.notification_header)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(0)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1);

							sid = intent.getExtras().getString("sid");
							count++;

							System.out.println(" GCM Count " + count);

							ChatNumberUpdate chatNumberUpdate = null;
							chatNumberUpdate = new ChatNumberUpdate();
							chatNumberUpdate.setNumberUpdate(count);
							try {
								ChatStatusNumberCacheManager.saveObject(
										Controller.getAppBackgroundContext(),
										chatNumberUpdate, sid);
							} catch (Exception e) {
								e.printStackTrace();
							}

							updateCoachesTab = new UpdateCoachesTab(context,
									"SendMessage");
							updateCoachesTab.execute();
							MediaPlayer helpSounds = MediaPlayer.create(this,
									R.raw.bite);
							helpSounds.start();
							// Call broadcast defined on ShowMessage.java to
							// show message on ShowMessage.java screen
							aController.displayMessageOnScreen(context,
									fromUser, message, sid);

						} else if (sendMessageObj.getAlert().getLocKey()
								.equals(Config.APNS_MESSAGENOIMAGE)) {

							message = getResources().getString(
									R.string.notification_photo_greeting)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(0)
									+ ", "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1)
									+ getResources()
											.getString(
													R.string.notification_photo_message);// sendMessageObj.getAlert().getLocArgs().get(2);//intent.getExtras().getString("aps");
							fromUser = getResources().getString(
									R.string.notification_header)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1);
							// aController.displayMessageOnScreen(context,
							// fromUser,message, sid);
							MediaPlayer helpSounds = MediaPlayer.create(this,
									R.raw.bite);
							helpSounds.start();
							sid = intent.getExtras().getString("sid");

							// ApnsMessage(context,Config.APNS_MESSAGENOIMAGE,).execute();

						} else if (sendMessageObj.getAlert().getLocKey()
								.equals(Config.APNS_COACHINVITE)) {

							CoachStatusNumber statusNumber, updateStatusNo;
							updateStatusNo = new CoachStatusNumber();
							try {
								statusNumber = CoachStatusNumberCacheManager
										.getObject(Controller
												.getAppBackgroundContext());

								updateStatusNo.setCoachStatusNo(statusNumber
										.getCoachStatusNo() + 1);
								CoachStatusNumberCacheManager.saveObject(
										(Controller.getAppBackgroundContext()),
										null);
								CoachStatusNumberCacheManager.saveObject(
										(Controller.getAppBackgroundContext()),
										updateStatusNo);
							} catch (Exception e) {
								e.printStackTrace();
							}

							message = sendMessageObj.getAlert().getLocArgs()
									.get(0)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1)
									+ " "
									+ getResources().getString(
											R.string.notification_coachinvite);// sendMessageObj.getAlert().getLocArgs().get(2);//intent.getExtras().getString("aps");
							fromUser = getResources().getString(
									R.string.notification_header)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(0)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1);
							MediaPlayer helpSounds = MediaPlayer.create(this,
									R.raw.bite);
							helpSounds.start();
							sid = intent.getExtras().getString("invitation");

							updateCoachesTab = new UpdateCoachesTab(context,
									"InviteCoach");
							updateCoachesTab.execute();

						} else if (sendMessageObj.getAlert().getLocKey()
								.equals(Config.APNS_COACHINVITEACCEPT)) {

							message = sendMessageObj.getAlert().getLocArgs()
									.get(0)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1)
									+ " "
									+ getResources()
											.getString(
													R.string.notification_coachinvite_accepted);
							fromUser = getResources().getString(
									R.string.notification_header)
									+ sendMessageObj.getAlert().getLocArgs()
											.get(0)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1);
							MediaPlayer helpSounds = MediaPlayer.create(this,
									R.raw.bite);
							helpSounds.start();
							sid = intent.getExtras().getString("invitation");

							updateCoachesTab = new UpdateCoachesTab(context,
									"InviteAccepted");
							updateCoachesTab.execute();

						} else if (sendMessageObj.getAlert().getLocKey()
								.equals(Config.APNS_COACHINVITEDECLINE)) {

							message = sendMessageObj.getAlert().getLocArgs()
									.get(0)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1)
									+ " "
									+ getResources()
											.getString(
													R.string.notification_coachinvite_declined);
							fromUser = getResources().getString(
									R.string.notification_header)
									+ sendMessageObj.getAlert().getLocArgs()
											.get(0)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1);
							MediaPlayer helpSounds = MediaPlayer.create(this,
									R.raw.bite);
							helpSounds.start();
							sid = intent.getExtras().getString("invitation");

						} else if (sendMessageObj.getAlert().getLocKey()
								.equals(Config.APNS_REMINDERWEEKLY)) {
							MediaPlayer helpSounds = MediaPlayer.create(this,
									R.raw.bite);
							helpSounds.start();
							sid = "ReminderWeekly";
							message = getResources().getString(
									R.string.notification_reminder_header);
							fromUser = getResources().getString(
									R.string.notification_reminder_message);
						}
					}
				}
			}
		} else {
			// generate notification to notify user
			if (checkUserDetails != null) {
				if (checkUserDetails.getCheckOutLogin() != null) {
					if (checkUserDetails.getCheckOutLogin().equals(
							"login_success")) {

						if (sendMessageObj.getAlert().getLocKey()
								.equals(Config.APNS_MESSAGE)) {
							message = sendMessageObj.getAlert().getLocArgs()
									.get(2);
							fromUser = getResources().getString(
									R.string.notification_header)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(0)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1);
							MediaPlayer helpSounds = MediaPlayer.create(this,
									R.raw.bite);
							helpSounds.start();
							sid = intent.getExtras().getString("sid");

						} else if (sendMessageObj.getAlert().getLocKey()
								.equals(Config.APNS_MESSAGENOIMAGE)) {

							message = getResources().getString(
									R.string.notification_photo_greeting)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(0)
									+ ", "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1)
									+ " "
									+ getResources()
											.getString(
													R.string.notification_photo_message);// sendMessageObj.getAlert().getLocArgs().get(2);//intent.getExtras().getString("aps");
							fromUser = getResources().getString(
									R.string.notification_header)
									+ sendMessageObj.getAlert().getLocArgs()
											.get(0)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1);
							MediaPlayer helpSounds = MediaPlayer.create(this,
									R.raw.bite);
							helpSounds.start();
							sid = intent.getExtras().getString("sid");

						} else if (sendMessageObj.getAlert().getLocKey()
								.equals(Config.APNS_COACHINVITE)) {

							CoachStatusNumber statusNumber, updateStatusNo;
							updateStatusNo = new CoachStatusNumber();
							try {
								statusNumber = CoachStatusNumberCacheManager
										.getObject(Controller
												.getAppBackgroundContext());

								updateStatusNo.setCoachStatusNo(statusNumber
										.getCoachStatusNo() + 1);
								CoachStatusNumberCacheManager.saveObject(
										(Controller.getAppBackgroundContext()),
										null);
								CoachStatusNumberCacheManager.saveObject(
										(Controller.getAppBackgroundContext()),
										updateStatusNo);
							} catch (Exception e) {
								e.printStackTrace();
							}

							/*
							 * if(statusNumber.getCoachStatusNo() == 0){ //
							 * statusUpdateCoach.setVisibility(View.GONE);
							 * updateStatusNo } else{ //
							 * statusUpdateCoach.setVisibility(View.VISIBLE); //
							 * statusUpdateCoach
							 * .setText(""+statusNumber.getCoachStatusNo()); }
							 */

							message = sendMessageObj.getAlert().getLocArgs()
									.get(0)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1)
									+ " "
									+ getResources().getString(
											R.string.notification_coachinvite);// sendMessageObj.getAlert().getLocArgs().get(2);//intent.getExtras().getString("aps");
							fromUser = getResources().getString(
									R.string.notification_header)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(0)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1);
							MediaPlayer helpSounds = MediaPlayer.create(this,
									R.raw.bite);
							helpSounds.start();
							sid = intent.getExtras().getString("invitation");

							updateCoachesTab = new UpdateCoachesTab(context,
									"InviteCoach");

						} else if (sendMessageObj.getAlert().getLocKey()
								.equals(Config.APNS_COACHINVITEACCEPT)) {

							message = sendMessageObj.getAlert().getLocArgs()
									.get(0)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1)
									+ " "
									+ getResources()
											.getString(
													R.string.notification_coachinvite_accepted);
							fromUser = getResources().getString(
									R.string.notification_header)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(0)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1);
							MediaPlayer helpSounds = MediaPlayer.create(this,
									R.raw.bite);
							helpSounds.start();
							sid = intent.getExtras().getString("invitation");

							updateCoachesTab = new UpdateCoachesTab(context,
									"InviteAccepted");

						} else if (sendMessageObj.getAlert().getLocKey()
								.equals(Config.APNS_COACHINVITEDECLINE)) {

							message = sendMessageObj.getAlert().getLocArgs()
									.get(0)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1)
									+ " "
									+ getResources()
											.getString(
													R.string.notification_coachinvite_declined);
							fromUser = getResources().getString(
									R.string.notification_header)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(0)
									+ " "
									+ sendMessageObj.getAlert().getLocArgs()
											.get(1);
							MediaPlayer helpSounds = MediaPlayer.create(this,
									R.raw.bite);
							helpSounds.start();
							sid = intent.getExtras().getString("invitation");

						} else if (sendMessageObj.getAlert().getLocKey()
								.equals(Config.APNS_REMINDERWEEKLY)) {
							MediaPlayer helpSounds = MediaPlayer.create(this,
									R.raw.bite);
							helpSounds.start();
							sid = "ReminderWeekly";
							message = getResources().getString(
									R.string.notification_reminder_header);
							fromUser = getResources().getString(
									R.string.notification_reminder_message);

						}

						generateNotification(context, fromUser, message, sid);
					}
				}
			}
		}
	}

	/**
	 * Method called on receiving a deleted message
	 * */
	@Override
	protected void onDeletedMessages(Context context, int total) {

		if (aController == null)
			aController = (Controller) getApplicationContext();

		Log.i(TAG, "---------- onDeletedMessages -------------");
		String message = getString(R.string.gcm_deleted, total);

		// aController.displayMessageOnScreen(context, message);

		if (isAppInForeground()) {
			// generate notification to notify user
			generateNotification(context, "", message, "");
		} else {
			// Update the UI
		}
	}

	/**
	 * Method called on Error
	 * */
	@Override
	public void onError(Context context, String errorId) {

		if (aController == null)
			aController = (Controller) getApplicationContext();

		Log.i(TAG, "---------- onError -------------");
		Log.i(TAG, "Received error: " + errorId);

		aController.displayRegistrationMessageOnScreen(context,
				getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {

		if (aController == null)
			aController = (Controller) getApplicationContext();

		Log.i(TAG, "---------- onRecoverableError -------------");

		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		aController.displayRegistrationMessageOnScreen(context,
				getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Create a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String userFrom,
			String message, String pid) {
		int id = 0;
		int icon = R.drawable.user_thumb;
		long when = System.currentTimeMillis();

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		Intent notificationIntent = null;
		if (sendMessageObj.getAlert().getLocKey().equals(Config.APNS_MESSAGE)) {
			notificationIntent = new Intent(context, ChatActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			id = 1;
			notificationIntent.putExtra("coachID", pid);
			notificationIntent.putExtra("checkNotify", "NOT");
			notificationIntent.putExtra("message", "NOT");
			// notificationIntent.putExtra("message", message);
			// notificationIntent.putExtra("checkNotify", Config.APNS_MESSAGE);

		} else if (sendMessageObj.getAlert().getLocKey()
				.equals(Config.APNS_MESSAGENOIMAGE)) {
			notificationIntent = new Intent(context, ChatActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			id = 2;
			notificationIntent.putExtra("coachID", pid);
			notificationIntent.putExtra("checkNotify", "NOT");
			notificationIntent.putExtra("message", "NOT");

			// notificationIntent.putExtra("coachID", pid);
			// notificationIntent.putExtra("message", "IMAGE");
			// notificationIntent.putExtra("checkNotify", Config.APNS_MESSAGE);

		} else if (sendMessageObj.getAlert().getLocKey()
				.equals(Config.APNS_COACHINVITE)) {
			id = 3;
			notificationIntent = new Intent(context, FoodTabActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			updateCoachesTab.execute();

		} else if (sendMessageObj.getAlert().getLocKey()
				.equals(Config.APNS_COACHINVITEACCEPT)) {
			id = 4;
			notificationIntent = new Intent(context, FoodTabActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			updateCoachesTab.execute();

		} else if (sendMessageObj.getAlert().getLocKey()
				.equals(Config.APNS_COACHINVITEDECLINE)) {
			id = 5;
			notificationIntent = new Intent(context, FoodTabActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		} else if (sendMessageObj.getAlert().getLocKey()
				.equals(Config.APNS_REMINDERWEEKLY)) {
			id = 6;
			notificationIntent = new Intent(context, FoodTabActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		}

		PendingIntent intent = PendingIntent.getActivity(context,
				(int) System.currentTimeMillis(), notificationIntent,
				PendingIntent.FLAG_ONE_SHOT);
		notification.setLatestEventInfo(context, userFrom, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// Play default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;

		// notification.sound = Uri.parse("android.resource://" +
		// context.getPackageName() + "your_sound_file_name.mp3");

		// Vibrate if vibrate is enabled
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(id, notification);
	}

	private boolean isAppInForeground() {

		ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> l = mActivityManager
				.getRunningAppProcesses();
		Iterator<RunningAppProcessInfo> i = l.iterator();
		while (i.hasNext()) {
			RunningAppProcessInfo info = i.next();

			if (info.uid == getApplicationInfo().uid
					&& info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

	private class UpdateCoachesTab extends AsyncTask<Void, Void, Void> {

		public Context context;
		public String whichCall;

		// public Activity objActiviy;

		public UpdateCoachesTab(Context context, String whichCall) {
			this.context = context;
			this.whichCall = whichCall;
			// this.objActiviy = objActiviy;//(Activity) context;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {

				if (whichCall.equals("InviteCoach")) {
					new OnboardingNetworkingServices(context)
							.invitationsHttpGetRequest(
									Integer.toString(accountOperationsObj
											.getData().getUser().getId()),
									UserDetailsCacheManager.getObject(
											Controller
													.getAppBackgroundContext())
											.getPassword());
				} else if (whichCall.equals("InviteAccepted")) {
					new OnboardingNetworkingServices(context)
							.interactedWithHttpGetRequest(
									Integer.toString(accountOperationsObj
											.getData().getUser().getId()),
									UserDetailsCacheManager.getObject(
											Controller
													.getAppBackgroundContext())
											.getPassword());
				} else if (whichCall.equals("SendMessage")) {
					new MessagesNetworkingServices(context)
							.messageFromUserHttpGetRequest(
									context,
									Integer.toString(accountOperationsObj
											.getData().getUser().getId()),
									UserDetailsCacheManager.getObject(
											Controller
													.getAppBackgroundContext())
											.getPassword(), sid, "", "", "", "");

				}
			} catch (Exception e) {
				e.printStackTrace();

			}
			return null;
		}
	}
}
