package com.foodfeedback.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.foodfeedback.myfood.ImageFileCache;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

	Context mContext;
	ImageFileCache fileCache;
	boolean isBlur = false;
	ImageView bmImage;

	public DownloadImageTask(ImageView bmImage, Context ctx) {
		this.mContext = ctx;
		this.bmImage = bmImage;
	}

	public DownloadImageTask(ImageView bmImage, Context ctx, boolean isBlur) {
		this.mContext = ctx;
		this.bmImage = bmImage;
		this.isBlur = isBlur;
	}

	protected Bitmap doInBackground(String... urls) {
		fileCache = new ImageFileCache(mContext);
		File f = fileCache.getFile(urls[0]);

		// from SD cache
		Bitmap mIcon11 = decodeFile(f);
		if (mIcon11 != null) {
			if (isBlur) {
				// mIcon11 = Utilities.applyGaussianBlur(mIcon11);
				mIcon11 = Utilities.fastblur(mIcon11, 20);
			}
			return mIcon11;
		}

		String urldisplay = urls[0];
		try {
			InputStream in = new java.net.URL(urldisplay).openStream();

			OutputStream os = new FileOutputStream(f);
			ImageCopyStreamUtil.CopyStream(in, os);
			os.close();

			mIcon11 = decodeFile(f);
			fileCache.saveFile(urldisplay, mIcon11);
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}

		if (isBlur) {
			mIcon11 = Utilities.fastblur(mIcon11, 75);
		}
		return mIcon11;
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		FileInputStream fis = null;
		try {
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
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			fis = new FileInputStream(f);
			return BitmapFactory.decodeStream(fis, null, o2);
			// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

			// bitmap.compress(format, quality, stream)

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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

	protected void onPostExecute(Bitmap result) {
		bmImage.setImageBitmap(result);
	}
}
