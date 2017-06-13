package com.foodfeedback.messages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ViewFlipper;

import com.foodfeedback.cachemanager.CoachOrderingCacheManager;
import com.foodfeedback.cachemanager.InteractedWithCacheManager;
import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.draglists.DynamicListViewMessages;
import com.foodfeedback.networking.CoachesNetworkingServices;
import com.foodfeedback.networking.OnboardingNetworkingServices;
import com.foodfeedback.networking.QuestionnaireNetworkingServices;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.onboarding.ChatActivity;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.settings.SettingsActivity;
import com.foodfeedback.utilities.RecommendAnimationHelper;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.AccountOperations;
import com.foodfeedback.valueobjects.InteractedWith;
import com.foodfeedback.valueobjects.MemberInfo;
import com.foodfeedback.valueobjects.UserDetails;
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class MessagesActivity extends Activity implements OnClickListener {

	private LocalyticsSession localyticsSession;

	private LinearLayout leftButton, rightButton, leftsearchButton;
	private TextView centreTitleTxt,addCoachText;
	private ViewFlipper viewflipper;
	private Button continueSearchButton;
	private Button searchByName, inviteaFriend;
	private EditText searchByNameEditText;
	int sizeFlipper;
	int loader = R.drawable.loader;
	AccountOperations accountOperationsObj;
	UserDetails userDetailsObj;
	String[] allCoachNames, allCoachBios;
	int[] allIds;
	public TextView statusUpdateList,answerQuestionTitle;
	public int numberStatusCount = 0;
	public static final int UPDATE_MESSAGESS = 1004;
	private ArrayList<MemberInfo> myCoachList;
	private DynamicListViewMessages allCoachesListView;
	private MessageCoachListAdapter messageCoachListAdapter;
	RelativeLayout footerView;
	Typeface tfNormal, tfSpecial;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.messages_screen);
		tfNormal = Typeface.createFromAsset(this.getAssets(),
				this.getResources().getString(R.string.font_standard));
		tfSpecial = Typeface.createFromAsset(this.getAssets(),
				this.getResources().getString(R.string.app_font_style_medium));
		try {
			accountOperationsObj = LoginDetailsCacheManager
					.getObject(Controller.getAppBackgroundContext());
			userDetailsObj = UserDetailsCacheManager.getObject(Controller
					.getAppBackgroundContext());
		} catch (Exception e) {
			e.printStackTrace();
		}

		new UpdateLatestMessageList(this).execute();

		setUpViewIds();
		setupFonts();
		addOnClickListeners();
		updateCoaches();
		updateList(true);

		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources

		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("Main Messages Tab");

		this.localyticsSession.upload(); // upload any data

		// new GetLatestMessagesList().execute();

	}

	private void updateList(boolean shouldAddFooter) {

		// Here is where we reorder the coaches to be based on the choice of
		// ordering done earlier
		// myCoachList =
		// CoachOrderingCacheManager.rearrangeCoachList(Controller.getAppBackgroundContext(),
		// myCoachList);

		messageCoachListAdapter = new MessageCoachListAdapter(this,
				myCoachList, ServiceKeys.SEARCH_BYNAME_MESSAGES);
		allCoachesListView.setAdapter(messageCoachListAdapter);
		allCoachesListView.setMemberList(myCoachList);
		allCoachesListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		setUpFooter();
//		if (myCoachList!=null && myCoachList.size() < 6){
//			//Reached the bottom
//			setUpFooter();
//		}else{
//			hideFooter();
//		}
		
//		allCoachesListView.setOnScrollListener(new OnScrollListener() {
//			
//			
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				
//				
//			}
//			
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				System.out.println("being fired here");
//				if (totalItemCount < 6){
//					System.out.println("There are less than 7 - so go ahead and make it visible man");
//					setUpFooter();
//				}else{
//					if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
//		            {
//						System.out.println("Match found");
//		                //Reached the bottom
//						setUpFooter();
//		            }else{
//		            	System.out.println("Match not found");
//		            	hideFooter();
//		            }	
//				}
//				
//				
//			}
//		});
	}

	private void hideFooter(){
		footerView.setVisibility(View.GONE);
	}
	private void setUpFooter(){
		footerView.setVisibility(View.VISIBLE);
		addCoachText = (TextView) findViewById(R.id.addCoachText);
		addCoachText.setTypeface(tfSpecial);
		footerView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewflipper.setInAnimation(RecommendAnimationHelper
						.inFromRightAnimation());
				viewflipper.setOutAnimation(RecommendAnimationHelper
						.outToLeftAnimation());
				viewflipper.setDisplayedChild(1);

			}
		});
	}
	class GetLatestMessagesList extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				accountOperationsObj = LoginDetailsCacheManager
						.getObject(Controller.getAppBackgroundContext());
				new OnboardingNetworkingServices(getParent()
						.getApplicationContext()).interactedWithHttpGetRequest(
						Integer.toString(accountOperationsObj.getData()
								.getUser().getId()),
						UserDetailsCacheManager.getObject(
								Controller.getAppBackgroundContext())
								.getPassword());
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// updateCoaches();
			updateList(false);
		}
	}

	@Override
	protected void onResume() {
		this.localyticsSession.open();
		System.out.println(" JanTest onresume() FoodGallery ");
		super.onResume();
	}

	@Override
	protected void onPostCreate(Bundle icicle) {
		System.out.println(" JanTest onPostCreate() Messages ");
		super.onPostCreate(icicle);
	}

	@Override
	protected void onStart() {
		System.out.println(" JanTest onStart() Messages ");
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.

	}

	private void addOnClickListeners() {

		continueSearchButton.setOnClickListener(this);
		leftButton.setOnClickListener(this);
		leftsearchButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
		searchByName.setOnClickListener(this);
		inviteaFriend.setOnClickListener(this);
	}

	private void updateCoaches() {
		
		myCoachList = new ArrayList<MemberInfo>();

		InteractedWith interactedWithObj = null;
		try {
			interactedWithObj = InteractedWithCacheManager.getObject(Controller
					.getAppBackgroundContext());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (interactedWithObj != null && interactedWithObj.getData() != null
				&& interactedWithObj.getData().size() > 0) {

			for (int i = 0; i < interactedWithObj.getData().size(); i++) {
				myCoachList.add(interactedWithObj.getData().get(i));
			}
		}

		// Here is where we reorder the coaches to be based on the choice of
		// ordering done earlier
		if (myCoachList!=null)
			myCoachList = CoachOrderingCacheManager.rearrangeMembers(
				Controller.getAppBackgroundContext(), myCoachList);
		allCoachesListView.setMemberList(myCoachList);
	}

	private void setupFonts() {
		centreTitleTxt.setTypeface(tfSpecial);
		continueSearchButton.setTypeface(tfSpecial);
		searchByName.setTypeface(tfSpecial);
		inviteaFriend.setTypeface(tfSpecial);
		answerQuestionTitle.setTypeface(tfNormal);
	}

	private void setUpViewIds() {

		viewflipper = (ViewFlipper) findViewById(R.id.viewFlipper_coachestab);
		footerView = (RelativeLayout) findViewById(R.id.footer);
		sizeFlipper = viewflipper.getChildCount();
		centreTitleTxt = (TextView) findViewById(R.id.center_txt);
		leftButton = (LinearLayout) findViewById(R.id.leftbutton);
		leftsearchButton = (LinearLayout) findViewById(R.id.leftbutton_search);
		rightButton = (LinearLayout) findViewById(R.id.rightbutton);
		continueSearchButton = (Button) findViewById(R.id.continue_search);
		searchByName = (Button) findViewById(R.id.searchby_name);
		inviteaFriend = (Button) findViewById(R.id.inviteaFriend);
		searchByNameEditText = (EditText) findViewById(R.id.search_coachname_message);
		allCoachesListView = (DynamicListViewMessages) findViewById(R.id.coaches_listView);
		answerQuestionTitle = (TextView) findViewById(R.id.answerQuestionTitle);

		allCoachesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println("Item click done");
				Intent intent = new Intent(view.getContext(),
						ChatActivity.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(
						"coachID",
						((MemberInfo) messageCoachListAdapter.getItem(position))
								.getId() + "");
				intent.putExtra("message", "NOT");
				intent.putExtra("checkNotify", "NOT");
				startActivityForResult(intent, UPDATE_MESSAGESS);
				
			}
		});

		searchByNameEditText.setText("");
	}

