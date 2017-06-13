package com.foodfeedback.coaches;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodfeedback.myfood.ImageLoader;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.valueobjects.Coach;

public class CoachListViewAdapter extends BaseAdapter {
	private Activity mContext;
	private ArrayList<Coach> mCoachItems;
	ImageLoader imgLoader;
	int loader = R.drawable.loader;
	public static final int UPDATE_COACHES = 1000;
	public static final int UPDATE_PENDINGCOACHES = 1001;
	Typeface tfMedium, tfSpecial;

	public CoachListViewAdapter(Activity c, ArrayList<Coach> mCoachItemsParam) {
		mContext = c;
		imgLoader = new ImageLoader(mContext.getApplicationContext());
		mCoachItems = mCoachItemsParam;

		tfMedium = Typeface.createFromAsset(mContext.getAssets(), mContext
				.getResources().getString(R.string.font_standard));

		tfSpecial = Typeface.createFromAsset(mContext.getAssets(), mContext
				.getResources().getString(R.string.app_font_style_medium));
	}

	public int getCount() {
		return mCoachItems.size();
	}

	public Object getItem(int position) {
		return mCoachItems.get(position);
	}

	final int INVALID_ID = -1;

	@Override
	public long getItemId(int position) {
		if (position < 0 || position >= mCoachItems.size()) {
			return INVALID_ID;
		}
		Coach item = (Coach) getItem(position);
		if (item != null) {
			if (item.getMemberInfo() != null) {
				return item.getMemberInfo().getId();

			} else if (item.getmInvitation() != null) {
				return item.getmInvitation().getId();
			} else {
				return 0;
			}
		} else {
			return 0;
		}

	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		final Coach coachItem = mCoachItems.get(position);
		LayoutInflater mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			holder = new ViewHolder();
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		switch (coachItem.getItemType()) {
		case ServiceKeys.COACH_ITEM_TYPE_INVITATION:
			if (!holder.isPending) {
				// We dont have the right view. lets explode a new layout
				convertView = mInflater.inflate(R.layout.coaches_item, null);
				holder = new ViewHolder();
				holder.coachPhotoImageView = (ImageView) convertView
						.findViewById(R.id.coach_image);
				holder.coachNameTextView = (TextView) convertView
						.findViewById(R.id.coach_name);
				holder.coachNameTextView.setTypeface(tfMedium);
				holder.isCoach = false;
				holder.isPending = true;
				holder.isTitle = false;
				convertView.setTag(holder);
			} else {
				// We have the right view
				holder = (ViewHolder) convertView.getTag();
			}
			break;
		case ServiceKeys.COACH_ITEM_TYPE_MYCOACH:
			if (!holder.isCoach) {
				// We dont have the right view. lets explode a new layout
				convertView = mInflater.inflate(R.layout.coaches_item, null);
				holder = new ViewHolder();
				
				holder.coachPhotoImageView = (ImageView) convertView
						.findViewById(R.id.coach_image);
				holder.coachNameTextView = (TextView) convertView
						.findViewById(R.id.coach_name);
				holder.coachNameTextView.setTypeface(tfMedium);
				holder.isCoach = true;
				holder.isPending = false;
				holder.isTitle = false;
				convertView.setTag(holder);
			} else {
				// We have the right view
				holder = (ViewHolder) convertView.getTag();
			}

			break;
		case ServiceKeys.COACH_ITEM_TYPE_PENDINGTITLE:
			if (!holder.isTitle) {
				// We dont have the right view. lets explode a new layout
				convertView = mInflater.inflate(R.layout.pending_title_item,
						null);
				holder = new ViewHolder();
				holder.tvTitle= (TextView) convertView
						.findViewById(R.id.txt_pending_coaches);
				holder.tvTitle.setTypeface(tfSpecial);
				holder.isCoach = false;
				holder.isPending = false;
				holder.isTitle = true;
				convertView.setTag(holder);
			} else {
				// We have the right view
				holder = (ViewHolder) convertView.getTag();
			}
			break;
		}

		// Now that we have the right value. lets go ahead and set up the
		// content
		// Set defaults first
		
		
		if (holder.isCoach) {
			if (coachItem.getMemberInfo() != null) {
				holder.coachNameTextView.setText(coachItem.getMemberInfo()
						.getFirst_name()
						+ " "
						+ coachItem.getMemberInfo().getLast_name());
				if (coachItem.getMemberInfo().getProfile_image_thumb_url() != null
						& !coachItem.getMemberInfo()
								.getProfile_image_thumb_url().equals("")) {

					imgLoader.DisplayImage(coachItem.getMemberInfo()
							.getProfile_image_thumb_url(), loader,
							holder.coachPhotoImageView,false);
				} else {
					holder.coachPhotoImageView
							.setImageResource(R.drawable.placeholder_bio);
				}
			}
		} else if (holder.isPending) {
			if (coachItem.getmInvitation() != null
					&& coachItem.getmInvitation().getSender() != null) {
				String name = coachItem.getmInvitation().getSender()
						.getFirst_name()
						+ " "
						+ coachItem.getmInvitation().getSender().getLast_name();
				holder.coachNameTextView.setText(name);

				if (coachItem.getmInvitation().getSender()
						.getProfile_image_thumb_url() != null
						& !coachItem.getmInvitation().getSender()
								.getProfile_image_thumb_url().equals("")) {
					imgLoader.DisplayImage(coachItem.getmInvitation()
							.getSender().getProfile_image_thumb_url(), loader,
							holder.coachPhotoImageView,false);
				} else {
					holder.coachPhotoImageView
							.setImageResource(R.drawable.placeholder_bio);
				}

			}

		}
		return convertView;
	}

	static class ViewHolder {
		TextView coachNameTextView;
		TextView tvTitle;
		ImageView coachPhotoImageView;
		boolean isCoach = false;
		boolean isTitle = false;
		boolean isPending = false;

	}
}
