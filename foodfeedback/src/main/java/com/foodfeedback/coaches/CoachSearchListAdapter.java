package com.foodfeedback.coaches;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodfeedback.myfood.ImageLoader;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.valueobjects.MatchingCoaches;
import com.foodfeedback.valueobjects.PartiallyMatchingCoaches;

public class CoachSearchListAdapter extends BaseAdapter {
	private Activity mContext;
	private ArrayList<Object> mCoachItems;
	ImageLoader imgLoader;
	int loader = R.drawable.loader;
	private TextView coachNameTextView;
	private ImageView coachPhotoImageView;
	public static final int UPDATE_MESSAGESS = 1004;
	public int numberStatusCount = 0;
	MatchingCoaches matchingCoaches;
	PartiallyMatchingCoaches partiallyMatchingCoaches;
	Typeface tfNormal;
	public CoachSearchListAdapter(Activity c,
			ArrayList<Object> mCoachItemsParam) {
		System.out.println(" Object Size == " + mCoachItemsParam.size());
		mContext = c;
		imgLoader = new ImageLoader(mContext.getApplicationContext());
		mCoachItems = mCoachItemsParam;
		tfNormal = Typeface.createFromAsset(mContext.getAssets(), mContext
				.getResources().getString(R.string.font_standard));
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

		convertView = inflater.inflate(R.layout.coaches_item, null);
		final Object coachItem = mCoachItems.get(position);

		coachNameTextView = (TextView) convertView
				.findViewById(R.id.coach_name);
		coachNameTextView.setTypeface(tfNormal);
		coachPhotoImageView = (ImageView) convertView
				.findViewById(R.id.coach_image);

		if (coachItem != null) {

			if (coachItem instanceof MatchingCoaches) {
				matchingCoaches = (MatchingCoaches) coachItem;
				coachNameTextView.setText(matchingCoaches.getFirst_name() + " "
						+ matchingCoaches.getLast_name());
				if (matchingCoaches.getProfile_image_thumb_url() != null
						& !matchingCoaches.getProfile_image_thumb_url().equals(
								"")) {

					imgLoader.DisplayImage(
							matchingCoaches.getProfile_image_thumb_url(),
							loader, coachPhotoImageView,false);
				}
			}

			if (coachItem instanceof PartiallyMatchingCoaches) {
				partiallyMatchingCoaches = (PartiallyMatchingCoaches) coachItem;
				coachNameTextView.setText(partiallyMatchingCoaches
						.getFirst_name()
						+ " "
						+ partiallyMatchingCoaches.getLast_name());
				if (partiallyMatchingCoaches.getProfile_image_thumb_url() != null
						& !partiallyMatchingCoaches
								.getProfile_image_thumb_url().equals("")) {

					imgLoader.DisplayImage(partiallyMatchingCoaches
							.getProfile_image_thumb_url(), loader,
							coachPhotoImageView,false);
				}
			}

		}

		
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Object coachItemClick = mCoachItems.get(position);
				if (coachItemClick instanceof MatchingCoaches) {
					Intent intent = new Intent(v.getContext(),
							CoachDetailActivity.class);
					intent.putExtra(
							"coachName",
							((MatchingCoaches) mCoachItems.get(position))
									.getFirst_name()
									+ " "
									+ ((MatchingCoaches) mCoachItems
											.get(position)).getLast_name());
					intent.putExtra("coachBio", ((MatchingCoaches) mCoachItems
							.get(position)).getBio());
					intent.putExtra("userId", ((MatchingCoaches) mCoachItems
							.get(position)).getId());
					intent.putExtra("thumbUrl", ((MatchingCoaches) mCoachItems
							.get(position)).getProfile_image_thumb_url());
					intent.putExtra("profileUrl",
							((MatchingCoaches) mCoachItems.get(position))
									.getProfile_image_url());
					mContext.startActivity(intent);

				}
				if (coachItemClick instanceof PartiallyMatchingCoaches) {
					Intent intent = new Intent(v.getContext(),
							CoachDetailActivity.class);
					intent.putExtra(
							"coachName",
							((PartiallyMatchingCoaches) mCoachItems
									.get(position)).getFirst_name()
									+ " "
									+ ((PartiallyMatchingCoaches) mCoachItems
											.get(position)).getLast_name());
					intent.putExtra("coachBio",
							((PartiallyMatchingCoaches) mCoachItems
									.get(position)).getBio());
					intent.putExtra("userId",
							((PartiallyMatchingCoaches) mCoachItems
									.get(position)).getId());
					intent.putExtra("thumbUrl",
							((PartiallyMatchingCoaches) mCoachItems
									.get(position))
									.getProfile_image_thumb_url());
					intent.putExtra("profileUrl",
							((PartiallyMatchingCoaches) mCoachItems
									.get(position)).getProfile_image_url());
					mContext.startActivity(intent);

				}
			}
		});

		return convertView;
	}
}
