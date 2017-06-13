package com.foodfeedback.onboarding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.foodfeedback.cachemanager.ChatStatusNumberCacheManager;
import com.foodfeedback.cachemanager.InteractedWithCacheManager;
import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.MessagesFromUserCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.coaches.CoachDeleteActivity;
import com.foodfeedback.messages.MessageObject;
import com.foodfeedback.networking.MessagesNetworkingServices;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.utilities.ProgressHUD;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.AccountOperations;
import com.foodfeedback.valueobjects.ChatNumberUpdate;
import com.foodfeedback.valueobjects.InteractedWith;
import com.foodfeedback.valueobjects.MessageList;
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class ChatActivity extends Activity implements OnClickListener {
	private LocalyticsSession localyticsSession;

	private EditText chatEditText;
	public ChatEntryAdapter chatMessagesAdapter;
	public TextView sendMessage, sendMessageLoader, centreTxt, noMessages;
	private String userId, notificationMessage, checkNotify;
	private String coachNameStr, bioStr, thumbURL, profileURL;
	private final static String TAG_Message_SENT = "Message sent";
	static final String TAG_MESSAGE_WITH_IMAGE = "with image";
	Bundle bundle;
	String newMessage;
	Activity objActivity;
	Context context;
	private LinearLayout leftButton, rightButton;
	AccountOperations accountOperationsObj;
	InteractedWith interactedWithObj;
	ListView chatListView;
	public static final int UPDATE_MESSAGECOACHES = 1003;
	Controller aController;
	public StringBuffer messageIds;
	private String myProfileImage, myFriendProfileImage;
	MessageList messagesFromUser;
	ProgressHUD mProgressHUD;
	Typeface tfNormal, tfSpecial;
	int top = -1;
	int index = -1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.overridePendingTransition(R.anim.anim_slide_in_left,
				R.anim.anim_slide_out_right);

		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext());
		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("Conversation");
		this.localyticsSession.upload(); // upload any data
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_screen);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		objActivity = this;
		context = this;
		tfNormal = Typeface.createFromAsset(context.getAssets(), context
				.getResources().getString(R.string.font_standard));
		tfSpecial = Typeface.createFromAsset(context.getAssets(), context
				.getResources().getString(R.string.app_font_style_medium));
		chatEditText = (EditText) findViewById(R.id.chat_edittext);
		chatEditText.setTypeface(tfNormal);
		chatEditText.addTextChangedListener(new TextWatcher() {
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
				if (sendMessage.getText().toString().length() == 0) {
					sendMessage.setTextColor(getResources().getColor(
							R.color.dark_grey));
					sendMessage.setEnabled(false);
				} else {
					sendMessage.setTextColor(Color.RED);
					sendMessage.setEnabled(true);
				}
			}
		});

		// Register custom Broadcast receiver to show messages on activity
		this.registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Config.DISPLAY_MESSAGE_ACTION));

		leftButton = (LinearLayout) findViewById(R.id.leftbutton);
		rightButton = (LinearLayout) findViewById(R.id.rightbutton);
		leftButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
		centreTxt = (TextView) findViewById(R.id.center_txt);
		centreTxt.setTypeface(Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.app_font_style_medium)));
		noMessages = (TextView) findViewById(R.id.no_messages);
		sendMessage = (TextView) findViewById(R.id.send_message);
		sendMessageLoader = (TextView) findViewById(R.id.send_loader);

		sendMessageLoader.setTypeface(tfSpecial);
		sendMessage.setOnClickListener(this);

		// Setup the list view
		chatListView = (ListView) findViewById(R.id.list);
		chatListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == 0) {

					//If its load more messages then
					index = chatListView.getFirstVisiblePosition();
					View v = chatListView.getChildAt(0);
					top = (v == null) ? 0 : v.getTop();

					new GetMessageAsyncTask(context, userId, 1).execute();
				}
			}
		});

		bundle = getIntent().getExtras();
		userId = bundle.getString("coachID");
		notificationMessage = bundle.getString("message");
		checkNotify = bundle.getString("checkNotify");
		// Get Global Controller Class object
		// (see application tag in AndroidManifest.xml)
		aController = (Controller) getApplicationContext();

	}

	private void checkConnectivityForMessageFromUser(Context ctx,
			String senderId) {
		if (Utilities.isConnectingToInternet(ctx.getApplicationContext())) {
			new GetMessageAsyncTask(ctx, senderId, 0).execute();
		} else {
			Utilities.showErrorToast(
					getResources().getString(R.string.internet_connection),
					this);
		}
	}

	private class GetLocalMessageAsyncTask extends
			AsyncTask<Void, String, Void> {

		public String userID;

		public GetLocalMessageAsyncTask(String userID) {
			this.userID = userID;
		}

		@Override
		protected void onPreExecute() {
			mProgressHUD = ProgressHUD.showMessageChat(context, "", true, true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				accountOperationsObj = LoginDetailsCacheManager
						.getObject(Controller.getAppBackgroundContext());
				myProfileImage = accountOperationsObj.getData().getUser()
						.getProfileImageThumbUrl();
				interactedWithObj = InteractedWithCacheManager
						.getObject(Controller.getAppBackgroundContext());
				messagesFromUser = MessagesFromUserCacheManager.getObject(
						Controller.getAppBackgroundContext(), this.userID);
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateChat();
			return null;

		}

		@Override
		protected void onPostExecute(Void result) {
			if (mProgressHUD != null) {
				if (mProgressHUD.isShowing()) {
					mProgressHUD.dismiss();
				}
			}
			// Get myProfileImage & Get myFriendProfileImage
			try {

				for (int i = 0; i < interactedWithObj.getData().size(); i++) {
					if (interactedWithObj.getData().get(i).getId() == Integer
							.parseInt(userId)) {

						coachNameStr = interactedWithObj.getData().get(i)
								.getFirst_name()
								+ " "
								+ interactedWithObj.getData().get(i)
										.getLast_name();

						bioStr = interactedWithObj.getData().get(i).getBio();
						thumbURL = interactedWithObj.getData().get(i)
								.getProfile_image_thumb_url();
						profileURL = interactedWithObj.getData().get(i)
								.getProfile_image_url();
						myFriendProfileImage = interactedWithObj.getData()
								.get(i).getProfile_image_thumb_url();
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (coachNameStr != null) {
				centreTxt.setText("" + coachNameStr);
			} else {
				centreTxt.setText("");
			}
			updateChatUI();
			checkConnectivityForMessageFromUser(context, userId);
		}
	}

	private class GetMessageAsyncTask extends AsyncTask<Void, String, String> {

		public Context context;
		public String senderId;

		int whichMessageId;

		public GetMessageAsyncTask(Context context, String senderId, int i) {
			this.context = context;
			this.senderId = senderId;
			this.whichMessageId = i;
		}

		@Override
		protected void onPreExecute() {
			mProgressHUD = ProgressHUD.showMessageChat(context, "", true, true);
		}

		@Override
		protected String doInBackground(Void... params) {

			String result = null;
			try {
				if (whichMessageId == 0) {
					result = new MessagesNetworkingServices(context)
							.messageFromUserHttpGetRequest(
									context,
									Integer.toString(accountOperationsObj
											.getData().getUser().getId()),
									UserDetailsCacheManager.getObject(
											Controller
													.getAppBackgroundContext())
											.getPassword(), senderId, "", "",
									"", "");

				} else {
					try {
						messagesFromUser = MessagesFromUserCacheManager
								.getObject(
										Controller.getAppBackgroundContext(),
										userId);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					String messageId = messagesFromUser.getData().get(0)
							.getId();
					System.out.println(" MessageId = " + messageId);
					result = new MessagesNetworkingServices(context)
							.messageFromUserHttpGetRequest(
									context,
									Integer.toString(accountOperationsObj
											.getData().getUser().getId()),
									UserDetailsCacheManager.getObject(
											Controller
													.getAppBackgroundContext())
											.getPassword(), senderId, "", "",
									messageId, "");
				}

			} catch (Exception e) {
				e.printStackTrace();
				// Utilities.showErrorToast(e.getMessage(), context);
			}
			try {
				messagesFromUser = MessagesFromUserCacheManager.getObject(
						Controller.getAppBackgroundContext(), userId);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			updateChat();
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (mProgressHUD != null) {
				if (mProgressHUD.isShowing()) {
					mProgressHUD.dismiss();
				}
			}
			if (result != null
					&& result.equals(ServiceKeys.MESSAGE_FROM_USER_SUCCESS)) {
				updateChatUI();
			} else {
				// Utilities.showErrorToast(result, objActivity);
			}
		}
	}

	private void updateChat() {
		Log.i("FoodFeeback", "Going to update chat window");

		chatMessagesAdapter = new ChatEntryAdapter(this,
				R.layout.chat_entry_list_item, userId);

		List<MessageObject> messageEntries = getNewsEntries();
		if (messageEntries.size() != 0) {

			MessageObject entry = new MessageObject(ServiceKeys.NO_PHTOTBG,
					ServiceKeys.NO_PROFILEBG, ServiceKeys.NO_PROFILE_PHOTO, "",
					"", "", ServiceKeys.CHAT_IMAGE_NO_STATUS, 0, "");
			chatMessagesAdapter.add(entry);
		}

		// Populate the list, through the adapter
		for (final MessageObject entry : messageEntries) {
			chatMessagesAdapter.add(entry);
		}

		// if (checkNotify.equals("NOT")) {
		// // Do nothing
		// chatMessagesAdapter.notifyDataSetChanged();
		// } else if (checkNotify.equals(Config.APNS_MESSAGE)) {
		// if (!notificationMessage.equals("IMAGE"))
		// otherMessage(notificationMessage);
		// }

		// chatListView.setAdapter(chatMessagesAdapter);
		// chatMessagesAdapter.notifyDataSetChanged();
		// ...

		try {
			ChatNumberUpdate chatNumberUpdate = new ChatNumberUpdate();
			chatNumberUpdate.setNumberUpdate(0);
			ChatStatusNumberCacheManager.saveObject(
					Controller.getAppBackgroundContext(), chatNumberUpdate,
					userId + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateChatUI() {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// otherLoadMoreMessages();

		if (checkNotify.equals("NOT")) {
			// Do nothing
			chatMessagesAdapter.notifyDataSetChanged();
		} else if (checkNotify.equals(Config.APNS_MESSAGE)) {
			if (!notificationMessage.equals("IMAGE"))
				otherMessage(notificationMessage);
		}

		if (chatMessagesAdapter.getCount() > 0) {

			noMessages.setVisibility(View.GONE);
		} else {
			noMessages.setVisibility(View.VISIBLE);
		}
		chatListView.setAdapter(chatMessagesAdapter);
		chatMessagesAdapter.notifyDataSetChanged();
		// ...

		// restore
		if (index != -1 && top != -1) {
			chatListView.setSelectionFromTop(index, top);
		}

	}

	// private void otherLoadMoreMessages() {
	// if (getNewsEntries().size() != 0) {
	// noMessages.setVisibility(View.GONE);
	// MessageObject entry = new MessageObject(ServiceKeys.NO_PHTOTBG,
	// ServiceKeys.NO_PROFILEBG, ServiceKeys.NO_PROFILE_PHOTO, "",
	// "", "", ServiceKeys.CHAT_IMAGE_NO_STATUS, 0, "");
	// chatMessagesAdapter.add(entry);
	// chatMessagesAdapter.notifyDataSetChanged();
	// } else {
	// noMessages.setVisibility(View.VISIBLE);
	// }
	// }

	private List<MessageObject> getNewsEntries() {
		MessageList messagesFromUser = null;
		try {
			messagesFromUser = MessagesFromUserCacheManager.getObject(
					Controller.getAppBackgroundContext(), userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		final List<MessageObject> entries = new ArrayList<MessageObject>();
		if (messagesFromUser != null) {

			// messagesFromUser =
			// MessagesFromUserCacheManager.getObject(Controller.getAppBackgroundContext());
			for (int i = 0; i < messagesFromUser.getData().size(); i++) {

				int senderID = Integer.parseInt(messagesFromUser.getData()
						.get(i).getSender_id());

				if (senderID == accountOperationsObj.getData().getUser()
						.getId()) {
					if (!messagesFromUser.getData().get(i).getText().equals("")) {
						if (messagesFromUser.getData().get(i).getImage_url()
								.equals("")
								&& messagesFromUser.getData().get(i)
										.getImage_thumb_url().equals("")) {
							entries.add(new MessageObject(
									ServiceKeys.NO_PHTOTBG,
									ServiceKeys.NO_PROFILEBG,
									myProfileImage,
									messagesFromUser.getData().get(i).getText(),
									messagesFromUser.getData().get(i)
											.getGallery_image_id(),
									messagesFromUser.getData().get(i)
											.getTimestamp(),

									ServiceKeys.CHAT_IMAGE_NO_STATUS,
									ServiceKeys.CHECK_OUTGOING_STATUS,
									messagesFromUser.getData().get(i).getId()));
						} else {
							entries.add(new MessageObject(messagesFromUser
									.getData().get(i).getImage_url(),
									messagesFromUser.getData().get(i)
											.getImage_thumb_url(),
									myProfileImage, messagesFromUser.getData()
											.get(i).getText(), messagesFromUser
											.getData().get(i)
											.getGallery_image_id(),
									messagesFromUser.getData().get(i)
											.getTimestamp(),
									ServiceKeys.CHAT_IMAGE_YES_STATUS,
									ServiceKeys.CHECK_OUTGOING_STATUS,
									messagesFromUser.getData().get(i).getId()));
						}

					} else {
						entries.add(new MessageObject(messagesFromUser
								.getData().get(i).getImage_url(),
								messagesFromUser.getData().get(i)
										.getImage_thumb_url(), myProfileImage,
								messagesFromUser.getData().get(i).getText(),
								messagesFromUser.getData().get(i)
										.getGallery_image_id(),
								messagesFromUser.getData().get(i)
										.getTimestamp(),
								ServiceKeys.CHAT_IMAGE_YES_STATUS,
								ServiceKeys.CHECK_OUTGOING_STATUS,
								messagesFromUser.getData().get(i).getId()));
					}
				} else {
					if (!messagesFromUser.getData().get(i).getText().equals("")) {
						if (messagesFromUser.getData().get(i).getImage_url()
								.equals("")
								&& messagesFromUser.getData().get(i)
										.getImage_thumb_url().equals("")) {
							entries.add(new MessageObject(
									ServiceKeys.NO_PHTOTBG,
									ServiceKeys.NO_PROFILEBG,
									myFriendProfileImage, messagesFromUser
											.getData().get(i).getText(),
									messagesFromUser.getData().get(i)
											.getGallery_image_id(),
									messagesFromUser.getData().get(i)
											.getTimestamp(),
									ServiceKeys.CHAT_IMAGE_NO_STATUS,
									ServiceKeys.CHECK_INCOMING_STATUS,
									messagesFromUser.getData().get(i).getId()));
						} else {
							entries.add(new MessageObject(messagesFromUser
									.getData().get(i).getImage_url(),
									messagesFromUser.getData().get(i)
											.getImage_thumb_url(),
									myFriendProfileImage, messagesFromUser
											.getData().get(i).getText(),
									messagesFromUser.getData().get(i)
											.getGallery_image_id(),
									messagesFromUser.getData().get(i)
											.getTimestamp(),
									ServiceKeys.CHAT_IMAGE_YES_STATUS,
									ServiceKeys.CHECK_INCOMING_STATUS,
									messagesFromUser.getData().get(i).getId()));
						}

					} else {

						entries.add(new MessageObject(messagesFromUser
								.getData().get(i).getImage_url(),
								messagesFromUser.getData().get(i)
										.getImage_thumb_url(),
								myFriendProfileImage, messagesFromUser
										.getData().get(i).getText(),
								messagesFromUser.getData().get(i)
										.getGallery_image_id(),
								messagesFromUser.getData().get(i)
										.getTimestamp(),
								ServiceKeys.CHAT_IMAGE_YES_STATUS,
								ServiceKeys.CHECK_INCOMING_STATUS,
								messagesFromUser.getData().get(i).getId()));

					}
				}
			}

		}

		return entries;
	}

	public void sendMessage() {

		chatEditText.setText("");
		MessageObject entry = new MessageObject(ServiceKeys.NO_PHTOTBG,
				myProfileImage, myProfileImage, "" + newMessage, "",
				getCurrentDate(), ServiceKeys.CHAT_IMAGE_NO_STATUS,
				ServiceKeys.CHECK_OUTGOING_STATUS, "");
		chatMessagesAdapter.add(entry);
		chatMessagesAdapter.notifyDataSetChanged();

	}

	public void otherMessage(String otherMessage) {

		MessageObject entry = new MessageObject(ServiceKeys.NO_PHTOTBG,
				ServiceKeys.NO_PROFILE_PHOTO, ServiceKeys.NO_PROFILE_PHOTO, ""
						+ otherMessage, "", getCurrentDate(),
				ServiceKeys.CHAT_IMAGE_NO_STATUS,
				ServiceKeys.CHECK_INCOMING_STATUS, "");
		chatMessagesAdapter.add(entry);
		chatMessagesAdapter.notifyDataSetChanged();
		// longTimeStamp = timeStamp;
	}

	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.send_message:

			checkConnectivityForSendMessage(v.getContext());

			break;

		case R.id.leftbutton:
			new UpdateMessagesFromUser(context).execute();
			setResult(RESULT_CANCELED);
			finish();
			break;

		case R.id.rightbutton:
			Intent intent = new Intent(v.getContext(),
					CoachDeleteActivity.class);
			intent.putExtra("coachName", coachNameStr);
			intent.putExtra("coachBio", bioStr);
			intent.putExtra("userID", Integer.parseInt(userId));
			intent.putExtra("thumbUrl", thumbURL);
			intent.putExtra("profileUrl", profileURL);
			// startActivity(intent);
			startActivityForResult(intent, UPDATE_MESSAGECOACHES);
			break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println("Received result here");
		if (requestCode == UPDATE_MESSAGECOACHES && resultCode == RESULT_OK) {
			System.out.println("Received result here 2 ");
			setResult(RESULT_OK);
			finish();

		} else if (requestCode == UPDATE_MESSAGECOACHES

		&& resultCode == RESULT_CANCELED) {
			System.out.println("Received result here 3");
		}
	}

	private void checkConnectivityForSendMessage(Context context) {

		if (Utilities.isConnectingToInternet(context.getApplicationContext())) {

			newMessage = chatEditText.getText().toString().trim();
			if (newMessage.length() > 0) {
				new SendMessageAsyncTask(context).execute();
				System.out.println("Localytics sent message");
				Map values = new HashMap();
				values.put(TAG_MESSAGE_WITH_IMAGE, "No");
				this.localyticsSession.tagEvent(TAG_Message_SENT, values);
				this.localyticsSession.tagEvent(ChatActivity.TAG_Message_SENT);
			}
		} else {
			Utilities.showErrorToast(
					getResources().getString(R.string.internet_connection),
					this);
		}

	}

	private class SendMessageAsyncTask extends AsyncTask<Void, String, String> {

		public Context context;

		public SendMessageAsyncTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			sendMessageLoader.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(Void... params) {

			String result = null;
			try {
				result = new MessagesNetworkingServices(context)
						.sendMessageHttpPostRequest(
								Integer.toString(accountOperationsObj.getData()
										.getUser().getId()),
								UserDetailsCacheManager.getObject(
										Controller.getAppBackgroundContext())
										.getPassword(), userId,
								"" + newMessage, "");
			} catch (Exception e) {
				e.printStackTrace();
				result = e.getMessage();
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			if (result.equals(getString(R.string.message_sent_success))) {
				sendMessageLoader.setVisibility(View.GONE);
				sendMessage();
			} else {
				Utilities.showErrorToast(result, objActivity);
				sendMessageLoader.setVisibility(View.GONE);
			}
		}
	}

	private String getCurrentDate() {
		return String.valueOf((System.currentTimeMillis() / 1000L));
		
		
	}

	@Override
	public void onBackPressed() {
		new UpdateMessagesFromUser(context).execute();
		System.out.println("onBackPressed");
		setResult(RESULT_CANCELED);
		finish();
	}

	private class UpdateMessagesFromUser extends AsyncTask<Void, Void, Void> {

		public Context context;
		boolean mShouldUpdateView = false;

		public UpdateMessagesFromUser(Context context, boolean shouldUpdateUI) {
			this.context = context;
			this.mShouldUpdateView = shouldUpdateUI;
		}

		public UpdateMessagesFromUser(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				new MessagesNetworkingServices(context)
						.messageFromUserHttpGetRequest(
								context,
								Integer.toString(accountOperationsObj.getData()
										.getUser().getId()),
								UserDetailsCacheManager.getObject(
										Controller.getAppBackgroundContext())
										.getPassword(), userId, "20", "", "",
								"");
				if (mShouldUpdateView) {
					updateChat();
				}
			} catch (Exception e) {

			}
			return null;
		}
	}

	// Create a broadcast receiver to get message and show on screen
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// String longTimeStamp = "NOT";
			String newMessage = intent.getExtras().getString(
					Config.EXTRA_MESSAGE);
			String newTitle = intent.getExtras().getString(Config.EXTRA_TITLE);
			String newPid = intent.getExtras().getString(Config.PID);
			// Waking up mobile if it is sleeping
			aController.acquireWakeLock(getApplicationContext());

			if (newPid.equals(userId)) {
				otherMessage(newTitle);
			}

			// Releasing wake lock
			aController.releaseWakeLock();
		}
	};

	@Override
	protected void onDestroy() {
		try {
			// Unregister Broadcast Receiver
			unregisterReceiver(mHandleMessageReceiver);

		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}

	public void onResume() {
		super.onResume();
		System.out.println("Resumed");
		// First get from local and then get from server
		new GetLocalMessageAsyncTask(userId).execute();

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
		System.out.println("Killing this activity");
		System.out.println("google Analytics Stop");

		EasyTracker.getInstance(this).activityStop(this); // Add this method.
		// finish();
	}
}
