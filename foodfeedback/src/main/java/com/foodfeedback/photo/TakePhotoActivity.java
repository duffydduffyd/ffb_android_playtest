package com.foodfeedback.photo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
// protected static final String EXTRA_IMAGE_PATH = "com.foodfeedback.photo.TakePhotoActivity.EXTRA_IMAGE_PATH";
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodfeedback.onboarding.FoodTabActivity;
import com.foodfeedback.onboarding.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

/**
 * Gives a simple UI that detects if this device has a camera, informing the
 * user if they do or dont
 * 
 * This also receives the result of a picture being taken and displays it to the
 * user
 * 
 */
public class TakePhotoActivity extends Activity implements OnClickListener {
	private LocalyticsSession localyticsSession;

	private static final int REQ_CAMERA_IMAGE = 123;
	private RelativeLayout useThisLayout, reTakeLayout;
	private String imagePath;
	public static ImageView capturedPhoto;
	private TextView txtAcceptInvitation;
	private TextView txtRejectInvitation;
	Typeface tfSpecial;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Controller.progressHUD = ProgressHUD.show(this, "", true, false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = new Intent(this, CameraActivity.class);

		tfSpecial = Typeface.createFromAsset(getAssets(), getResources()
				.getString(R.string.app_font_style_medium));

		startActivityForResult(intent, REQ_CAMERA_IMAGE);
		// Instantiate the object
		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources

		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("All others");

		this.localyticsSession.upload(); // upload any data

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		setContentView(R.layout.takephoto_screen);
		useThisLayout = (RelativeLayout) findViewById(R.id.usethis);
		reTakeLayout = (RelativeLayout) findViewById(R.id.retake);

		useThisLayout.setOnClickListener(this);
		reTakeLayout.setOnClickListener(this);

		txtAcceptInvitation = (TextView) findViewById(R.id.useThisTxt);
		txtRejectInvitation = (TextView) findViewById(R.id.rejectThisTxt);

		txtRejectInvitation.setTypeface(tfSpecial);
		txtAcceptInvitation.setTypeface(tfSpecial);

		if (requestCode == REQ_CAMERA_IMAGE && resultCode == RESULT_OK) {
			imagePath = data.getStringExtra(CameraActivity.EXTRA_IMAGE_PATH);
			Log.i("Got image path: " + imagePath);
			displayImage(imagePath);
		} else if (requestCode == REQ_CAMERA_IMAGE
				&& resultCode == RESULT_CANCELED) {
			Log.i("User didn't take an image");
			Intent intentFood = new Intent(this, FoodTabActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentFood);
		}
	}

	private void displayImage(String path) {

		capturedPhoto = (ImageView) findViewById(R.id.image_view_captured_image);
		Bitmap myBitmap = BitmapFactory.decodeFile(path);
		capturedPhoto.setImageBitmap(myBitmap);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.usethis:

			Intent intentSharePicture = new Intent(v.getContext(),
					ShareTakePicture.class);
			intentSharePicture.putExtra("imagepath", imagePath);
			startActivity(intentSharePicture);

			break;

		case R.id.retake:

			Intent intent = new Intent(this, CameraActivity.class);
			startActivityForResult(intent, REQ_CAMERA_IMAGE);

			break;

		}
	}

	@Override
	public void onBackPressed() {

		System.out.println("onBackPressed");

	}

	@Override
	protected void onResume() {
		this.localyticsSession.open();
		System.out.println(" JanTest onresume() FoodGallery ");
		super.onResume();
	}

	@Override
	protected void onPostCreate(Bundle icicle) {
		System.out.println(" JanTest onPostCreate() photo ");
		super.onPostCreate(icicle);
	}

	@Override
	protected void onStart() {
		System.out.println(" JanTest onStart() photo ");
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	public void onPause() {
		this.localyticsSession.close();
		this.localyticsSession.upload();
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		System.out.println("google Analytics Stop");

		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

}