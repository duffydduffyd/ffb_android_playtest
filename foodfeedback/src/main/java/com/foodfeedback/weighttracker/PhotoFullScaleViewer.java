package com.foodfeedback.weighttracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodfeedback.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import com.foodfeedback.GalleryWidget.GalleryViewPager;
import com.foodfeedback.GalleryWidget.UrlPagerAdapter;
import com.foodfeedback.TouchView.UrlTouchImageView;
import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.networking.GalleryNetworkingServices;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class PhotoFullScaleViewer extends Activity implements OnClickListener {
	private LocalyticsSession localyticsSession;

	private LinearLayout leftButton, rightButton;
	private TextView centreTitleTxt;
	public ImageView profilePhoto[];
	public LinearLayout photoSlideRight, photoSlideLeft;
	int count = 0;
	public int indexOfPhotoToShow;
	private LinearLayout deletePhoto, sharePhoto;
	private Animation slideUpInAnim;
	private RelativeLayout deletePhotoLayout;
	private TextView cancelDialog, requestDeleteService;
	DisplayMetrics metrics;
	private GalleryViewPager mViewPager;
	public Context context;
	String[] listOfURLSToDisplay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.food_gallery);
		setResult(RESULT_CANCELED);
		context = this;

		Bundle bundle = getIntent().getExtras();
		indexOfPhotoToShow = bundle.getInt("clickedPicture");
		listOfURLSToDisplay = bundle.getStringArray("listOfURLS");

		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		setUpViewIds();
		setupFonts();
		addOnClickListeners();

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

	}

	private void addOnClickListeners() {

		leftButton.setOnClickListener(this);
		photoSlideRight.setOnClickListener(this);
		photoSlideLeft.setOnClickListener(this);
		deletePhoto.setOnClickListener(this);
		sharePhoto.setOnClickListener(this);
		deletePhotoLayout.setOnClickListener(this);

		cancelDialog.setOnClickListener(this);
		requestDeleteService.setOnClickListener(this);
	}

	public void updateGallery() throws Exception {
		if (listOfURLSToDisplay != null && listOfURLSToDisplay.length > 0) {
			final List<String> items = new ArrayList<String>();
			Collections.addAll(items, listOfURLSToDisplay);
			UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, items);

			pagerAdapter.setOnItemChangeListener(new OnItemChangeListener()
            {
				@Override
				public void onItemChange(int currentPosition)
                {
					if (listOfURLSToDisplay.length > 1) {
						centreTitleTxt.setText((mViewPager.getCurrentItem() + 1)
								+ " of " + listOfURLSToDisplay.length);
						photoSlideRight.setVisibility(View.INVISIBLE);
						photoSlideLeft.setVisibility(View.INVISIBLE);
					}

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

		if (listOfURLSToDisplay.length > 1) {
			if (listOfURLSToDisplay != null && listOfURLSToDisplay.length > 0)
				centreTitleTxt.setText((mViewPager.getCurrentItem() + 1)
						+ " of " + listOfURLSToDisplay.length);
			photoSlideRight.setVisibility(View.INVISIBLE);
			photoSlideLeft.setVisibility(View.INVISIBLE);
		}

	}

	private void setupFonts() {
		centreTitleTxt.setTypeface(Typeface.createFromAsset(this.getAssets(),
				this.getResources().getString(R.string.app_font_style_medium)));
	}

	private void setUpViewIds() {

		photoSlideRight = (LinearLayout) findViewById(R.id.photobrowser_right);
		photoSlideLeft = (LinearLayout) findViewById(R.id.photobrowser_left);

		centreTitleTxt = (TextView) findViewById(R.id.center_txt);
		centreTitleTxt.setText("");
		leftButton = (LinearLayout) findViewById(R.id.leftbutton);
		rightButton = (LinearLayout) findViewById(R.id.rightbutton);
		rightButton.setVisibility(View.GONE);

		deletePhoto = (LinearLayout) findViewById(R.id.delete_photo);
		deletePhoto.setVisibility(View.GONE);
		sharePhoto = (LinearLayout) findViewById(R.id.share_photo);
		sharePhoto.setVisibility(View.GONE);
		// sharePhoto.setEnabled(true);
		deletePhotoLayout = (RelativeLayout) findViewById(R.id.dialog_delete);

		requestDeleteService = (TextView) findViewById(R.id.request_deletephoto);
		requestDeleteService.setVisibility(View.GONE);
		cancelDialog = (TextView) findViewById(R.id.removedialog);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.leftbutton:

			mViewPager.removeAllViews();
			mViewPager = null;
			finish();
			break;

		case R.id.photobrowser_left:

			mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);

			break;

		case R.id.photobrowser_right:

			mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);

			break;

		case R.id.delete_photo:
			// System.out.println(" deleteTest "+ecoGallery.getSelectedItemPosition());
			slideUpInAnim = AnimationUtils.loadAnimation(this,
					R.anim.slide_upin_fast);
			deletePhotoLayout.setVisibility(View.VISIBLE);
			deletePhotoLayout.startAnimation(slideUpInAnim);

			break;

		case R.id.share_photo:
			sharePhoto.setEnabled(false);
			sharePhoto();
			break;

		case R.id.request_deletephoto:

			String imageId = listOfURLSToDisplay[mViewPager.getCurrentItem()];
			try {
				checkConnectivityForUpdateGallery(
						v.getContext(),
						LoginDetailsCacheManager
								.getObject(Controller.getAppBackgroundContext())
								.getData().getUser().getId(),
						UserDetailsCacheManager.getObject(
								Controller.getAppBackgroundContext())
								.getPassword(), imageId);

			} catch (Exception e) {
				e.printStackTrace();
			}
			indexOfPhotoToShow = mViewPager.getCurrentItem() - 1;
			deletePhotoLayout.setVisibility(View.GONE);

			break;

		case R.id.removedialog:
			deletePhotoLayout.setVisibility(View.GONE);
			break;
		}
	}

	private void checkConnectivityForUpdateGallery(Context context, int id,
			String loginPassword, String imageId) {

		GalleryNetworkingServices.getInstance().requestUpdateGalleryService(
				context, Integer.toString(id), loginPassword, imageId, "0",
				this, ServiceKeys.UPDATE_GALLERY_SERVICE);
	}

	public void onBackPressed() {

		System.out.println("onBackPressed");
		mViewPager.removeAllViews();
		mViewPager = null;
		finish();
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

	private void sharePhoto() {
		String urlOfImageToDownload = listOfURLSToDisplay[mViewPager
				.getCurrentItem()];

		// new DownloadImageTask(urlOfImageToDownload).execute();

	}

	public void onResume() {
		sharePhoto.setEnabled(true);
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