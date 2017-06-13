package com.foodfeedback.messages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.foodfeedback.GalleryWidget.BasePagerAdapter;
import com.foodfeedback.GalleryWidget.GalleryViewPager;
import com.foodfeedback.GalleryWidget.UrlPagerAdapter;
import com.foodfeedback.TouchView.UrlTouchImageView;
import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.myfood.ImageFileCache;
import com.foodfeedback.networking.MessagesNetworkingServices;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.utilities.ProgressHUD;
import com.foodfeedback.utilities.Utilities;
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class MessagePhotoView extends Activity {
	private LocalyticsSession localyticsSession;

	private ImageView backButton, delete_button, share_button;
	private TextView centreTitleTxt, sendButton;
	private TextView cancelDialog, request_deletephoto;
	private RelativeLayout deletePhotoLayout;
	ViewFlipper.LayoutParams imageViewParams;
	LinearLayout send_message_footer;

	public ImageView profilePhoto[];
	int count = 0;
	public int indexOfPhotoToShow;
	String IDOfPhotoToShow, UserInteractingWithID, messageID;
	private Animation slideUpInAnim;
	DisplayMetrics metrics;
	private GalleryViewPager mViewPager;
	public Context context;
	String[] listOfURLSToDisplay;
	Typeface tf, tfTitle;
	Boolean myPhoto;
	Handler deleteHandler, messageHandler;
	EditText messageTxtBox;
	ProgressHUD mProgressHUD, dProgressHUD;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.message_photo_viewer);

		context = this;

		Bundle bundle = getIntent().getExtras();
		indexOfPhotoToShow = bundle.getInt("clickedPicture");
		IDOfPhotoToShow = bundle.getString("IDOfPhotoToShow");
		UserInteractingWithID = bundle.getString("UserInteractingWithID");
		messageID = bundle.getString("messageID");
		System.out.println("## OK the image ID is " + IDOfPhotoToShow);
		myPhoto = bundle.getBoolean("myPicture", false);
		listOfURLSToDisplay = bundle.getStringArray("listOfURLS");

		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		setUpViewIds();

		try {
			updateGallery();
		} catch (Exception e) {
		}

		// Context used to access device resources
		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext());

		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("Photo Browser");

		this.localyticsSession.upload(); // upload any data

		deleteHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String response = msg.getData().getString("RESULT");
				System.out.println("Handling the message " + response);
				if (response.equals(ServiceKeys.UPDATE_MESSAGE_SUCCESS)) {
					Utilities.showToast(response, MessagePhotoView.this);
					dProgressHUD.dismiss();
					finish();
				} else {
					dProgressHUD.dismiss();
					Utilities.showErrorToast(response, MessagePhotoView.this);
				}
			}

		};

		messageHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String response = msg.getData().getString("RESULT");
				System.out.println("Handling the message " + response);
				messageTxtBox.setText("");
				if (!response.equals("ERROR")) {
					Utilities.showToast("" + response, MessagePhotoView.this);
					
					mProgressHUD.dismiss();
					finish();
				} else {
					mProgressHUD.dismiss();
					Utilities.showErrorToast(response, MessagePhotoView.this);
				}
			}
		};

	}

	private void setUpViewIds() {

		tf = Typeface.createFromAsset(
				getApplicationContext().getAssets(),
				getApplicationContext().getResources().getString(
						R.string.font_standard));

		tfTitle = Typeface.createFromAsset(
				getApplicationContext().getAssets(),
				getApplicationContext().getResources().getString(
						R.string.app_font_style_medium));

		backButton = (ImageView) findViewById(R.id.back_button);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (mViewPager != null) {
						mViewPager.removeAllViews();
						mViewPager = null;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				finish();
			}
		});

		deletePhotoLayout = (RelativeLayout) findViewById(R.id.dialog_delete);
		request_deletephoto = (TextView) findViewById(R.id.request_deletephoto);

		request_deletephoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					dProgressHUD = ProgressHUD.show(MessagePhotoView.this,
							getResources().getString(R.string.loading_txt),
							true, false);
					new DeleteImageTask().start();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// mViewPager.removeAllViews();
				// mViewPager = null;
				// finish();

			}
		});

		send_message_footer = (LinearLayout) findViewById(R.id.send_message_footer);
		delete_button = (ImageView) findViewById(R.id.delete_button);
		delete_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Delete Button Clicked");
				// System.out.println(" deleteTest "+ecoGallery.getSelectedItemPosition());
				slideUpInAnim = AnimationUtils.loadAnimation(v.getContext(),
						R.anim.slide_upin_fast);
				deletePhotoLayout.setVisibility(View.VISIBLE);
				deletePhotoLayout.startAnimation(slideUpInAnim);
				send_message_footer.setVisibility(View.INVISIBLE);
			}
		});

		cancelDialog = (TextView) findViewById(R.id.removedialog);
		cancelDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				send_message_footer.setVisibility(View.VISIBLE);
				deletePhotoLayout.setVisibility(View.GONE);

			}
		});

		share_button = (ImageView) findViewById(R.id.share_button);
		share_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Share Button Clicked");
				// share_button.setEnabled(false);
				sharePhoto();
			}
		});

		centreTitleTxt = (TextView) findViewById(R.id.center_title);
		centreTitleTxt.setText("");
		centreTitleTxt.setTypeface(tfTitle);

		messageTxtBox = (EditText) findViewById(R.id.message_textbox);
		messageTxtBox.setTypeface(tf);

		sendButton = (TextView) findViewById(R.id.sendButton);
		sendButton.setTypeface(tf);

		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.out.println("Send Button clicked");
				mProgressHUD = ProgressHUD.show(MessagePhotoView.this,
						getResources().getString(R.string.loading_txt), true,
						false);
				new SendMessageTask().start();

			}
		});

		if (!myPhoto) {
			share_button.setVisibility(View.INVISIBLE);
			delete_button.setVisibility(View.INVISIBLE);
		} else {
			share_button.setVisibility(View.VISIBLE);
			delete_button.setVisibility(View.VISIBLE);
		}

	}

	public void updateGallery() throws Exception {
		if (listOfURLSToDisplay != null && listOfURLSToDisplay.length > 0) {
			final List<String> items = new ArrayList<String>();
			Collections.addAll(items, listOfURLSToDisplay);
			UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, items);

            pagerAdapter.setOnItemChangeListener(new BasePagerAdapter.OnItemChangeListener() {
                @Override
                public void onItemChange(int currentPosition)
                {
                    for ( int tag = 0; tag < items.size(); ++tag )
                    {
                        View vw = mViewPager.findViewWithTag(tag);
                        if ( vw != null && vw.getClass() == UrlTouchImageView.class )
                        {
                            UrlTouchImageView iv = (UrlTouchImageView)vw;
                            if ( tag == currentPosition )
                                iv.loadImage();
                            else
                                iv.killImage();
                        }
                    }
                }
            });

			try {
				mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
				mViewPager.setOffscreenPageLimit(1);
				mViewPager.setAdapter(pagerAdapter);
				mViewPager.setCurrentItem(indexOfPhotoToShow - 1);
			} catch (Exception e) {
				System.err.println("Exception while adding gallery fliper = "
						+ e);
			}
		}
	}

	class DeleteImageTask extends Thread {
		@Override
		public void run() {
			String response = "";
			try {
				String userID = LoginDetailsCacheManager
						.getObject(Controller.getAppBackgroundContext())
						.getData().getUser().getId()
						+ "";
				String password = UserDetailsCacheManager.getObject(
						Controller.getAppBackgroundContext()).getPassword();
				response = new MessagesNetworkingServices(
						getApplicationContext()).updateMessageHttpPostRequest(
						getApplicationContext(), userID, password, messageID,
						"0");

				// response = GalleryNetworkingServices.getInstance()
				// .updateGalleryHttpPostRequest(getApplicationContext(),
				// userID, password, IDOfPhotoToShow, "0");
				System.out.println("Printing the response = " + response);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("RESULT", response);
			msg.setData(data);
			deleteHandler.sendMessage(msg);
		}
	}

	class SendMessageTask extends Thread {
		@Override
		public void run() {

			String result = null;
			String userID;
			try {
				userID = LoginDetailsCacheManager
						.getObject(Controller.getAppBackgroundContext())
						.getData().getUser().getId()
						+ "";
				String password = UserDetailsCacheManager.getObject(
						Controller.getAppBackgroundContext()).getPassword();

				if (messageTxtBox.getText() != null
						&& messageTxtBox.getText().toString().trim() != "") {
					String messageToSend = messageTxtBox.getText().toString();
					try {
						result = new MessagesNetworkingServices(context)
								.sendMessageHttpPostRequest(userID, password,
										UserInteractingWithID, ""
												+ messageToSend, "");
					} catch (Exception e) {
						e.printStackTrace();
						result = e.getMessage();
					}

					Message msg = new Message();
					Bundle data = new Bundle();
					data.putString("RESULT", result);
					msg.setData(data);
					messageHandler.sendMessage(msg);
				}
			} catch (Exception e1) {
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("RESULT", "ERROR");
				msg.setData(data);
				e1.printStackTrace();
				messageHandler.sendMessage(msg);
			}
		}
	}

	public void onBackPressed() {

		System.out.println("onBackPressed");
		mViewPager.removeAllViews();
		mViewPager = null;
		finish();
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	private void sharePhoto() {
		String urlOfImageToDownload = listOfURLSToDisplay[mViewPager
				.getCurrentItem()];

		new GalleryImageDownload(urlOfImageToDownload).execute();
	}

	public class GalleryImageDownload extends AsyncTask<Void, Void, File> {

		String urlImage;

		public GalleryImageDownload(String urlOfImageToDownload) {
			this.urlImage = urlOfImageToDownload;
		}

		@Override
		protected void onPreExecute() {
			Controller.progressHUD = ProgressHUD.show(context, "", true, false);
		}

		@Override
		protected File doInBackground(Void... params) {

			// Do we need to download and attach an icon and is the SD Card
			// available?
			ImageFileCache fileCache;
			fileCache = new ImageFileCache(getApplicationContext());
			File ff = fileCache.getFile(urlImage);

			Bitmap b = decodeFile(ff);
			if (b != null) {
				System.out.println(" NOT Downloading Share ");
				return ff;
			} else {
				System.out.println(" NULL Downloading Share ");
				return ff;
			}

		}

		@Override
		protected void onPostExecute(File result) {
			Controller.progressHUD.dismiss();
			share(result.getPath(),
					getResources().getString(R.string.share_photo));
		}
	}

	void share(String imagePath, String message) {
		try {
			List<Intent> targetedShareIntents = new ArrayList<Intent>();
			Intent share = new Intent(android.content.Intent.ACTION_SEND);
			share.setType("image/jpeg");
			List<ResolveInfo> resInfo = getPackageManager()
					.queryIntentActivities(share, 0);
			if (!resInfo.isEmpty()) {
				for (ResolveInfo info : resInfo) {
					Intent targetedShare = new Intent(
							android.content.Intent.ACTION_SEND);
					targetedShare.setType("image/jpeg"); // put here your mime
					// type
					targetedShare.putExtra(Intent.EXTRA_SUBJECT,
							"Check out my Food Feedback photo!");
					targetedShare.putExtra(Intent.EXTRA_TEXT, message);
					targetedShare.putExtra(Intent.EXTRA_STREAM,
							Uri.fromFile(new File(imagePath)));
					targetedShare.setPackage(info.activityInfo.packageName);
					targetedShareIntents.add(targetedShare);
				}
				Intent chooserIntent = Intent.createChooser(
						targetedShareIntents.remove(0), "Select app to share");
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
						targetedShareIntents.toArray(new Parcelable[] {}));
				startActivity(chooserIntent);
			}
		}

		catch (Exception e) {
			Log.v("VM", "Exception while sending image on" + e.getMessage());
		}
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		
		FileInputStream fis = null;
		try {

			// return savebitmap(f.getAbsolutePath());

			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			fis = new FileInputStream(f);
			BitmapFactory.decodeStream(fis, null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 600;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			fis.close();
			fis = new FileInputStream(f);
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(fis, null, o2);
			// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

			// bitmap.compress(format, quality, stream)

		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
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
