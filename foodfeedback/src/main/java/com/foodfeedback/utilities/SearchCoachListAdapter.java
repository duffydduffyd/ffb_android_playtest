package com.foodfeedback.utilities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodfeedback.cachemanager.InteractedWithCacheManager;
import com.foodfeedback.coaches.CoachDeleteActivity;
import com.foodfeedback.coaches.CoachDetailActivity;
import com.foodfeedback.myfood.ImageLoader;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.onboarding.ChatActivity;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.valueobjects.Coach;
import com.foodfeedback.valueobjects.InteractedWith;
import com.foodfeedback.valueobjects.MemberInfo;

public class SearchCoachListAdapter extends BaseAdapter {
	private Activity mContext;
	private ArrayList<MemberInfo> mCoachItems;
	ImageLoader imgLoader;
	int loader = R.drawable.loader;
	private TextView coachNameTextView;
	private ImageView coachPhotoImageView;
	public static final int UPDATE_MESSAGESS = 1004;
	public int numberStatusCount = 0;
	private int typeOfScreen;

	public SearchCoachListAdapter(Activity c,
			ArrayList<MemberInfo> mCoachItemsParam, int mTypeOfScreen) {
		this.typeOfScreen = mTypeOfScreen;
		mContext = c;
		imgLoader = new ImageLoader(mContext.getApplicationContext());
		mCoachItems = mCoachItemsParam;
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
		final MemberInfo coachItem = mCoachItems.get(position);

		coachNameTextView = (TextView) convertView
				.findViewById(R.id.coach_name);
		coachPhotoImageView = (ImageView) convertView
				.findViewById(R.id.coach_image);

		if (coachItem != null) {
			coachNameTextView.setText(coachItem.getFirst_name() + " "
					+ coachItem.getLast_name());
			if (coachItem.getProfile_image_thumb_url() != null
					& !coachItem.getProfile_image_thumb_url().equals("")) {

				imgLoader.DisplayImage(coachItem.getProfile_image_thumb_url(),
						loader, coachPhotoImageView,false);
			}
		}
		if (this.typeOfScreen == ServiceKeys.SEARCH_BYNAME_COACHES) {
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Find out if he is already a coach and act accordingly
					boolean isAlreadyCoach = false;
					ArrayList<Coach> myCoachList = new ArrayList<Coach>();
					InteractedWith coachListObj = null;
					try {
						coachListObj = InteractedWithCacheManager
								.getObject(Controller.getAppBackgroundContext());
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (coachListObj != null) {

						for (int i = 0; i < coachListObj.getData().size(); i++) {
							if (coachListObj.getData().get(i).getId() == mCoachItems
									.get(position).getId()) {
								isAlreadyCoach = true;
								break;
							}
						}
					}
					Intent intent;
					if (!isAlreadyCoach) {
						intent = new Intent(v.getContext(),
								CoachDetailActivity.class);

					} else {
						intent = new Intent(v.getContext(),
								CoachDeleteActivity.class);

					}
					intent.putExtra("coachName", mCoachItems.get(position)
							.getFirst_name()
							+ " "
							+ mCoachItems.get(position).getLast_name());
					intent.putExtra("coachBio", mCoachItems.get(position)
							.getBio());
					intent.putExtra("userId", mCoachItems.get(position).getId());
					intent.putExtra("thumbUrl", mCoachItems.get(position)
							.getProfile_image_thumb_url());
					intent.putExtra("profileUrl", mCoachItems.get(position)
							.getProfile_image_url());
					mContext.startActivity(intent);

				}
			});

		} else if (this.typeOfScreen == ServiceKeys.SEARCH_BYNAME_MESSAGES) {
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(),
							ChatActivity.class);
					Log.i("FoodFeedback", " The coach id is "
							+ mCoachItems.get(position).getId());
					intent.putExtra("coachID", mCoachItems.get(position)
							.getId() + "");
					intent.putExtra("message", "NOT");
					intent.putExtra("checkNotify", "NOT");
					mContext.startActivityForResult(intent, UPDATE_MESSAGESS);

				}
			});

		}

		return convertView;
	}
}
