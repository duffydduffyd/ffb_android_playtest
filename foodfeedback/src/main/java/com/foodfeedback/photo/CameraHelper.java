package com.foodfeedback.photo;

import android.app.Activity;
import android.hardware.Camera;
import android.view.Surface;

import com.foodfeedback.cachemanager.UserPreferencesCacheManager;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.valueobjects.UserPreferences;

/**
 * Used to make camera use in the tutorial a bit more obvious in a production
 * environment you wouldn't make these calls static as you have no way to mock
 * them for testing
 * 
 * @author paul.blundell
 * 
 */
public class CameraHelper {

	public static boolean cameraAvailable(Camera camera) {
		return camera != null;
	}

	public static Camera getCameraInstance(Activity activity) {
		Camera c = null;
		try {
			c = Camera.open();
			
			Object flashMode = null;
			UserPreferences fetchflash = UserPreferencesCacheManager.getObject(Controller
					.getAppBackgroundContext());
			if (fetchflash != null) {
				flashMode = fetchflash.getFlashMode();
			}else{
				flashMode = Camera.Parameters.FLASH_MODE_AUTO;
			}
			
			setCameraDisplayOrientation(activity, 1, c);
		} catch (Exception e) {
			// Camera is not available or doesn't exist
			Log.d("getCamera failed", e);
		}
		return c;
	}

	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
			// System.out.println("front facing  :"+result);
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
			// System.out.println("back facing  :"+result);
		}
		camera.setDisplayOrientation(result);
	}

}
