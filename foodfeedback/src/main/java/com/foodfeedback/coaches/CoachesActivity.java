package com.foodfeedback.coaches;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ViewFlipper;

import com.foodfeedback.cachemanager.CoachOrderingCacheManager;
import com.foodfeedback.cachemanager.InteractedWithCacheManager;
import com.foodfeedback.cachemanager.InvitationsCacheManager;
import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.draglists.DynamicListViewCoaches;
import com.foodfeedback.networking.CoachesNetworkingServices;
import com.foodfeedback.networking.OnboardingNetworkingServices;
import com.foodfeedback.networking.QuestionnaireNetworkingServices;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.settings.SettingsActivity;
import com.foodfeedback.utilities.RecommendAnimationHelper;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.AccountOperations;
import com.foodfeedback.valueobjects.Coach;
import com.foodfeedback.valueobjects.InteractedWith;
import com.foodfeedback.valueobjects.Invitations;
import com.foodfeedback.valueobjects.UserDetails;
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class CoachesActivity extends Activity implements OnClickListener {
	private LocalyticsSession localyticsSession;

	private static class SingletonHolder {
		public static final CoachesActivity INSTANCE = new CoachesActivity();
	}

	public static final int UPDATE_COACHES = 1000;
	public static final int UPDATE_PENDINGCOACHES = 1001;

	public static CoachesActivity getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private LinearLayout leftButton, rightButton, leftsearchButton;
	private TextView centreTitleTxt,answerQuestionTitle;

	private ImageView rightButtonImage;
	private ViewFlipper viewflipper;
	private Button continueSearchButton;
	private ImageView left_button;
	private Button searchByName, inviteaFriend;
	public EditText searchByNameEditText;
	Controller aController;
	private DynamicListViewCoaches allCoachesListView;
	String[] allCoachNames, allCoachBios;
	String[] allMainCoachNames = null, allMainCoachBios = null,
			allThumbUrl = null, allProfileUrl = null,
			allThumbUrlPending = null, allProfileUrlPending = null;
	public int coachUserId;
	public String coachName1, coachBio1;
	int[] allIds, allMainIds;
	AccountOperations accountOperationsObj;
	UserDetails userDetailsObj;
	int loader = R.drawable.loader;
	private ArrayList<Coach> myCoachList;
	CoachListViewAdapter coachListViewAdapter;
	TextView addCoachText;
	Typeface tfNormal, tfSpecial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.coaches_tab);
		
		
		tfNormal = Typeface.createFromAsset(this.getAssets(),
				this.getResources().getString(R.string.font_standard));
		tfSpecial = Typeface.createFromAsset(this.getAssets(),
				this.getResources().getString(R.string.app_font_style_medium));
		
		setUpViewIds();
		setupFonts();
		addOnClickListeners();

		updateCoaches();
		updatePendingCoaches();
		updateList(true);

		try {
			accountOperationsObj = LoginDetailsCacheManager
					.getObject(Controller.getAppBackgroundContext());
			userDetailsObj = UserDetailsCacheManager.getObject(Controller
					.getAppBackgroundContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources

		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("Coaches");

		this.localyticsSession.upload(); // upload any data
		new GetLatestCoachList(getApplicationContext()).execute();

	}

	class GetLatestCoachList extends AsyncTask<Void, Void, Void> {

		private Context ctx;

		public GetLatestCoachList(Context context) {
			this.ctx = context;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				accountOperationsObj = LoginDetailsCacheManager
						.getObject(Controller.getAppBackgroundContext());
				new OnboardingNetworkingServices(this.ctx)
						.interactedWithHttpGetRequest(
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
			updateCoaches();
			updatePendingCoaches();
			updateList(false);
		}
	}
//	private void hideFooter(){
//		RelativeLayout footerView = (RelativeLayout) findViewById(R.id.footer);
//		
//		footerView.setVisibility(View.GONE);
//	}
	private void setUpFooter(){
		RelativeLayout footerView = (RelativeLayout) findViewById(R.id.footer);
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
	private void updateList(boolean shouldAddFooter) {

		coachListViewAdapter = new CoachListViewAdapter(this, myCoachList);
		allCoachesListView.setAdapter(coachListViewAdapter);
		allCoachesListView.setMemberList(myCoachList);
		allCoachesListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		setUpFooter();
//		allCoachesListView.setOnScrollListener(new OnScrollListener() {
//
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//			}

//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				
//				if (totalItemCount < 7){
//					setUpFooter();
//				}else{
//					if (firstVisibleItem + visibleItemCount == totalItemCount
//							&& totalItemCount != 0) {
//						// Reached the bottom
//						setUpFooter();
//					} else {
//						hideFooter();
//					}
//				}
//			}
//		});
	}

	private void updatePendingCoaches() {
		Invitations invitationsObj = null;
		try {
			invitationsObj = InvitationsCacheManager.getObject(Controller
					.getAppBackgroundContext());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (invitationsObj != null) {

			if (invitationsObj.getData().size() > 0) {
				Coach cItemPending = new Coach();
				cItemPending
						.setItemType(ServiceKeys.COACH_ITEM_TYPE_PENDINGTITLE);
				myCoachList.add(cItemPending);
			}

			for (int i = 0; i < invitationsObj.getData().size(); i++) {

				Coach cItem = new Coach();
				cItem.setItemType(ServiceKeys.COACH_ITEM_TYPE_INVITATION);
				cItem.setmInvitation(invitationsObj.getData().get(i));
				myCoachList.add(cItem);
			}
		}
	}

	private void addOnClickListeners() {

		continueSearchButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
		leftButton.setOnClickListener(this);
		left_button.setOnClickListener(this);
		leftsearchButton.setOnClickListener(this);
		searchByName.setOnClickListener(this);
		inviteaFriend.setOnClickListener(this);
	}

	private void updateCoaches() {
		myCoachList = new ArrayList<Coach>();
		InteractedWith coachListObj = null;
		try {
			coachListObj = InteractedWithCacheManager.getObject(Controller
					.getAppBackgroundContext());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (coachListObj != null) {

			for (int i = 0; i < coachListObj.getData().size(); i++) {
				Coach cItem = new Coach();
				cItem.setItemType(ServiceKeys.COACH_ITEM_TYPE_MYCOACH);
				cItem.setMemberInfo(coachListObj.getData().get(i));
				myCoachList.add(cItem);
			}
		}

		// Here is where we reorder the coaches to be based on the choice of
		// ordering done earlier
		myCoachList = CoachOrderingCacheManager.rearrangeCoaches(
				Controller.getAppBackgroundContext(), myCoachList);

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
		rightButtonImage = (ImageView) findViewById(R.id.right_button);
		rightButtonImage.setImageResource(R.drawable.setting);
		centreTitleTxt = (TextView) findViewById(R.id.center_txt);
		centreTitleTxt.setText(getResources().getString(R.string.coaches));
		leftButton = (LinearLayout) findViewById(R.id.leftbutton);
		rightButton = (LinearLayout) findViewById(R.id.rightbutton);
		continueSearchButton = (Button) findViewById(R.id.continue_search);
		left_button = (ImageView) findViewById(R.id.left_button);
		leftsearchButton = (LinearLayout) findViewById(R.id.leftbutton_search);
		searchByName = (Button) findViewById(R.id.searchby_name);

		inviteaFriend = (Button) findViewById(R.id.inviteaFriend);
		searchByNameEditText = (EditText) findViewById(R.id.search_coachname_message);
		allCoachesListView = (DynamicListViewCoaches) findViewById(R.id.coaches_listView);
		searchByNameEditText.setText("");

		answerQuestionTitle = (TextView) findViewById(R.id.answerQuestionTitle);
		
		allCoachesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,

			int position, long id) {

				Intent intent;

				switch (((Coach) coachListViewAdapter.getItem(position))
						.getItemType()) {
				case ServiceKeys.COACH_ITEM_TYPE_MYCOACH:

					intent = new Intent(view.getContext(),
							CoachDeleteActivity.class);

					intent.putExtra(
							"coachName",
							((Coach) coachListViewAdapter.getItem(position))
									.getMemberInfo().getFirst_name()
									+ " "
									+ ((Coach) coachListViewAdapter
											.getItem(position)).getMemberInfo()
											.getLast_name());
					intent.putExtra("coachBio", ((Coach) coachListViewAdapter
							.getItem(position)).getMemberInfo().getBio());
					intent.putExtra("userID",
							((Coach) coachListViewAdapter.getItem(position))
									.getMemberInfo().getId());
					intent.putExtra("thumbUrl", ((Coach) coachListViewAdapter
							.getItem(position)).getMemberInfo()
							.getProfile_image_thumb_url());
					intent.putExtra("profileUrl", ((Coach) coachListViewAdapter
							.getItem(position)).getMemberInfo()
							.getProfile_image_url());
					startActivityForResult(intent, UPDATE_COACHES);

					break;

				case ServiceKeys.COACH_ITEM_TYPE_PENDINGTITLE:

					// coachNameTextView.setText("Pending Coaches");

					break;

				case ServiceKeys.COACH_ITEM_TYPE_INVITATION:

					if (((Coach) coachListViewAdapter.getItem(position))
							.getmInvitation() != null) {

						intent = new Intent(view.getContext(),
								CoachInvitationResponseActivity.class);

						intent.putExtra(
								"coachName",
								((Coach) coachListViewAdapter.getItem(position))
										.getmInvitation().getSender().getFirst_name()
										+ " "
										+ ((Coach) coachListViewAdapter
												.getItem(position))
												.getmInvitation().getSender().getLast_name());
						intent.putExtra(
								"coachBio",
								((Coach) coachListViewAdapter.getItem(position))
										.getmInvitation().getSender().getBio());
						intent.putExtra(
								"invitationId",
								((Coach) coachListViewAdapter.getItem(position))
										.getmInvitation().getId());
						intent.putExtra(
								"thumbUrl",
								((Coach) coachListViewAdapter.getItem(position))
										.getmInvitation().getSender()
										.getProfile_image_thumb_url());
						intent.putExtra(
								"profileUrl",
								((Coach) coachListViewAdapter.getItem(position))
										.getmInvitation().getSender().getProfile_image_url());
						startActivityForResult(intent, UPDATE_PENDINGCOACHES);

					}

					break;

				}

			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == UPDATE_COACHES && resultCode == RESULT_OK) {

			updateCoaches();
			updatePendingCoaches();
			updateList(false);
			coachListViewAdapter.notifyDataSetChanged();

		} else if (requestCode == UPDATE_PENDINGCOACHES
				&& resultCode == RESULT_OK) {

			updateCoaches();
			updatePendingCoaches();
			updateList(false);
			coachListViewAdapter.notifyDataSetChanged();

		} else if (requestCode == UPDATE_COACHES
				&& resultCode == RESULT_CANCELED) {

		} else if (requestCode == UPDATE_PENDINGCOACHES
				&& resultCode == RESULT_CANCELED) {

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

		case R.id.leftbutton:

			viewflipper.setInAnimation(RecommendAnimationHelper
					.inFromRightAnimation());
			viewflipper.setOutAnimation(RecommendAnimationHelper
					.outToLeftAnimation());
			System.out.println(" unique_continuew  add_morecoaches");
			viewflipper
					.setDisplayedChild(viewflipper.getCurrentView().getId() - 1);

			break;

		case R.id.rightbutton:
			startActivity(new Intent(this, SettingsActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			break;

		case R.id.leftbutton_search:

			viewflipper.setInAnimation(RecommendAnimationHelper
					.inFromRightAnimation());
			viewflipper.setOutAnimation(RecommendAnimationHelper
					.outToLeftAnimation());
			viewflipper.setDisplayedChild(1);

			break;

		case R.id.continue_search:

			checkConnectivityForQuestionnarie(v.getContext(),
					accountOperationsObj.getData().getUser().getId(),
					userDetailsObj.getPassword());

			break;

		case R.id.inviteaFriend:

			sendMailInvite("Join Food Feedback!");

			break;

		case R.id.searchby_name:

			viewflipper.setInAnimation(RecommendAnimationHelper
					.inFromRightAnimation());
			viewflipper.setOutAnimation(RecommendAnimationHelper
					.outToLeftAnimation());
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

								ServiceKeys.SEARCH_BYNAME = 400;
								if (accountOperationsObj != null
										&& accountOperationsObj.getData() != null) {

									checkConnectivityForSearchCoaches(v
											.getContext(), Integer
											.toString(accountOperationsObj
													.getData().getUser()
													.getId()), userDetailsObj
											.getPassword(), v.getText()
											.toString(), "0");
								}

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

	@Override
	protected void onResume() {
		this.localyticsSession.open();
		System.out.println(" JanTest onresume() FoodGallery ");
		super.onResume();
	}

	/*
	 * class DelayToClear extends AsyncTask<Void, Void, Void> {
	 * 
	 * @Override protected Void doInBackground(Void... params) {
	 * 
	 * try { Thread.sleep(3000); } catch (InterruptedException e) {
	 * e.printStackTrace(); }
	 * 
	 * return null; }
	 * 
	 * @Override protected void onPostExecute(Void result) {
	 * super.onPostExecute(result); }
	 * 
	 * }
	 */

	@Override
	protected void onPostCreate(Bundle icicle) {
		System.out.println(" JanTest onPostCreate() Coaches ");
		super.onPostCreate(icicle);
	}

	@Override
	protected void onStart() {
		System.out.println(" JanTest onStart() Coaches ");
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	protected void updateCoachesUI() {
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