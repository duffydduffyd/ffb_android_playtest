package com.foodfeedback.photo;

import static com.foodfeedback.photo.CameraHelper.cameraAvailable;
import static com.foodfeedback.photo.CameraHelper.getCameraInstance;
import static com.foodfeedback.photo.MediaHelper.getOutputMediaFile;
import static com.foodfeedback.photo.MediaHelper.saveToFile;

import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.foodfeedback.cachemanager.UserPreferencesCacheManager;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.FoodTabActivity;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.valueobjects.UserPreferences;
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

/**
 * Takes a photo saves it to the SD card and returns the path of this photo to
 * the calling Activity
 * 
 * 
 */
public class CameraActivity extends Activity implements PictureCallback,
		OnClickListener {
	private LocalyticsSession localyticsSession;

	protected static final String EXTRA_IMAGE_PATH = "com.foodfeedback.photo.CameraActivity.EXTRA_IMAGE_PATH";

	private Camera camera;
	private CameraPreview cameraPreview;
	private LinearLayout cancelCamera, sharePicture, capturePicture;
	private Button autoBtn, onBtn, offBtn;

	private static final int SELECT_PICTURE = 1;
	private String selectedImagePath;

	private int screen_orientation;
	private String flashMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_camera);

		Typeface tfNormal = Typeface.createFromAsset(this.getAssets(),
				this.getResources().getString(R.string.font_standard));
	
		
		
		cancelCamera = (LinearLayout) findViewById(R.id.cancel_camera);
		sharePicture = (LinearLayout) findViewById(R.id.share_picture);
		capturePicture = (LinearLayout) findViewById(R.id.capture_photo);
		capturePicture.setEnabled(true);
		cancelCamera.setOnClickListener(this);
		sharePicture.setOnClickListener(this);
		capturePicture.setOnClickListener(this);
		
		autoBtn = (Button) findViewById(R.id.buttonAuto);
		onBtn = (Button) findViewById(R.id.buttonOn);
		offBtn = (Button) findViewById(R.id.buttonOff);

		autoBtn.setTypeface(tfNormal);
		onBtn.setTypeface(tfNormal);
		offBtn.setTypeface(tfNormal);
		
		
		autoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (cameraAvailable(camera))
                {
                    try
                    {
                        Camera.Parameters params = camera.getParameters();
                        flashMode = Camera.Parameters.FLASH_MODE_AUTO;
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                        camera.setParameters(params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try
                    {
                        Camera.Parameters params = camera.getParameters();
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        camera.setParameters(params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

					try {
						UserPreferences fetchflash = UserPreferencesCacheManager
								.getObject(Controller.getAppBackgroundContext());

						if (fetchflash == null) {
							fetchflash = new UserPreferences();
						}
						fetchflash.setFlashMode(flashMode);
						UserPreferencesCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								fetchflash);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				onBtn.setEnabled(true);
				offBtn.setEnabled(true);
				autoBtn.setEnabled(false);
			}
		});

		onBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onBtn.setEnabled(false);
				offBtn.setEnabled(true);
				autoBtn.setEnabled(true);
				if (cameraAvailable(camera)) {

                    try
                    {
                        Camera.Parameters params = camera.getParameters();
                        flashMode = Camera.Parameters.FLASH_MODE_ON;
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        camera.setParameters(params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try
                    {
                        Camera.Parameters params = camera.getParameters();
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        camera.setParameters(params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

					try {
						UserPreferences fetchflash = UserPreferencesCacheManager
								.getObject(Controller.getAppBackgroundContext());

						if (fetchflash == null) {
							fetchflash = new UserPreferences();
						}

						fetchflash.setFlashMode(flashMode);
						UserPreferencesCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								fetchflash);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		offBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onBtn.setEnabled(true);
				offBtn.setEnabled(false);
				autoBtn.setEnabled(true);
				if (cameraAvailable(camera)) {

                    try
                    {
                        Camera.Parameters params = camera.getParameters();
                        flashMode = Camera.Parameters.FLASH_MODE_OFF;
                        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try
                    {
                        Camera.Parameters params = camera.getParameters();
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                        camera.setParameters(params);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

					try {
						UserPreferences fetchflash = UserPreferencesCacheManager
								.getObject(Controller.getAppBackgroundContext());

						if (fetchflash == null) {
							fetchflash = new UserPreferences();
						}

						fetchflash.setFlashMode(flashMode);
						UserPreferencesCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								fetchflash);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources

		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("Camera");

		this.localyticsSession.upload(); // upload any data

	}

	// Show the camera view on the activity
	private void initCameraPreview() {
		cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
		cameraPreview.init(camera, this);
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.d("Picture taken");
		// try {

		if (data != null) {
			byte[] byteArrayForBitmap = new byte[17 * 1024];
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inTempStorage = byteArrayForBitmap;
			opt.inDither = true;
			opt.inJustDecodeBounds = false;
			opt.inSampleSize = 2;
			opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
			// System.gc();

			Bitmap bm = BitmapFactory.decodeByteArray(data, 0,
					(data != null) ? data.length : 0, opt);

			int rotationValue = 0;
			System.out.println("Orientation is:"
					+ getResources().getConfiguration().orientation);
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				if (screen_orientation == 0) {
					rotationValue = 90;
				} else {
					rotationValue = -90;
				}
			} else {
				if (screen_orientation == 3) {
					rotationValue = -180;
				} else {
					rotationValue = 0;
				}
			}
			Matrix mtx = new Matrix();
			mtx.postRotate(rotationValue);
			// Rotating Bitmap
			bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), mtx, true);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bm.compress(CompressFormat.JPEG, 70, bos);
			byte[] bitmapdata = bos.toByteArray();
			String path = savePictureToFileSystem(bitmapdata);
			setResult(path);
			finish();
		}
	}

	private static String savePictureToFileSystem(byte[] data) {
		File file = null;
		try {
			file = getOutputMediaFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		saveToFile(data, file);
		return file.getAbsolutePath();
	}

	private void setResult(String path) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_IMAGE_PATH, path);
		setResult(RESULT_OK, intent);
	}

	// ALWAYS remember to release the camera when you are finished
	@Override
	protected void onPause() {
		this.localyticsSession.close();
		this.localyticsSession.upload();
		super.onPause();
		releaseCamera();
	}

	private void releaseCamera() {
		if (camera != null) {
			camera.release();
			camera = null;
		}
	}

	@SuppressWarnings("deprecation")
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();
				selectedImagePath = getPath(selectedImageUri);
				System.out.println("Image Path : " + selectedImagePath);
				// TakePhotoActivity.capturedPhoto.setImageURI(selectedImageUri);
				Intent intentSharePicture = new Intent(this,
						ShareTakePicture.class);
				intentSharePicture.putExtra("imagepath", selectedImagePath);
				startActivity(intentSharePicture);
			} else {
			}
		} else {

			Intent intentFood = new Intent(this, FoodTabActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentFood);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.cancel_camera:
			finish();
			break;

		case R.id.share_picture:
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			// intent.setType("image/*");
			// intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Select Picture"),
					SELECT_PICTURE);
			break;

		case R.id.capture_photo:
			try {
				camera.takePicture(null, null, this);
			} catch (Exception e) {
			}
			capturePicture.setEnabled(false);
			break;
		}
	}

	@Override
	public void onBackPressed() {

		System.out.println("onBackPressed");

	}

	public void onResume() {
		super.onResume();
		this.localyticsSession.open();

		try {
			UserPreferences fetchflash = UserPreferencesCacheManager
					.getObject(Controller.getAppBackgroundContext());
			if (fetchflash != null) {
				flashMode = (String) fetchflash.getFlashMode();

				if (flashMode.equals(Camera.Parameters.FLASH_MODE_ON)) {
					System.out.println("#FLASh");
					onBtn.setEnabled(false);
					offBtn.setEnabled(true);
					autoBtn.setEnabled(true);
				} else if (flashMode.equals(Camera.Parameters.FLASH_MODE_OFF)) {
					onBtn.setEnabled(true);
					offBtn.setEnabled(false);
					autoBtn.setEnabled(true);
				} else {
					onBtn.setEnabled(true);
					offBtn.setEnabled(true);
					autoBtn.setEnabled(false);
				}
			} else {
				flashMode = Camera.Parameters.FLASH_MODE_AUTO;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		setResult(RESULT_CANCELED);
		camera = getCameraInstance(this);
		if (cameraAvailable(camera))
        {
            try
            {
                Camera.Parameters params = camera.getParameters();
                params.setFlashMode(flashMode);
                camera.setParameters(params);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try
            {
                Camera.Parameters params = camera.getParameters();
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                camera.setParameters(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
		}
        else
        {
			// finish();
			Intent intentFood = new Intent(this, FoodTabActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentFood);
		}
		try
        {
			if (Controller.progressHUD != null)
				Controller.progressHUD.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}

        initCameraPreview();
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
