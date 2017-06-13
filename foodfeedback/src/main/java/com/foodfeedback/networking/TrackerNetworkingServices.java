package com.foodfeedback.networking;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;

import com.foodfeedback.cachemanager.FetchWeightCacheManager;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.FetchWeight;
import com.foodfeedback.valueobjects.StatusUpdate;
import com.foodfeedback.weighttracker.WeightUpdates;
import com.google.gson.Gson;

public class TrackerNetworkingServices implements Serializable {

	private static final long serialVersionUID = 1L;

	private TrackerNetworkingServices() {
		// private constructor
	}

	private static class SingletonHolder {
		public static final TrackerNetworkingServices INSTANCE = new TrackerNetworkingServices();
	}

	public static TrackerNetworkingServices getInstance() {
		return SingletonHolder.INSTANCE;
	}

	protected Object readResolve() {
		return getInstance();
	}

	private String responseSTR;
	private String m_cResponseData;
	private String userIdStr, password;
	private Context context;
	private Activity objActivity;
	private String timeStamp;
	private String kilos;
	private String idWeight;

	public void requestAddWeightService(Context ctx, String userId,
			String password, String kilos, Activity activityReference,
			int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userIdStr = userId;
		this.password = password;
		this.kilos = kilos;

		asyncTaskStartUp(serviceCall);
	}

	public void requestUpdateWeightService(Context ctx, String userId,
			String password, String idWeight, String kilos, String timeStamp,
			Activity activityContext, int serviceCall) {

		this.context = ctx;
		this.userIdStr = userId;
		this.password = password;
		this.idWeight = idWeight;
		this.kilos = kilos;
		this.timeStamp = timeStamp;
		this.objActivity = activityContext;

		asyncTaskStartUp(serviceCall);
	}

	public void requestDeleteWeightService(Context ctx, String userId,
			String password, String idWeight, Activity activityReference,
			int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userIdStr = userId;
		this.password = password;
		this.idWeight = idWeight;

		asyncTaskStartUp(serviceCall);
	}

	public void requestFetchWeightsService(Context ctx, String userId,
			String password, Activity activityReference, int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userIdStr = userId;
		this.password = password;

		asyncTaskStartUp(serviceCall);
	}

	private void asyncTaskStartUp(int serviceCall) {

		final int checkSerive = serviceCall;

		BackgroundTask<Object, Object, String> loginBackgroundTask = new BackgroundTask<Object, Object, String>() {

			@Override
			public String execute(Object... params) {

				String result = null;

				switch (checkSerive) {

				case ServiceKeys.ADD_WEIGHT_SERVICE:
					result = addWeightHttpPostRequest(userIdStr, password,
							kilos, context);
					break;
				case ServiceKeys.UPDATE_WEIGHT_SERVICE:
					result = updateWeightHttpPostRequest();
					break;
				case ServiceKeys.DELETE_WEIGHT_SERVICE:
					result = deleteWeightHttpPostRequest(userIdStr, password,
							idWeight, context);
					break;
				case ServiceKeys.FETCH_WEIGHTS_SERVICE:
					result = fetchWeightsHttpGetRequest(userIdStr, password);
					break;
				}
				return result;
			}
		};

		Renderer<String> loginResult = new Renderer<String>() {

			@Override
			public void render(String results) {

				switch (checkSerive) {

				case ServiceKeys.ADD_WEIGHT_SERVICE:
					updateResult(results, ServiceKeys.ADD_WEIGHT_SUCCESS,
							ServiceKeys.ADD_WEIGHT_SERVICE);
					break;
				case ServiceKeys.UPDATE_WEIGHT_SERVICE:
					updateResult(results, ServiceKeys.UPDATE_WEIGHT_SUCCESS,
							ServiceKeys.UPDATE_WEIGHT_SERVICE);
					break;
				case ServiceKeys.DELETE_WEIGHT_SERVICE:
					updateResult(results, ServiceKeys.DELETE_WEIGHT_SUCCESS,
							ServiceKeys.DELETE_WEIGHT_SERVICE);
					break;
				case ServiceKeys.FETCH_WEIGHTS_SERVICE:
					updateResult(results, ServiceKeys.FETCH_WEIGHTS_SUCCESS,
							ServiceKeys.FETCH_WEIGHTS_SERVICE);
					break;
				}
			}
		};
		
		String messageForLoading = context.getResources().getString(
				R.string.loading_txt);

		AsyncTaskExecutor<Object, Object, String> loginExecutor = new AsyncTaskExecutor<Object, Object, String>(
				context, loginBackgroundTask, loginResult,messageForLoading);
		loginExecutor.execute();
	}

