package com.foodfeedback.networking;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.foodfeedback.onboarding.R;
import com.foodfeedback.utilities.SearchCoachListAdapter;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.CoachList;
import com.foodfeedback.valueobjects.MemberInfo;
import com.foodfeedback.valueobjects.SuggestedCoaches;
import com.foodfeedback.valueobjects.TransferObject;
import com.google.gson.Gson;

public class CoachesNetworkingServices implements Serializable {

	private static final long serialVersionUID = 1L;

	private CoachesNetworkingServices() {
		// private constructor
	}

	private static class SingletonHolder {
		public static final CoachesNetworkingServices INSTANCE = new CoachesNetworkingServices();
	}

	public static CoachesNetworkingServices getInstance() {
		return SingletonHolder.INSTANCE;
	}

	protected Object readResolve() {
		return getInstance();
	}

	private ArrayList<MemberInfo> myCoachList;
	private ListView allCoachesListView;
	private SearchCoachListAdapter messageCoachListAdapter;

	CoachList coachList;
	SuggestedCoaches suggestedCoaches;
	private String responseSTR;
	private String m_cResponseData;
	private String userIdStr, password;
	private Context context;
	private Activity objActivity;
	int loader = R.drawable.loader;
	private String nameText;
	private String excludeMyCoachesBoolean;
	public String[] allCoachNames = null, allCoachBios = null,
			allThumbUrl = null, allProfileUrl = null;
	public int[] allIds = null;
	public RelativeLayout coacheItem;

	public void requestFetchCoachesService(Context ctx, String userId,
			String password, String mineBoolean, Activity activityReference,
			int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userIdStr = userId;
		this.password = password;

		asyncTaskStartUp(serviceCall);
	}

	public void requestSearchCoachesService(Context ctx, String userId,
			String password, String nameText, String excludeMyCoachesBoolean,
			Activity activityReference, int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;
		this.userIdStr = userId;
		this.password = password;
		this.nameText = nameText;
		this.excludeMyCoachesBoolean = excludeMyCoachesBoolean;

		asyncTaskStartUp(serviceCall);
	}

