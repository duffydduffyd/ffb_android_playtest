package com.foodfeedback.onboarding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.util.Log;

import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.utilities.ProgressHUD;
import com.google.android.gcm.GCMRegistrar;

public class Controller extends Application {

	private static Context context;
	public static ProgressHUD progressHUD;

	public void onCreate() {
		super.onCreate();
		Controller.context = getApplicationContext();

	}

	public static Context getAppBackgroundContext() {
		return Controller.context;
	}

	private final int MAX_ATTEMPTS = 5;
	private final int BACKOFF_MILLI_SECONDS = 2000;
	private final Random random = new Random();

	// Register this account with the server.
	public void register(final Context context, final String regId) {

		Log.i(Config.TAG, "registering device (regId = " + regId + ")");

		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

		// Once GCM returns a registration id, we need to register on our server
		// As the server might be down, we will retry it a couple
		// times.
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {

			Log.d(Config.TAG, "Attempt #" + i + " to register");

			try {
				// Send Broadcast to Show message on screen
				displayRegistrationMessageOnScreen(context, context.getString(
						R.string.server_registering, i, MAX_ATTEMPTS));

				// Post registration values to web server
				post(regId);

				GCMRegistrar.setRegisteredOnServer(context, true);

				// Send Broadcast to Show message on screen
				String message = context.getString(R.string.server_registered);
				displayRegistrationMessageOnScreen(context, message);

				// Launch Main Activity
				/*
				 * Intent i1 = new Intent(getApplicationContext(),
				 * GridViewExample.class);
				 * i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
				 * Intent.FLAG_ACTIVITY_CLEAR_TOP); startActivity(i1);
				 */

				return;
			} catch (IOException e) {

				// Here we are simplifying and retrying on any error; in a real
				// application, it should retry only on unrecoverable errors
				// (like HTTP error code 503).

				Log.e(Config.TAG, "Failed to register on attempt " + i + ":"
						+ e);

				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {

					Log.d(Config.TAG, "Sleeping for " + backoff
							+ " ms before retry");
					Thread.sleep(backoff);

				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(Config.TAG,
							"Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return;
				}

				// increase backoff exponentially
				backoff *= 2;
			}
		}

		String message = context.getString(R.string.server_register_error,
				MAX_ATTEMPTS);

		// Send Broadcast to Show message on screen
		displayRegistrationMessageOnScreen(context, message);
	}

	// Unregister this account/device pair within the server.
	public void unregister(final Context context, final String regId) {

		Log.i(Config.TAG, "unregistering device (regId = " + regId + ")");

		GCMRegistrar.setRegisteredOnServer(context, false);

		String message = context.getString(R.string.server_unregistered);
		displayRegistrationMessageOnScreen(context, message);
	}

	// Issue a POST request to the server.
	private static void post(String registerID) throws IOException {

		HttpClient httpclient = null;
		HttpPost httppost = null;
		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.ADD_GCM_DEVICE_TOKEN);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

			nameValuePairs.add(new BasicNameValuePair("user_id",
					UserDetailsCacheManager.getObject(
							Controller.getAppBackgroundContext()).getUserID()));
			nameValuePairs
					.add(new BasicNameValuePair("password",
							UserDetailsCacheManager.getObject(
									Controller.getAppBackgroundContext())
									.getPassword()));
			nameValuePairs.add(new BasicNameValuePair("token", registerID));
			// nameValuePairs.add(new BasicNameValuePair("sandbox", sandBox));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {
				System.out
						.println(" TestResponse == "
								+ inputStreamToString(
										response.getEntity().getContent())
										.toString());

			}

		} catch (Exception e) {
			System.out.println(" TestException = " + e.getMessage());
		}

	}

	// Checking for all possible internet providers
	public boolean isConnectingToInternet() {

		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}

		}
		return false;
	}

	// Notifies UI to display a message.
	public void displayRegistrationMessageOnScreen(Context context,
			String message) {

		Intent intent = new Intent(Config.DISPLAY_REGISTRATION_MESSAGE_ACTION);
		intent.putExtra(Config.EXTRA_MESSAGE, message);

		// Send Broadcast to Broadcast receiver with message
		context.sendBroadcast(intent);

	}

	// Notifies UI to display a message.
	public void displayMessageOnScreen(Context context, String title,
			String message, String sid) {

		Intent intent = new Intent(Config.DISPLAY_MESSAGE_ACTION);
		intent.putExtra(Config.EXTRA_MESSAGE, title);
		intent.putExtra(Config.EXTRA_TITLE, message);
		intent.putExtra(Config.PID, sid);
		// intent.putExtra(Config.PID, pid);
		// Send Broadcast to Broadcast receiver with message
		context.sendBroadcast(intent);

	}

	// Function to display simple Alert Dialog
	public void showAlertDialog1(Context context, String title, String message,
			Boolean status) {
		AlertDialog alertDialog = new AlertDialog.Builder(context).create();

		// Set Dialog Title
		alertDialog.setTitle(title);

		// Set Dialog Message
		alertDialog.setMessage(message);

		if (status != null)
			// Set alert dialog icon
			alertDialog
					.setIcon((status) ? R.drawable.success : R.drawable.fail);

		// Set OK Button
		alertDialog.setButton(getResources().getString(R.string.dialog_ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		// Show Alert Message
		alertDialog.show();
	}

	private PowerManager.WakeLock wakeLock;

	public void acquireWakeLock(Context context) {
		if (wakeLock != null)
			wakeLock.release();

		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);

		wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.ON_AFTER_RELEASE, "WakeLock");

		wakeLock.acquire();
	}

	public void releaseWakeLock() {
		if (wakeLock != null)
			wakeLock.release();
		wakeLock = null;
	}

	public static StringBuilder inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();

		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		// Read response until the end
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Return full string
		return total;
	}
}
