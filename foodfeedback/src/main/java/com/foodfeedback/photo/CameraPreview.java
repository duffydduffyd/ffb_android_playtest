package com.foodfeedback.photo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 
 * @author paul.blundell
 * 
 */
public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {

	private Camera camera;
	private SurfaceHolder holder;
	private Activity context;
	int width = 0,height =0;

	public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CameraPreview(Context context) {
		super(context);
	}

	public void init(Camera camera, CameraActivity cameraActivity) {
		this.camera = camera;
		initSurfaceHolder(cameraActivity);
	}

	@SuppressWarnings("deprecation")
	// needed for < 3.0
	private void initSurfaceHolder(Activity cameraActivity) {
		context = cameraActivity;
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initCamera(holder);
	}

	private void initCamera(SurfaceHolder holder) {
		try {
			// camera.setDisplayOrientation(degrees)
			//camera = Camera.open();
			// setCameraDisplayOrientation(getActivity(),
			// CameraInfo.CAMERA_FACING_BACK, camera);
			
			if (width!=0 && height!=0){
				 Camera.Parameters parameters=camera.getParameters();
			     Camera.Size size=getBestPreviewSize(width, height, parameters);
			     parameters.setPreviewSize(size.width, size.height);
			     
			     Camera.Size pictureSize=getSmallestPictureSize(parameters);
			     parameters.setPictureSize(pictureSize.width,
                         pictureSize.height);
			     
			     camera.setParameters(parameters);
			}
			
		        
			setCameraDisplayOrientation(context, 1, camera);
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch (Exception e) {
			Log.d("Error setting camera preview", e);
		}
	}

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}

		return (result);
	}
	
	private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
	    Camera.Size result=null;

	    for (Camera.Size size : parameters.getSupportedPictureSizes()) {
	      if (result == null) {
	        result=size;
	      }
	      else {
	        int resultArea=result.width * result.height;
	        int newArea=size.width * size.height;

	        if (newArea < resultArea) {
	          result=size;
	        }
	      }
	    }

	    return(result);
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
		System.out.println("Degrees are :" + rotation);
		System.out.println("orientation test:" + info.orientation);
		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
			System.out.println("front facing  :" + result);
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
			System.out.println("back facing  :" + result);
		}
		camera.setDisplayOrientation(result);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
		 this.width = width;
		 this.height = height;
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}
}