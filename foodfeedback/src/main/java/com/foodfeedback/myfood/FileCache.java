package com.foodfeedback.myfood;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

public class FileCache {

	private File cacheDir;

	public FileCache(Context context) {
		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"TempImages");
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public File getFile(String url) {
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);
		return f;

	}

	public void saveFile(String url, Bitmap fileToSave) {
		try {
			String filename = String.valueOf(url.hashCode());
			//create a file to write bitmap data
			File f = new File(cacheDir, filename);
			f.createNewFile();
			//write the bytes in file
			FileOutputStream fos = new FileOutputStream(f);
			//Convert bitmap to byte array
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			fileToSave.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
			byte[] bitmapdata = bos.toByteArray();
			fos.write(bitmapdata);
			fos.flush();
			fos.close();
			System.out.println("Saved file properly");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}

}