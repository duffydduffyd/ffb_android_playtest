package com.foodfeedback.myfood;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.foodfeedback.onboarding.R;
import com.foodfeedback.valueobjects.FoodImagePost;

public class ImageAdapter extends BaseAdapter {
	private Activity mContext;
	public ArrayList<FoodImagePost> foodImages;
	ImageLoader imgLoader;
	int loader = R.drawable.placeholder_gallery;
	int size;
	DisplayMetrics metrics;
	
	public ImageAdapter(Activity c, ArrayList<FoodImagePost> mfoodImages) {
		imgLoader = new ImageLoader(c.getApplicationContext());
		mContext = c;
		foodImages = mfoodImages;
		size = mContext.getResources().getInteger(R.integer.icon_size);
		metrics = new DisplayMetrics();
		mContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
	}

	public int getCount() {
		return foodImages.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder; 
		
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = mInflater.inflate(R.layout.gallery_item, parent, false);
			holder.imageView = (ImageView)convertView.findViewById(R.id.imgHolder);
			convertView.setTag(holder);
		} else {
			 holder = (ViewHolder ) convertView.getTag();
		}
		imgLoader.DisplayImage(foodImages.get(position).getImage_thumb_url(),
				loader, holder.imageView,false);
		return convertView;
	}
	
	static class ViewHolder { 
	    ImageView imageView; 
	}
}
