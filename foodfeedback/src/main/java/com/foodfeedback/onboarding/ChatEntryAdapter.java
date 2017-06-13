package com.foodfeedback.onboarding;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foodfeedback.messages.MessageObject;
import com.foodfeedback.messages.MessagePhotoView;
import com.foodfeedback.myfood.ImageLoader;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.valueobjects.TransferObject;

/**
 * Adapts ChatEntry objects onto views for lists
 */
public final class ChatEntryAdapter extends ArrayAdapter<MessageObject> {

	private final int newsItemLayoutResource;
	ImageLoader imgLoader;
	int loader = R.drawable.loader;
	public MessageObject entry;
	public ViewHolder viewHolder;
	public String userId;
	Context mContext;
	Bitmap meBitmap, otherBitmap;
	Typeface tfNormal;
	

	public ChatEntryAdapter(final Context context,
			final int newsItemLayoutResource, String userId) {
		super(context, 0);
		this.mContext = context;

		tfNormal = Typeface.createFromAsset(mContext.getAssets(), mContext
				.getResources().getString(R.string.font_standard));
		this.newsItemLayoutResource = newsItemLayoutResource;
		imgLoader = new ImageLoader(context.getApplicationContext());
		this.userId = userId;

		Bitmap bitmapObj1 = BitmapFactory.decodeResource(
				context.getResources(), R.drawable.placeholder_bio);
		TransferObject.setDefaultBitmapImage(bitmapObj1);
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {

		// We need to get the best view (re-used if possible) and then
		// retrieve its corresponding ViewHolder, which optimizes lookup
		// efficiency
		final View view = getWorkingView(convertView, position);

		return view;
	}

	private View getWorkingView(final View convertView, int pos) {
		// The workingView is basically just the convertView re-used if possible
		// or inflated new if not possible
		View workingView = null;

		if (null == convertView) {
			final Context context = getContext();
			final LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			workingView = inflater.inflate(newsItemLayoutResource, null);

		} else {
			workingView = convertView;
		}

		viewHolder = getViewHolder(workingView);
		entry = getItem(pos);

		switch (entry.getCheckInOutStatus()) {

		case ServiceKeys.CHECK_OUTGOING_STATUS:
			viewHolder.loadMoreMessages.setVisibility(View.GONE);
			viewHolder.outgoingTextCheck.setVisibility(View.GONE);
			viewHolder.outgoingImageCheck.setVisibility(View.GONE);

			viewHolder.incomingTextCheck.setVisibility(View.VISIBLE);

			switch (entry.getCheckImageStatus()) {

			case ServiceKeys.CHAT_IMAGE_NO_STATUS:
				viewHolder.incomingImageCheck.setVisibility(View.GONE);
				viewHolder.incomingText.setText(entry.getTextChat());
				viewHolder.incomingDate.setText(entry.getFormattedTimeChat(mContext));
				break;

			case ServiceKeys.CHAT_IMAGE_YES_STATUS:
				viewHolder.incomingImageCheck.setVisibility(View.VISIBLE);
				
				if (entry.getTextChat()!=null&&!entry.getTextChat().trim().equals("")){
					viewHolder.incomingText.setText(entry.getTextChat());
					viewHolder.incomingDate.setText(entry.getFormattedTimeChat(mContext));	
				}else{
					viewHolder.incomingTextCheck.setVisibility(View.GONE);
				}
				
				viewHolder.incomingImageBg.setImageBitmap(null);
				imgLoader.DisplayImage(entry.getPhotoBgPath(), loader,
						viewHolder.incomingImageBg,false);
				
				//new DownloadImageTask(viewHolder.incomingImageBg, this.mContext).execute(entry.getPhotoBgPath());
				
				
//				viewHolder.incomingImageBg
//						.setImageResource(R.drawable.foodimagebg);

				viewHolder.incomingImageThumb
						.setImageResource(R.drawable.placeholder_bio);

				
				
				


//				 viewHolder.incomingImageBg.getBackground().setColorFilter(Color.parseColor("BLACK"),
//				 PorterDuff.Mode.ADD);
				
				imgLoader.DisplayImage(entry.getProfileBgPath(), loader,
						viewHolder.incomingImageThumb,false);
				
				

				viewHolder.incomingImageBg.setTag(entry.getMessageImageID()
						+ "##" + entry.getPhotoBgPath()+"##" + entry.getMessageID());
				viewHolder.incomingImageBg
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								System.out.println("ON CLICK ----- "
										+ v.getTag());
								Intent intent = new Intent(v.getContext(),
										MessagePhotoView.class);
								// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								String[] listOfURLSToDisplay = new String[1];
								String urlAndID = (String) v.getTag();
								String url = urlAndID.split("##")[1];
								String imageID = urlAndID.split("##")[0];
								String messageID = urlAndID.split("##")[2];
								listOfURLSToDisplay[0] = url;
								Bundle b = new Bundle();
								b.putInt("clickedPicture", 1);
								b.putString("IDOfPhotoToShow", imageID);
								b.putString("UserInteractingWithID", userId);
								b.putString("messageID", messageID);
								b.putStringArray("listOfURLS",
										listOfURLSToDisplay);
								b.putBoolean("myPicture", true);
								intent.putExtras(b);
								v.getContext().startActivity(intent);
							}
						});

				viewHolder.incomingImageThumb.setTag(entry
						.getMessageImageID() + "##" + entry.getPhotoBgPath()+"##" + entry.getMessageID());
				viewHolder.incomingImageThumb
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								System.out.println("ON CLICK ----- "
										+ v.getTag());
								Intent intent = new Intent(v.getContext(),
										MessagePhotoView.class);
								// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								String[] listOfURLSToDisplay = new String[1];
								String urlAndID = (String) v.getTag();
								String url = urlAndID.split("##")[1];
								String imageID = urlAndID.split("##")[0];
								String messageID = urlAndID.split("##")[2];
								listOfURLSToDisplay[0] = url;

								Bundle b = new Bundle();
								b.putInt("clickedPicture", 1);
								b.putString("IDOfPhotoToShow", imageID);
								b.putString("UserInteractingWithID", userId);
								b.putString("messageID", messageID);
								b.putBoolean("myPicture", true);

								b.putStringArray("listOfURLS",
										listOfURLSToDisplay);
								intent.putExtras(b);
								v.getContext().startActivity(intent);
							}
						});
				// viewHolder.incomingImageBg.setBackgroundColor(null);
				// viewHolder.incomingImageProfile.setBackgroundColor(mContext.getResources().getColor(R.color.trans));
				break;
			}

			
			//new DownloadImageTask(viewHolder.incomingProfile, mContext).execute(entry.getProfilePath());
			if (entry.getProfilePath()!=null && !entry.getProfilePath().equals("")){
				imgLoader.DisplayImage(entry.getProfilePath(), loader,
						viewHolder.incomingProfile,false);
				
			}else{
				viewHolder.incomingProfile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.placeholder_gallery));
			}

			break;

		case ServiceKeys.CHECK_INCOMING_STATUS:

			viewHolder.outgoingTextCheck.setVisibility(View.VISIBLE);
			viewHolder.loadMoreMessages.setVisibility(View.GONE);
			viewHolder.incomingTextCheck.setVisibility(View.GONE);
			viewHolder.incomingImageCheck.setVisibility(View.GONE);

			switch (entry.getCheckImageStatus()) {

			case ServiceKeys.CHAT_IMAGE_NO_STATUS:
				viewHolder.outgoingImageCheck.setVisibility(View.GONE);
				viewHolder.outgoingTextCheck.setVisibility(View.VISIBLE);
				viewHolder.outgoingText.setText(entry.getTextChat());
				viewHolder.outgoingDate.setText(entry.getFormattedTimeChat(mContext));
				break;

			case ServiceKeys.CHAT_IMAGE_YES_STATUS:
				viewHolder.outgoingImageCheck.setVisibility(View.VISIBLE);
				
				
				if (entry.getTextChat()!=null&&!entry.getTextChat().trim().equals("")){
					viewHolder.outgoingText.setText(entry.getTextChat());
					viewHolder.outgoingDate.setText(entry.getFormattedTimeChat(mContext));
				}else{
					viewHolder.outgoingTextCheck.setVisibility(View.GONE);
				}
				
//				viewHolder.outgoingImageBg
//						.setImageResource(R.drawable.foodimagebg);
				
				viewHolder.outgoingImageBg.setImageBitmap(null);
				
				viewHolder.outgoingImageThumb
						.setImageResource(R.drawable.placeholder_bio);

				imgLoader.DisplayImage(entry.getPhotoBgPath(), loader,
						viewHolder.outgoingImageBg,false);
				imgLoader.DisplayImage(entry.getProfileBgPath(), loader,
						viewHolder.outgoingImageThumb,false);
				
				
				viewHolder.outgoingImageThumb.setTag(entry
						.getMessageImageID() + "##" + entry.getPhotoBgPath()+"##" + entry.getMessageID());

				viewHolder.outgoingImageThumb
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								System.out.println("ON CLICK ----- "
										+ v.getTag());
								Intent intent = new Intent(v.getContext(),
										MessagePhotoView.class);
								// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								String[] listOfURLSToDisplay = new String[1];
								String urlAndID = (String) v.getTag();
								String url = urlAndID.split("##")[1];
								String imageID = urlAndID.split("##")[0];
								String messageID = urlAndID.split("##")[2];
								listOfURLSToDisplay[0] = url;

								Bundle b = new Bundle();
								b.putInt("clickedPicture", 1);

								b.putBoolean("myPicture", false);
								b.putString("IDOfPhotoToShow", imageID);
								b.putString("UserInteractingWithID", userId);
								b.putString("messageID", messageID);
								b.putStringArray("listOfURLS",
										listOfURLSToDisplay);
								intent.putExtras(b);
								v.getContext().startActivity(intent);
							}
						});

				viewHolder.outgoingImageBg.setTag(entry.getMessageImageID()
						+ "##" + entry.getPhotoBgPath()+"##" + entry.getMessageID());
				viewHolder.outgoingImageBg
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								System.out.println("ON CLICK ----- "
										+ v.getTag());
								Intent intent = new Intent(v.getContext(),
										MessagePhotoView.class);
								// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								String[] listOfURLSToDisplay = new String[1];
								String urlAndID = (String) v.getTag();
								String url = urlAndID.split("##")[1];
								String imageID = urlAndID.split("##")[0];
								String messageID = urlAndID.split("##")[2];
								listOfURLSToDisplay[0] = url;

								Bundle b = new Bundle();
								b.putInt("clickedPicture", 1);
								b.putString("IDOfPhotoToShow", imageID);
								b.putString("UserInteractingWithID", userId);
								b.putString("messageID", messageID);
								b.putBoolean("myPicture", false);
								b.putStringArray("listOfURLS",
										listOfURLSToDisplay);
								intent.putExtras(b);
								v.getContext().startActivity(intent);
							}
						});
				// viewHolder.outgoingImageBg.setBackgroundColor(mContext.getResources().getColor(R.color.trans));
				// viewHolder.outgoingImageProfile.setBackgroundColor(mContext.getResources().getColor(R.color.trans));

				break;
			}

			
