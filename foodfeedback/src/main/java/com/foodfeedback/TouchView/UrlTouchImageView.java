/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.foodfeedback.TouchView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.foodfeedback.myfood.ImageFileCache;
import com.foodfeedback.onboarding.R;

public class UrlTouchImageView extends RelativeLayout {
	protected ProgressBar mProgressBar;
	protected TouchImageView mImageView;
	ImageFileCache fileCache;
	protected Context mContext;
    String m_imageUrl;
    ImageLoadTask m_loader;

	public UrlTouchImageView(Context ctx) {
		super(ctx);
		mContext = ctx;
		init();

	}

	public UrlTouchImageView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		mContext = ctx;
		init();
	}

	public TouchImageView getImageView() {
		return mImageView;
	}

	protected void init() {
		fileCache = new ImageFileCache(mContext);
		mImageView = new TouchImageView(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mImageView.setLayoutParams(params);
		this.addView(mImageView);
		mImageView.setVisibility(GONE);

		mProgressBar = new ProgressBar(mContext, null,
				android.R.attr.progressBarStyleHorizontal);
		params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_VERTICAL);
		params.setMargins(30, 0, 30, 0);
		mProgressBar.setLayoutParams(params);
		mProgressBar.setIndeterminate(false);
		mProgressBar.setMax(100);
		this.addView(mProgressBar);
	}

	public void setUrl(String imageUrl)
    {
        m_imageUrl = imageUrl;
	}

    public void loadImage()
    {
        if ( m_loader == null )
        {
            m_loader = new ImageLoadTask();
            m_loader.execute(m_imageUrl);
        }
    }

    public void killImage()
    {
        if ( m_loader != null )
        {
            m_loader.cancel(true);
            m_loader = null;

            mImageView.setImageDrawable(null);
            mImageView.setVisibility(GONE);
            mProgressBar.setVisibility(GONE);
        }
    }

	public class ImageLoadTask extends AsyncTask<String, Integer, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... strings) {
			String urldisplay = strings[0];
			int count;
			File f = fileCache.getFile(urldisplay);
			publishProgress(5);
			// from SD cache
			Bitmap returnBitmap = decodeFile(f);
			if (returnBitmap != null)
				return returnBitmap;

			System.out.println("Downloading now");
			try {
				URL url = new URL(urldisplay);
				URLConnection conection = url.openConnection();
				conection.connect();
				int lenghtOfFile = conection.getContentLength();

				InputStream in = url.openStream();

				OutputStream os = new FileOutputStream(f);

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = in.read(data)) != -1) {
					total += count;
					// publishing the progress....
					// After this onProgressUpdate will be called
					publishProgress((int) ((total * 100) / lenghtOfFile));
					// writing data to file
					os.write(data, 0, count);
				}

				os.flush();
				os.close();
                in.close();

				returnBitmap = decodeFile(f);
				System.out.println("Downloading Finished");
				fileCache.saveFile(urldisplay, returnBitmap);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}

			return returnBitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap)
        {
			if (bitmap == null)
            {
				mImageView.setScaleType(ScaleType.CENTER);
				mImageView.setImageDrawable(getResources().getDrawable(R.drawable.placeholder_bio));
			}
            else
            {
				mImageView.setScaleType(ScaleType.MATRIX);
				mImageView.setImageBitmap(bitmap);
			}
			mImageView.setVisibility(VISIBLE);
			mProgressBar.setVisibility(GONE);
		}

		@Override
		protected void onProgressUpdate(Integer... values)
        {
			mProgressBar.setProgress(values[0]);
		}
	}

	private Bitmap decodeFile(File f) {
		FileInputStream fis = null;
		try {

			// return savebitmap(f.getAbsolutePath());

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

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			fis.close();
			fis = new FileInputStream(f);
			return BitmapFactory.decodeStream(fis, null, o2);
			// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

			// bitmap.compress(format, quality, stream)

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}catch (NullPointerException e) {
				e.printStackTrace();
			}
			
		}
		return null;
	}
}
