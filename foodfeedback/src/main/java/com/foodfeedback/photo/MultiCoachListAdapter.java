package com.foodfeedback.photo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foodfeedback.myfood.ImageLoader;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.valueobjects.MemberInfo;

public class MultiCoachListAdapter extends BaseAdapter {
	private Activity mContext;
	private ArrayList<MemberInfo> mCoachItems;
	ImageLoader imgLoader;
	int loader = R.drawable.placeholder_bio;
	private TextView coachNameTextView;
	private CheckBox checkBox;
	private ImageView coachPhotoImageView;
	public static final int UPDATE_MESSAGESS = 1004;
	public int numberStatusCount = 0;
	public TextView statusUpdateList;
	private Button continueButton;
	private LinearLayout leftButton;
	private String[] selectedIDsOfCoachesArray;
	ArrayList<String> selectedCoachesList;
	Typeface tfNormal;

	public MultiCoachListAdapter(Activity c,
			ArrayList<MemberInfo> mCoachItemsParam, Button continueButton,
			LinearLayout leftButton, String[] selectedIDsOfCoachesArray) {
		mContext = c;
		tfNormal =Typeface.createFromAsset(
				mContext.getAssets(),
				mContext.getResources().getString(R.string.font_standard));
		
		
		imgLoader = new ImageLoader(mContext.getApplicationContext());
		mCoachItems = mCoachItemsParam;
		this.continueButton = continueButton;
		this.leftButton = leftButton;
		this.selectedIDsOfCoachesArray = selectedIDsOfCoachesArray;
		selectedCoachesList = new ArrayList<String>();

		for (int i = 0; i < selectedIDsOfCoachesArray.length; i++) {
			if (selectedIDsOfCoachesArray[i] != null
					&& !selectedIDsOfCoachesArray[i].trim().equals("")) {

				selectedCoachesList.add(selectedIDsOfCoachesArray[i]);
			}
		}

		if (selectedCoachesList.size() > 0) {
			this.continueButton.setEnabled(true);
			continueButton.setBackgroundResource(R.drawable.redbar_button);
		} else {
			this.continueButton.setEnabled(false);
			this.continueButton
					.setBackgroundResource(R.drawable.unfocus_redimage);
		}
		this.continueButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out
						.println("CONTINUE: Number of coaches finally selected = "
								+ selectedCoachesList.size());
				Intent intentObj = new Intent(mContext.getIntent());
				// intentObj.putExtra("SelectedIds",
				// selectedCoachesList.toArray());
				String selectedCoachesToSend = "";
				for (int i = 0; i < selectedCoachesList.size(); i++) {
					if (selectedCoachesToSend.equals("")) {
						selectedCoachesToSend = selectedCoachesList.get(i);
					} else {
						selectedCoachesToSend = selectedCoachesToSend + ","
								+ selectedCoachesList.get(i);
					}
				}
				intentObj.putExtra("SelectedIds", selectedCoachesToSend);
				mContext.setResult(mContext.RESULT_OK, intentObj);
				mContext.finish();

			}
		});

		this.leftButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out
						.println("BACK BUTTON ON TOP BAR: Number of coaches finally selected = "
								+ selectedCoachesList.size());
				Intent intentObj = new Intent(mContext.getIntent());
				// intentObj.putExtra("SelectedIds",
				// selectedCoachesList.toArray());
				String selectedCoachesToSend = "";
				for (int i = 0; i < selectedCoachesList.size(); i++) {
					if (selectedCoachesToSend.equals("")) {
						selectedCoachesToSend = selectedCoachesList.get(i);
					} else {
						selectedCoachesToSend = selectedCoachesToSend + ","
								+ selectedCoachesList.get(i);
					}
				}
				intentObj.putExtra("SelectedIds", selectedCoachesToSend);
				mContext.setResult(mContext.RESULT_OK, intentObj);
				mContext.finish();
			}
		});

	}

	public int getCount() {
		return mCoachItems.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	@SuppressWarnings("static-access")
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

		convertView = inflater.inflate(R.layout.select_coach_item, null);
		statusUpdateList = (TextView) convertView
				.findViewById(R.id.statusUpdateList);
		final MemberInfo coachItem = mCoachItems.get(position);

		coachNameTextView = (TextView) convertView
				.findViewById(R.id.coach_name);
		coachNameTextView.setTypeface(tfNormal);
		coachPhotoImageView = (ImageView) convertView
				.findViewById(R.id.coach_image);
		checkBox = (CheckBox) convertView.findViewById(R.id.chk);
		checkBox.setId(coachItem.getId());

		if (coachItem != null) {
			coachNameTextView.setText(coachItem.getFirst_name() + " "
					+ coachItem.getLast_name());
			if (coachItem.getProfile_image_thumb_url() != null
					& !coachItem.getProfile_image_thumb_url().equals("")) {

				imgLoader.DisplayImage(coachItem.getProfile_image_thumb_url(),
						loader, coachPhotoImageView,false);
			}
		}

		if (selectedIDsOfCoachesArray != null
				&& selectedIDsOfCoachesArray.length > 0) {
			// There are some items already selected
			if (selectedCoachesList.contains(String.valueOf(coachItem.getId()))) {
				// This is the ID of the coach being displayed
				System.out.println("The coach with ID "
						+ String.valueOf(coachItem.getId())
						+ " has already been selected");
				checkBox.setChecked(true);
			} else {
				checkBox.setChecked(false);
			}
		}

		checkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View viewThatWasClicked) {

				CheckBox tempCheckBox = (CheckBox) viewThatWasClicked;
				
				if (tempCheckBox.isChecked()) {
					int IDOFSelectedCoach = tempCheckBox.getId();
					selectedCoachesList.add("" + IDOFSelectedCoach);
				} else {
					if (selectedCoachesList.contains(String
							.valueOf(tempCheckBox.getId()))) {
						// Remove from the list
						selectedCoachesList.remove(String.valueOf(tempCheckBox
								.getId()));
					} else {
						// Do nothing
					}
				}

				if (selectedCoachesList.size() > 0) {
					continueButton.setEnabled(true);
					continueButton
							.setBackgroundResource(R.drawable.redbar_button);
				} else {
					continueButton.setEnabled(false);
					continueButton
							.setBackgroundResource(R.drawable.unfocus_redimage);
				}
			}
		});

		return convertView;
	}

}