//			new DownloadImageTask(viewHolder.outgoingProfile, mContext).execute(entry.getProfilePath());
			
			if (entry.getProfilePath()!=null && !entry.getProfilePath().equals("")){
				imgLoader.DisplayImage(entry.getProfilePath(), loader,
						viewHolder.outgoingProfile,false);
				
			}else{
				viewHolder.outgoingProfile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.placeholder_gallery));
			}

			break;

		case 0:
			viewHolder.outgoingTextCheck.setVisibility(View.GONE);
			viewHolder.incomingTextCheck.setVisibility(View.GONE);
			viewHolder.loadMoreMessages.setVisibility(View.VISIBLE);
			viewHolder.outgoingImageCheck.setVisibility(View.GONE);
			viewHolder.incomingImageCheck.setVisibility(View.GONE);

			break;
		}

		return workingView;
	}

	private ViewHolder getViewHolder(final View workingView) {
		// The viewHolder allows us to avoid re-looking up view references
		// Since views are recycled, these references will never change
		final Object tag = workingView.getTag();
		ViewHolder viewHolder = null;

		if (null == tag || !(tag instanceof ViewHolder)) {
			viewHolder = new ViewHolder();

			// incoming images
			viewHolder.incomingImageBg = (ImageView) workingView
					.findViewById(R.id.incoming_bgphoto);
			viewHolder.incomingImageThumb = (ImageView) workingView
					.findViewById(R.id.incoming_profilebg);
			// incoming text, date.
			viewHolder.incomingProfile = (ImageView) workingView
					.findViewById(R.id.incoming_profile_photo_holder);

			viewHolder.incomingText = (TextView) workingView
					.findViewById(R.id.incoming_data);
			viewHolder.incomingText.setTypeface(tfNormal);

			viewHolder.incomingDate = (TextView) workingView
					.findViewById(R.id.incoming_time);
			viewHolder.incomingDate.setTypeface(tfNormal);
			// outgoing images
			viewHolder.outgoingImageBg = (ImageView) workingView
					.findViewById(R.id.outgoing_photobg);
			viewHolder.outgoingImageThumb = (ImageView) workingView
					.findViewById(R.id.outgoing_profilebg);
			// outgoing text, date.
			viewHolder.outgoingProfile = (ImageView) workingView
					.findViewById(R.id.outgoing_profile_image_holder);

			viewHolder.outgoingText = (TextView) workingView
					.findViewById(R.id.outgoing_date);
			viewHolder.outgoingText.setTypeface(tfNormal);
			viewHolder.outgoingDate = (TextView) workingView
					.findViewById(R.id.outgoing_time);
			viewHolder.outgoingDate.setTypeface(tfNormal);

			viewHolder.incomingImageCheck = (LinearLayout) workingView
					.findViewById(R.id.image_incoming);
			viewHolder.incomingTextCheck = (LinearLayout) workingView
					.findViewById(R.id.text_incoming);

			viewHolder.outgoingImageCheck = (LinearLayout) workingView
					.findViewById(R.id.image_outgoing);
			viewHolder.outgoingTextCheck = (LinearLayout) workingView
					.findViewById(R.id.text_outgoing);

			viewHolder.loadMoreMessages = (TextView) workingView
					.findViewById(R.id.load_moremessages);
			viewHolder.loadMoreMessages.setTypeface(tfNormal);

			workingView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) tag;
		}

		return viewHolder;
	}

	/**
	 * ViewHolder allows us to avoid re-looking up view references Since views
	 * are recycled, these references will never change
	 */
	private static class ViewHolder {

		// incoming images
		public ImageView incomingImageBg;
		public ImageView incomingImageThumb;
		public ImageView incomingProfile;

		// incoming text, date.
		public TextView incomingText;
		public TextView incomingDate;

		// outgoing images
		public ImageView outgoingImageBg;
		public ImageView outgoingImageThumb;
		public ImageView outgoingProfile;

		// outgoing text, date.
		public TextView outgoingText;
		public TextView outgoingDate;

		public LinearLayout incomingImageCheck;
		public LinearLayout incomingTextCheck;
		public LinearLayout outgoingImageCheck;
		public LinearLayout outgoingTextCheck;

		public TextView loadMoreMessages;

	}

	public Bitmap getCroppedBitmap(Bitmap bitmap) {
		Bitmap output;
		// if (bitmap != null) {

		System.out.println(" Cutting the ImageView ");

		try {
			output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
					Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
					bitmap.getWidth() / 2, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);
		} catch (NullPointerException e) {
			output = TransferObject.getDefaultBitmapImage();
		}
		// } else {
		// output = TransferObject.getDefaultBitmapImage();
		// }
		// Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
		// return _bmp;
		return output;
	}
}