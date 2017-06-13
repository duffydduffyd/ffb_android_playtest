package com.foodfeedback.myfood;

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
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
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
import android.widget.ViewFlipper;

import com.foodfeedback.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import com.foodfeedback.GalleryWidget.GalleryViewPager;
import com.foodfeedback.GalleryWidget.UrlPagerAdapter;
import com.foodfeedback.TouchView.UrlTouchImageView;
import com.foodfeedback.cachemanager.FetchGalleryCacheManager;
import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.networking.GalleryNetworkingServices;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.utilities.ProgressHUD;
import com.foodfeedback.valueobjects.FetchGallery;
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class FoodGalleryFlipper extends Activity implements OnClickListener {
	private LocalyticsSession localyticsSession;

	private LinearLayout leftButton, rightButton;
	private TextView centreTitleTxt;
	String profilePhotoUrl[];
	public ImageView profilePhoto[];
	public LinearLayout photoSlideRight, photoSlideLeft;
	int count = 0;
	public int clickedPicture;
	private LinearLayout deletePhoto, sharePhoto;
	private Animation slideUpInAnim;
	private RelativeLayout deletePhotoLayout;
	private TextView cancelDialog, requestDeleteService;
	FetchGallery fetchGalleryObj;
	DisplayMetrics metrics;
	ViewFlipper.LayoutParams imageViewParams;
	private GalleryViewPager mViewPager;
	public Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.food_gallery);
		setResult(RESULT_CANCELED);
		context = this;

		// StrictMode.ThreadPolicy policy = new
		// StrictMode.ThreadPolicy.Builder()
		// .permitAll().build();
		// StrictMode.setThreadPolicy(policy);
		Bundle bundle = getIntent().getExtras();
		clickedPicture = bundle.getInt("clickedPicture");
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		try {
			fetchGalleryObj = FetchGalleryCacheManager.getObject(Controller
					.getAppBackgroundContext());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		setUpViewIds();
		setupFonts();
		addOnClickListeners();
		try {
			updateGallery();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (profilePhotoUrl != null && profilePhotoUrl.length != 0)
				centreTitleTxt.setText(clickedPicture + " of "
						+ profilePhotoUrl.length);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources

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

		if (fetchGalleryObj != null && fetchGalleryObj.getData() != null
				&& fetchGalleryObj.getData().size() > 0) {

			profilePhotoUrl = new String[fetchGalleryObj.getData().size()];

			for (int i = 0; i < fetchGalleryObj.getData().size(); i++) {

				profilePhotoUrl[i] = fetchGalleryObj.getData().get(i)
						.getImage_url();

				System.out.println(" imageProfilesTest " + profilePhotoUrl[i]);
			}
			final List<String> items = new ArrayList<String>();
			Collections.addAll(items, profilePhotoUrl);
			final UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, items);


			pagerAdapter.setOnItemChangeListener(new OnItemChangeListener() {
				@Override
				public void onItemChange(int currentPosition)
                {
					centreTitleTxt.setText(currentPosition + 1 + " of "
							+ profilePhotoUrl.length);

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

            /*
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
            {

                @Override
                public void onPageSelected(int position)
                {
                    centreTitleTxt.setText(position + 1 + " of "
                            + profilePhotoUrl.length);

                    for ( int tag = 0; tag < items.size(); ++tag )
                    {
                        View vw = mViewPager.findViewWithTag(position);
                        if ( vw != null && vw.getClass() == UrlTouchImageView.class )
                        {
                            UrlTouchImageView iv = (UrlTouchImageView)vw;
                            if ( tag == position )
                                iv.loadImage();
                            else
                                iv.killImage();
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state)
                {
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                {
                }
            }); */

			try {
				mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
				mViewPager.setOffscreenPageLimit(3);
				mViewPager.setAdapter(pagerAdapter);
				mViewPager.setCurrentItem(clickedPicture - 1);
			} catch (Exception e) {
				System.err.println("Exception while adding gallery fliper = "
						+ e);
			}
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
		sharePhoto = (LinearLayout) findViewById(R.id.share_photo);
		sharePhoto.setEnabled(true);
		deletePhotoLayout = (RelativeLayout) findViewById(R.id.dialog_delete);

		requestDeleteService = (TextView) findViewById(R.id.request_deletephoto);
		cancelDialog = (TextView) findViewById(R.id.removedialog);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.leftbutton:

			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			if (mViewPager != null)
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

			String imageId = fetchGalleryObj.getData()
					.get(mViewPager.getCurrentItem()).getId();
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
			clickedPicture = mViewPager.getCurrentItem() - 1;
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
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		if (mViewPager != null) {
			mViewPager.removeAllViews();
			mViewPager = null;
		}
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
		String urlOfImageToDownload = fetchGalleryObj.getData()
				.get(mViewPager.getCurrentItem()).getImage_url();

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

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		FileInputStream fis=null;
		try {

			// return savebitmap(f.getAbsolutePath());
			fis = new FileInputStream(f);
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
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
}