//	private DropListener mDropListener = new DropListener() {
//		public void onDrop(int from, int to) {
//			if (messageCoachListAdapter instanceof MessageCoachListAdapter) {
//				((MessageCoachListAdapter) messageCoachListAdapter).onDrop(
//						from, to);
//				allCoachesListView.invalidateViews();
//				
//				
//			}
//		}
//	};
//
//	private RemoveListener mRemoveListener = new RemoveListener() {
//		public void onRemove(int which) {
//			if (messageCoachListAdapter instanceof MessageCoachListAdapter) {
//				((MessageCoachListAdapter) messageCoachListAdapter)
//						.onRemove(which);
//				allCoachesListView.invalidateViews();
//			}
//		}
//	};
//
//	private DragListener mDragListener = new DragListener() {
//
//		int backgroundColor = 0xe0103010;
//		int defaultBackgroundColor;
//
//		public void onDrag(int x, int y, ListView listView) {
//			System.out.println("Dragged");
//		}
//
//		public void onStartDrag(View itemView) {
//			System.out.println("Drag Started");
//			try {
//				itemView.setVisibility(View.INVISIBLE);
//				defaultBackgroundColor = itemView
//						.getDrawingCacheBackgroundColor();
//				itemView.setBackgroundColor(backgroundColor);
//
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//
//		}
//
//		public void onStopDrag(View itemView) {
//			System.out.println("Drag Ended");
//			try {
//				itemView.setVisibility(View.VISIBLE);
//				itemView.setBackgroundColor(defaultBackgroundColor);
//
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//
//		}
//
//	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1004 && resultCode == RESULT_OK) {

			updateCoaches();
			updateList(false);
			messageCoachListAdapter.notifyDataSetChanged();
		} else if (requestCode == 1004 && resultCode == RESULT_CANCELED) {
			updateCoaches();
			updateList(false);
			messageCoachListAdapter.notifyDataSetChanged();
		} else {
			updateCoaches();
			updateList(false);
			messageCoachListAdapter.notifyDataSetChanged();
		}
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
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.remove_coach:

			break;

		case R.id.rightbutton:
			startActivity(new Intent(this, SettingsActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			break;

		case R.id.continue_search:

			System.out.println(" getId "
					+ accountOperationsObj.getData().getUser().getId());
			checkConnectivityForQuestionnarie(v.getContext(),
					accountOperationsObj.getData().getUser().getId(),
					userDetailsObj.getPassword());
			System.out.println(" unique_continuew  xyz");

			break;
		case R.id.inviteaFriend:

			sendMailInvite("Join Food Feedback!");

			break;
		case R.id.leftbutton:

			viewflipper.setInAnimation(RecommendAnimationHelper
					.inFromRightAnimation());
			viewflipper.setOutAnimation(RecommendAnimationHelper
					.outToLeftAnimation());
			System.out.println(" unique_continuew  add_morecoaches");
			viewflipper
					.setDisplayedChild(viewflipper.getCurrentView().getId() - 1);

			break;

		case R.id.leftbutton_search:

			viewflipper.setInAnimation(RecommendAnimationHelper
					.inFromRightAnimation());
			viewflipper.setOutAnimation(RecommendAnimationHelper
					.outToLeftAnimation());
			System.out.println(" unique_continuew  add_morecoaches");
			viewflipper.setDisplayedChild(1);

			break;

		case R.id.searchby_name:

			viewflipper.setInAnimation(RecommendAnimationHelper
					.inFromRightAnimation());
			viewflipper.setOutAnimation(RecommendAnimationHelper
					.outToLeftAnimation());
			System.out.println(" unique_continuew  add_morecoaches");
			viewflipper.setDisplayedChild(2);
			searchByNameEditText.setText("");
			searchByNameEditText
					.setOnEditorActionListener(new OnEditorActionListener() {

						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if (actionId == EditorInfo.IME_ACTION_SEARCH) {
								System.out
										.println("  EditText Search ..... !  "
												+ v.getText());

								ServiceKeys.SEARCH_BYNAME = 300;
								checkConnectivityForSearchCoaches(v
										.getContext(), Integer
										.toString(accountOperationsObj
												.getData().getUser().getId()),
										userDetailsObj.getPassword(), v
												.getText().toString(), "0");

								InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(
										searchByNameEditText.getWindowToken(),
										0);
								return true;
							}
							return false;
						}
					});

			break;
		}
	}

	private void checkConnectivityForQuestionnarie(Context ctx, int userId,
			String password) {

		if (Utilities.isConnectingToInternet(ctx.getApplicationContext())) {

			QuestionnaireNetworkingServices.getInstance()
					.requestQuestionnaireService(ctx, userId, password, this,
							ServiceKeys.QUESTIONNAIRE_SERVICE);

		} else {
			Utilities.showErrorToast(
					getResources().getString(R.string.internet_connection),
					this);
		}

	}

	private void checkConnectivityForSearchCoaches(Context ctx, String userId,
			String password, String nameText, String excludeMyCoachesBoolean) {

		if (Utilities.isConnectingToInternet(ctx.getApplicationContext())) {

			CoachesNetworkingServices.getInstance()
					.requestSearchCoachesService(ctx, userId, password,
							nameText, excludeMyCoachesBoolean, this,
							ServiceKeys.SEARCH_COACHES_SERVICE);

		} else {
			Utilities.showErrorToast(
					getResources().getString(R.string.internet_connection),
					this);
		}

	}

	@Override
	public void onBackPressed() {

		System.out.println("onBackPressed");

	}

	public void onPause() {
		this.localyticsSession.close();
		this.localyticsSession.upload();
		System.out.println("paused");
		//allCoachesListView.invalidateViews();
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		System.out.println("google Analytics Stop");

		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	private class UpdateLatestMessageList extends AsyncTask<Void, Void, Void> {

		public UpdateLatestMessageList(Context context) {
			try {
				accountOperationsObj = LoginDetailsCacheManager.getObject(Controller.getAppBackgroundContext());
				userDetailsObj = UserDetailsCacheManager.getObject(Controller.getAppBackgroundContext());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {

			try {
				new OnboardingNetworkingServices(getParent()
						.getApplicationContext()).interactedWithHttpGetRequest(
						Integer.toString(accountOperationsObj.getData()
								.getUser().getId()),
						userDetailsObj.getPassword());
			} catch (Exception e) {

			}
			return null;
		}
	}
}