	public void requestSuggestedCoachesService(Context ctx, String userId,
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

				case ServiceKeys.FETCH_COACHES_SERVICE:
					// result = fetchCoachesHttpGetRequest(userIdStr, password,
					// mineBoolean, context);
					break;
				case ServiceKeys.SEARCH_COACHES_SERVICE:
					result = searchCoachesHttpGetRequest();
					break;
				case ServiceKeys.SUGGESTED_COACHES_SERVICE:
					result = suggestedCoachesHttpGetRequest(userIdStr, password);
					break;
				}
				return result;
			}
		};

		Renderer<String> loginResult = new Renderer<String>() {

			@Override
			public void render(String results) {

				switch (checkSerive) {

				case ServiceKeys.FETCH_COACHES_SERVICE:
					updateResult(results, ServiceKeys.FETCH_COACHES_SUCCESS,
							ServiceKeys.FETCH_COACHES_SERVICE);
					break;
				case ServiceKeys.SEARCH_COACHES_SERVICE:
					updateResult(results, ServiceKeys.SEARCH_COACHES_SUCCESS,
							ServiceKeys.SEARCH_COACHES_SERVICE);
					break;
				case ServiceKeys.SUGGESTED_COACHES_SERVICE:
					updateResult(results,
							ServiceKeys.SUGGESTED_COACHES_SUCCESS,
							ServiceKeys.SUGGESTED_COACHES_SERVICE);
					break;
				}
			}
		};

		AsyncTaskExecutor<Object, Object, String> loginExecutor = new AsyncTaskExecutor<Object, Object, String>(
				context, loginBackgroundTask, loginResult, context.getResources().getString(R.string.loading_txt));
		loginExecutor.execute();
	}

	/*
	 * public String fetchCoachesHttpGetRequest(String userIdStr, String
	 * password, String mineBoolean, Context context) {}
	 */

	public String searchCoachesHttpGetRequest() {

		HttpClient httpclient = null;
		HttpGet httppost = null;

		try {
			// Add your data
			String userIdEncode = URLEncoder.encode(userIdStr, "UTF-8");
			String passwordEncode = URLEncoder.encode(password, "UTF-8");
			String nameTextEncode = URLEncoder.encode(nameText, "UTF-8");
			String excludeMyCoachesBooleanEncode = URLEncoder.encode(
					excludeMyCoachesBoolean, "UTF-8");

			String httpGetUrl = ServiceKeys.SEARCH_COACHES_URL + "user_id="
					+ userIdEncode + "&password=" + passwordEncode + "&name="
					+ nameTextEncode + "&exclude_my_coaches="
					+ excludeMyCoachesBooleanEncode;

			httpclient = new DefaultHttpClient();
			httppost = new HttpGet(httpGetUrl);

			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			httppost.setHeader("Accept-Language",Utilities.getLanguageHeaderToSet());
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			// responseSTR =
			// inputStreamToString(response.getEntity().getContent()).toString();

			if (statusCode == 200) {

				Gson gson = new Gson();
				CoachList searchCoaches = gson.fromJson(new InputStreamReader(
						response.getEntity().getContent()), CoachList.class);

				if (searchCoaches.getStatus() == 0) {

					TransferObject.setSearchList(searchCoaches);

				} else {
					System.out.println(" getMessagetext() =  "
							+ searchCoaches.getMessagetext());
				}

				m_cResponseData = searchCoaches.getMessagetext();

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

	public String suggestedCoachesHttpGetRequest(String userIdStr,
			String password) {

		HttpClient httpclient = null;
		HttpGet httppost = null;

		try {
			// Add your data
			String userIdEncode = URLEncoder.encode(userIdStr, "UTF-8");
			String passwordEncode = URLEncoder.encode(password, "UTF-8");

			String httpGetUrl = ServiceKeys.SUGGESTED_COACHES_URL + "user_id="
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
				suggestedCoaches = gson.fromJson(new InputStreamReader(response
						.getEntity().getContent()), SuggestedCoaches.class);

				TransferObject.setSuggestedCoaches(suggestedCoaches);

				m_cResponseData = suggestedCoaches.getMessagetext();

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

			switch (requestService) {

			case ServiceKeys.FETCH_COACHES_SERVICE:

				break;
			case ServiceKeys.SEARCH_COACHES_SERVICE:

				if (TransferObject.getSearchList() != null) {

					allCoachNames = null;
					allCoachBios = null;
					allIds = null;
					allThumbUrl = null;
					allProfileUrl = null;

					switch (ServiceKeys.SEARCH_BYNAME) {

					case ServiceKeys.SEARCH_BYNAME_MESSAGES:
						// MessagesActivity.coacheSearchNameLayout
						// .removeAllViews();
						break;

					case ServiceKeys.SEARCH_BYNAME_COACHES:
						// CoachesActivity.coacheSearchNameLayout.removeAllViews();
						break;
					}

					updateSearchByNameCoaches();
				} else {
					Utilities.showToast(context.getResources().getString(R.string.no_results), context);
					
				}

				break;
			case ServiceKeys.SUGGESTED_COACHES_SERVICE:
				/*
				 * if (suggestedCoaches.getData().getMatching_coaches().size()
				 * == 0 &&
				 * suggestedCoaches.getData().getPartially_matching_coaches
				 * ().size() == 0) { Utilities.showErrorToast(results,
				 * objActivity); } else { //
				 * TransferObject.setSuggestedCoaches(suggestedCoaches);
				 * context.startActivity(new Intent(context,
				 * CoachSearchListActivity.class)); }
				 */
				break;
			}
		} else {
			Utilities.showErrorToast(results, objActivity);
		}
	}

	private void updateSearchByNameCoaches() {
		myCoachList = new ArrayList<MemberInfo>();
		if (TransferObject.getSearchList() != null
				&& TransferObject.getSearchList().getData() != null
				&& TransferObject.getSearchList().getData().size() > 0) {

			Log.i("FoodFeedback", " NO. of Matched Coaches for search: "
					+ TransferObject.getSearchList().getData().size());

			myCoachList = TransferObject.getSearchList().getData();

		}
		allCoachesListView = (ListView) objActivity
				.findViewById(R.id.search_listView);
		messageCoachListAdapter = new SearchCoachListAdapter(objActivity,
				myCoachList, ServiceKeys.SEARCH_BYNAME_COACHES);
		allCoachesListView.setAdapter(messageCoachListAdapter);
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