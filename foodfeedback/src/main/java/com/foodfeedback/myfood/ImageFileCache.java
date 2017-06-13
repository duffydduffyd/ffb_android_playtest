package com.foodfeedback.myfood;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;

public class ImageFileCache {

	private File cacheDir;

	public ImageFileCache(Context context) {
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
		new SaveFileToCache(url, fileToSave).execute();
	}

	class SaveFileToCache extends AsyncTask<Void, Void, String> {
		String thisURl;
		Bitmap thisbm;

		public SaveFileToCache(String url, Bitmap fileToSave) {
			this.thisbm = fileToSave;
			this.thisURl = url;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				String filename = String.valueOf(this.thisURl.hashCode());
				// create a file to write bitmap data
				File f = new File(cacheDir, filename);
				f.createNewFile();
				// write the bytes in file
				FileOutputStream fos = new FileOutputStream(f);
				// Convert bitmap to byte array
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				this.thisbm.compress(CompressFormat.PNG,
						0 /* ignored for PNG */, bos);
				byte[] bitmapdata = bos.toByteArray();
				fos.write(bitmapdata);
				fos.flush();
				fos.close();
				System.out.println("Saved file properly");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "Saved";
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

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