	public String addWeightHttpPostRequest(String userIdStr, String password,
			String kilos, Context ctx) {

		HttpClient httpclient = null;
		HttpPost httppost = null;
		this.context = ctx;
		this.objActivity = (Activity) ctx;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.ADD_WEIGHT_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

			nameValuePairs.add(new BasicNameValuePair("user_id", userIdStr));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("kilos", kilos));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				Gson gson = new Gson();
				WeightUpdates weightUpdates = gson
						.fromJson(new InputStreamReader(response.getEntity()
								.getContent()), WeightUpdates.class);
				if (weightUpdates.getStatus() == 0)
					fetchWeightsHttpGetRequest(userIdStr, password);
				// System.out.println(" getMessagetext() =  " +
				// weightUpdates.getMessagetext());
				// System.out.println(" getStatus() =  " +
				// weightUpdates.getStatus());
				// System.out.println(" data getId() =  " +
				// weightUpdates.getData().getKilos());
				m_cResponseData = weightUpdates.getMessagetext();

			} else {
				checkingError(context, response);
			}
		} catch (ClientProtocolException e) {
			checkingException(context, e.getLocalizedMessage());
		} catch (IOException e) {
			checkingException(context, e.getLocalizedMessage());
		}
		return m_cResponseData;
	}

	public String updateWeightHttpPostRequest() {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.UPDATE_WEIGHT_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);

			nameValuePairs.add(new BasicNameValuePair("user_id", userIdStr));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("id", idWeight));
			nameValuePairs.add(new BasicNameValuePair("kilos", kilos));
			nameValuePairs.add(new BasicNameValuePair("timestamp", timeStamp));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				Gson gson = new Gson();
				WeightUpdates weightUpdates = gson
						.fromJson(new InputStreamReader(response.getEntity()
								.getContent()), WeightUpdates.class);
				System.out.println(" getMessagetext() =  "
						+ weightUpdates.getMessagetext());
				System.out.println(" getStatus() =  "
						+ weightUpdates.getStatus());
				System.out.println(" data getId() =  "
						+ weightUpdates.getData().getKilos());
				m_cResponseData = weightUpdates.getMessagetext();

			} else {
				checkingError(context, response);
			}
		} catch (ClientProtocolException e) {
			checkingException(context, e.getLocalizedMessage());
		} catch (IOException e) {
			checkingException(context, e.getLocalizedMessage());
		}
		return m_cResponseData;
	}

	public String deleteWeightHttpPostRequest(String userIdStr,
			String password, String idWeight, Context context) {

		HttpClient httpclient = null;
		HttpPost httppost = null;
		objActivity = (Activity) context;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.DELETE_WEIGHT_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

			nameValuePairs.add(new BasicNameValuePair("user_id", userIdStr));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("id", idWeight));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				Gson gson = new Gson();
				StatusUpdate statusUpdates = gson
						.fromJson(new InputStreamReader(response.getEntity()
								.getContent()), StatusUpdate.class);

				if (statusUpdates.getStatus() == 0)
					fetchWeightsHttpGetRequest(userIdStr, password);

				m_cResponseData = statusUpdates.getMessagetext();

			} else {
				checkingError(context, response);
			}
		} catch (ClientProtocolException e) {
			checkingException(context, e.getLocalizedMessage());
		} catch (IOException e) {
			checkingException(context, e.getLocalizedMessage());
		}
		return m_cResponseData;
	}

	public String fetchWeightsHttpGetRequest(String userIdStr, String password) {

		HttpClient httpclient = null;
		HttpGet httppost = null;

		try {
			// Add your data
			String userIdEncode = URLEncoder.encode(userIdStr, "UTF-8");
			String passwordEncode = URLEncoder.encode(password, "UTF-8");

			String httpGetUrl = ServiceKeys.FETCH_WEIGHTS_URL + "user_id="
					+ userIdEncode + "&password=" + passwordEncode;

			httpclient = new DefaultHttpClient();
			httppost = new HttpGet(httpGetUrl);

			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			httppost.setHeader("Accept-Language",Utilities.getLanguageHeaderToSet());
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				Gson gson = new Gson();
				FetchWeight fetchWeight = gson.fromJson(new InputStreamReader(
						response.getEntity().getContent()), FetchWeight.class);
				if (fetchWeight.getData() != null) {
					try {
						FetchWeightCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								fetchWeight);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				m_cResponseData = fetchWeight.getMessagetext();

			} else {
				checkingError(context, response);
			}
		} catch (ClientProtocolException e) {
			checkingException(context, e.getLocalizedMessage());
		} catch (IOException e) {
			checkingException(context, e.getLocalizedMessage());
		}
		return m_cResponseData;
	}

	public void updateResult(String results, String statusMessage,
			int requestService) {
		System.out.println(" responseSTR  = " + responseSTR);
		if (results.equals(statusMessage)) {

			// Toast.makeText(context, "" + statusMessage,
			// Toast.LENGTH_SHORT).show();

			switch (requestService) {

			case ServiceKeys.ADD_WEIGHT_SERVICE:
				break;
			case ServiceKeys.UPDATE_WEIGHT_SERVICE:
				break;
			case ServiceKeys.FETCH_WEIGHTS_SERVICE:
				break;
			}
		} else {
			Utilities.showErrorToast(results, objActivity);
		}
	}

	private void checkingException(Context check, String checkException) {

		if (Utilities.isConnectingToInternet(check))
			m_cResponseData = "" + checkException;
		else
			m_cResponseData = check.getResources().getString(
					R.string.network_connection);
	}

	private void checkingError(Context checkError, HttpResponse response) {

		if (Utilities.isConnectingToInternet(checkError))
			m_cResponseData = "" + response.getStatusLine().getReasonPhrase();
		else
			m_cResponseData = checkError.getResources().getString(
					R.string.network_connection);

	}
}