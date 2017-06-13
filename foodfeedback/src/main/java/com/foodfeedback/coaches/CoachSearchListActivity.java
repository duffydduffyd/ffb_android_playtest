package com.foodfeedback.coaches;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.foodfeedback.onboarding.FoodTabActivity;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.valueobjects.TransferObject;
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class CoachSearchListActivity extends Activity implements OnClickListener {
	private LocalyticsSession localyticsSession;
	private ListView coachesSearchList;
	private LinearLayout leftButton, rightButton;
	private TextView centreTitleTxt,txtCongratulationsLabel;
	int count = 0;
	String[] allCoachNames, allCoachBios, allThumbUrl, allProfileUrl;
	int[] allIds;
	private ImageView rithtButtonImage;
	int loader = R.drawable.loader;
	private ArrayList<Object> myCoachList;
	private CoachSearchListAdapter messageCoachListAdapter;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.coaches_search_screen);

		setUpViewIds();
		setupFonts();
		addOnClickListeners();
		updateSearchByNameCoaches();
		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources

		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("Coaches");

		this.localyticsSession.upload(); // upload any data

	}

	private void updateSearchByNameCoaches() {
		System.out.println(" Matches Coaches == "
				+ TransferObject.getSuggestedCoaches().getData()
						.getMatching_coaches().size());
		System.out.println(" Partially Coaches == "
				+ TransferObject.getSuggestedCoaches().getData()
						.getPartially_matching_coaches().size());
		myCoachList = new ArrayList<Object>();
		if (TransferObject.getSuggestedCoaches().getData() != null) {
			if (TransferObject.getSuggestedCoaches().getData()
					.getMatching_coaches().size() != 0) {

				for (int i = 0; i < TransferObject.getSuggestedCoaches()
						.getData().getMatching_coaches().size(); i++) {

					myCoachList.add(TransferObject.getSuggestedCoaches()
							.getData().getMatching_coaches().get(i));
				}
			}

			if (TransferObject.getSuggestedCoaches().getData()
					.getPartially_matching_coaches().size() != 0) {
				for (int j = 0; j < TransferObject.getSuggestedCoaches()
						.getData().getPartially_matching_coaches().size(); j++) {
					myCoachList.add(TransferObject.getSuggestedCoaches()
							.getData().getPartially_matching_coaches().get(j));
				}
			}
		}

		ArrayList<Object> uniques = new ArrayList<Object>();
		for (Object element : myCoachList) {
			if (!uniques.contains(element)) {
				uniques.add(element);
			}
		}

		//uniques = CoachOrderingCacheManager.rearrangeCoachList(Controller.getAppBackgroundContext(), uniques);
		messageCoachListAdapter = new CoachSearchListAdapter(this,
				uniques);
		coachesSearchList.setAdapter(messageCoachListAdapter);
	}

	private void addOnClickListeners() {

		leftButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
	}

	private void setupFonts() {
		
		Typeface tfSpecial = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.app_font_style_medium));
		Typeface tfNormal = Typeface.createFromAsset(this.getAssets(), this
				.getResources().getString(R.string.font_standard));
		
		centreTitleTxt.setTypeface(tfSpecial);
		txtCongratulationsLabel.setTypeface(tfNormal);
	}

	private void setUpViewIds() {

		centreTitleTxt = (TextView) findViewById(R.id.center_txt);
		centreTitleTxt.setText(getResources().getString(R.string.coaches));
		leftButton = (LinearLayout) findViewById(R.id.leftbutton);
		rightButton = (LinearLayout) findViewById(R.id.rightbutton);
		// rightButton.setVisibility(View.GONE);
		rithtButtonImage = (ImageView) findViewById(R.id.right_button);
		rithtButtonImage.setImageResource(R.drawable.close);
		txtCongratulationsLabel =  (TextView) findViewById(R.id.txtCongratulationsLabel);
		coachesSearchList = (ListView) findViewById(R.id.search_listView);
	}

	// private ImageView addRow(String name, String bio, int id, String
	// thumbUrl,
	// String profileUrl) {}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.leftbutton:

			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			finish();
			break;

		case R.id.rightbutton:

			Intent intentFood = new Intent(this, FoodTabActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			startActivity(intentFood);

			break;

		}
	}

	@Override
	public void onBackPressed() {

		System.out.println("onBackPressed");
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
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
