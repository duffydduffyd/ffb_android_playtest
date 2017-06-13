package com.foodfeedback.messages;

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
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.foodfeedback.cachemanager.ChatStatusNumberCacheManager;
import com.foodfeedback.cachemanager.UnReadCacheManager;
import com.foodfeedback.coaches.CoachDetailActivity;
import com.foodfeedback.myfood.ImageLoader;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.valueobjects.ChatNumberUpdate;
import com.foodfeedback.valueobjects.MemberInfo;
import com.foodfeedback.valueobjects.MessageList;

public class MessageCoachListAdapter extends BaseAdapter {
	private Activity mContext;
	private ArrayList<MemberInfo> mCoachItems;
	ImageLoader imgLoader;
	int loader = R.drawable.loader;

	public static final int UPDATE_MESSAGESS = 1004;
	public MessageList unReadMessages;
	public int numberStatusCount = 0;
	Typeface tfMedium;
	private int typeOfScreen;
	public ChatNumberUpdate chatNumberUpdate;
	final int INVALID_ID = -1;

	public MessageCoachListAdapter(Activity c,
			ArrayList<MemberInfo> mCoachItemsParam, int mTypeOfScreen) {
		this.typeOfScreen = mTypeOfScreen;
		try {
			unReadMessages = UnReadCacheManager.getObject(Controller
					.getAppBackgroundContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		mContext = c;
		imgLoader = new ImageLoader(mContext.getApplicationContext());
		mCoachItems = mCoachItemsParam;

		// Compare mCoachItems with the ordered mCoachItems in the cached and
		// save again
		tfMedium = Typeface.createFromAsset(mContext.getAssets(), mContext
				.getResources().getString(R.string.font_standard));
	}

	public int getCount() {
		return mCoachItems.size();
	}

	public Object getItem(int position) {
		return mCoachItems.get(position);
	}

	@SuppressWarnings("static-access")
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
		final MemberInfo coachItem = mCoachItems.get(position);

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.messages_item, null);
			holder.statusUpdateList = (TextView) convertView
					.findViewById(R.id.statusUpdateList);
			holder.statusUpdateList.setTypeface(tfMedium);
			holder.coachNameTextView = (TextView) convertView
					.findViewById(R.id.coach_name);
			holder.coachNameTextView.setTypeface(tfMedium);
			holder.coachPhotoImageView = (ImageView) convertView
					.findViewById(R.id.coach_image);
			convertView.setTag(holder);

		}
		holder = (ViewHolder) convertView.getTag();
		if (unReadMessages!=null &&  unReadMessages.getData()!=null){
			for (int i = 0; i < unReadMessages.getData().size(); i++) {
				
				if (coachItem.getId() == Integer.parseInt(unReadMessages.getData()
						.get(i).getSender_id())) {
					numberStatusCount++;
				}
			}
			
		}

		try {
			if (ChatStatusNumberCacheManager.getObject(
					Controller.getAppBackgroundContext(), coachItem.getId()
							+ "") != null) {
				chatNumberUpdate = ChatStatusNumberCacheManager.getObject(
						Controller.getAppBackgroundContext(), coachItem.getId()
								+ "");
				numberStatusCount = numberStatusCount
						+ chatNumberUpdate.getNumberUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (numberStatusCount != 0) {
			holder.statusUpdateList.setVisibility(View.VISIBLE);
			holder.statusUpdateList.setText("" + numberStatusCount);
			numberStatusCount = 0;
		} else {
			holder.statusUpdateList.setVisibility(View.GONE);
		}

		if (coachItem != null) {
			holder.coachNameTextView.setText(coachItem.getFirst_name() + " "
					+ coachItem.getLast_name());
			if (coachItem.getProfile_image_thumb_url() != null
					& !coachItem.getProfile_image_thumb_url().equals("")) {

				imgLoader.DisplayImage(coachItem.getProfile_image_thumb_url(),
						loader, holder.coachPhotoImageView,false);
			} else {
				holder.coachPhotoImageView
						.setImageDrawable(mContext.getResources().getDrawable(
								R.drawable.placeholder_bio));
				holder.coachPhotoImageView
						.setScaleType(ScaleType.CENTER_INSIDE);
			}
		}

		if (this.typeOfScreen == ServiceKeys.SEARCH_BYNAME_COACHES) {
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(),
							CoachDetailActivity.class);
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
					v.getContext().startActivity(intent);

				}
			});

		} else if (this.typeOfScreen == ServiceKeys.SEARCH_BYNAME_MESSAGES) {

		}

		return convertView;
	}

	static class ViewHolder {
		TextView coachNameTextView;
		ImageView coachPhotoImageView;
		TextView statusUpdateList;
	}

	

	@Override
	public long getItemId(int position) {
		if (position < 0 || position >= mCoachItems.size()) {
			return INVALID_ID;
		}
		MemberInfo item = (MemberInfo) getItem(position);
		return item.getId();

	}

}
