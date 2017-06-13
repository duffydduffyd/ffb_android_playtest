package com.foodfeedback.networking;

import android.content.Context;
import android.os.AsyncTask;

import com.foodfeedback.utilities.ProgressHUD;

public class AsyncTaskExecutor<Params, Progress, Results> {

	private final BackgroundTask<Params, Progress, Results> task;
	private final Renderer<Results> renderer;
	private AsyncTask<Params, Progress, Results> asyncTask;
	private String loaderMessageDisplay;
	// ProgressDialog dialog;
	ProgressHUD mProgressHUD;
	Context context;

	public AsyncTaskExecutor(Context activity,
			BackgroundTask<Params, Progress, Results> task,
			Renderer<Results> renderer, String loaderMessageDisplay) {
		this.task = task;
		this.renderer = renderer;
		// this.dialog = new ProgressDialog(activity);
		this.asyncTask = new MyAsyncTask(activity);
		this.context = activity;
		this.loaderMessageDisplay =loaderMessageDisplay;
	}

	public void execute(Params... params) {
		this.asyncTask.execute(params);
	}

	class MyAsyncTask extends AsyncTask<Params, Progress, Results> {
		private Context activity;

		public MyAsyncTask(Context activity) {
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {
			try {
				mProgressHUD = ProgressHUD.show(activity,loaderMessageDisplay, true, false);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			super.onPreExecute();
		}

		@Override
		protected Results doInBackground(Params... params) {

			return AsyncTaskExecutor.this.task.execute(params);
		}

		@Override
		protected void onPostExecute(Results results) {
			try {

				if (mProgressHUD!=null && mProgressHUD.isShowing()){
					mProgressHUD.dismiss();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			AsyncTaskExecutor.this.renderer.render(results);

		}
	}

}
