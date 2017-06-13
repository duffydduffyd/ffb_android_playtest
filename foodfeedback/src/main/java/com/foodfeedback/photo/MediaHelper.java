package com.foodfeedback.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.onboarding.Controller;

/**
 * Used to make file system use in the tutorial a bit more obvious
 * in a production environment you wouldn't make these calls static
 * as you have no way to mock them for testing
 * @author paul.blundell
 *
 */
public class MediaHelper {
	public static File mediaStorageDir;
	public static File getOutputMediaFile() throws Exception{
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
//		System.out.println(" MediaHelperTTest getCameraRoll "+UserDetailsCacheManager.getObject(MyApplication.getAppBackgroundContext()).getCameraRoll());
		if(UserDetailsCacheManager.getObject(Controller.getAppBackgroundContext()).getCameraRoll() == 0)
			mediaStorageDir = new File(Controller.getAppBackgroundContext().getCacheDir().getPath(), "Spike");
		else
			mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DCIM");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile = new File(mediaStorageDir.getPath() + File.separator +"IMG_"+ timeStamp +".jpg");

	    return mediaFile;
	}

	public static boolean saveToFile(byte[] bytes, File file){
		boolean saved = false;
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bytes);
			fos.close();
			saved = true;
		} catch (FileNotFoundException e) {
			Log.e("FileNotFoundException", e);
		} catch (IOException e) {
			Log.e("IOException", e);
		}
		return saved;
	}

}
