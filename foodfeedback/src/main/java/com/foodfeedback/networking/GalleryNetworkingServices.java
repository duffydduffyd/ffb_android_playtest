package com.foodfeedback.networking;

import java.io.File;
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
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.foodfeedback.cachemanager.FetchGalleryCacheManager;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.FoodTabActivity;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.AddGalleryImage;
import com.foodfeedback.valueobjects.FetchGallery;
import com.google.gson.Gson;

public class GalleryNetworkingServices implements Serializable {

	private static final long serialVersionUID = 1L;

	private GalleryNetworkingServices() {
		// private constructor
	}

	private static class SingletonHolder {
		public static final GalleryNetworkingServices INSTANCE = new GalleryNetworkingServices();
	}

	public static GalleryNetworkingServices getInstance() {
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
	private String imagePath;
	private String activeStatus;
	private String galleryImageId;

	public void requestFetchGalleryService(Context ctx, String userId,
			String password, String activeStatus, Activity activityReference,
			int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userIdStr = userId;
		this.password = password;
		this.activeStatus = activeStatus;

		asyncTaskStartUp(serviceCall);
	}

	public void requestUpdateGalleryService(Context ctx, String userId,
			String password, String galleryImageId, String activeStatus,
			Activity activityReference, int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userIdStr = userId;
		this.password = password;
		this.galleryImageId = galleryImageId;
		this.activeStatus = activeStatus;

		asyncTaskStartUp(serviceCall);
	}

	public void requestAddImageGalleryService(Context ctx, String userId,
			String password, String imagePath, Activity activityReference,
			int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userIdStr = userId;
		this.password = password;
		this.imagePath = imagePath;
		// this.md5Hash = md5Hash;

		asyncTaskStartUp(serviceCall);
	}

	private void asyncTaskStartUp(int serviceCall) {

		final int checkSerive = serviceCall;

		BackgroundTask<Object, Object, String> loginBackgroundTask = new BackgroundTask<Object, Object, String>() {

			@Override
			public String execute(Object... params) {

				String result = null;

				switch (checkSerive) {

				case ServiceKeys.FETCH_GALLERY_SERVICE:
					result = fetchGalleryHttpGetRequest(userIdStr, password,
							activeStatus);
					break;

				case ServiceKeys.UPDATE_GALLERY_SERVICE:
					result = updateGalleryHttpPostRequest();
					break;
				case ServiceKeys.ADD_IMAGE_GALLERY_SERVICE:
					result = addImageGalleryHttpPostRequest();
					break;
				}
				return result;
			}
		};

		Renderer<String> loginResult = new Renderer<String>() {

			@Override
			public void render(String results) {

				switch (checkSerive) {

				case ServiceKeys.FETCH_GALLERY_SERVICE:
					updateResult(results, ServiceKeys.FETCH_GALLERY_SUCCESS,
							ServiceKeys.FETCH_GALLERY_SERVICE);
					break;

				case ServiceKeys.UPDATE_GALLERY_SERVICE:
					updateResult(results, ServiceKeys.UPDATE_GALLERY_SUCCESS,
							ServiceKeys.UPDATE_GALLERY_SERVICE);
					break;
				case ServiceKeys.ADD_IMAGE_GALLERY_SERVICE:
					updateResult(results,
							ServiceKeys.ADD_IMAGE_GALLERY_SUCCESS,
							ServiceKeys.ADD_IMAGE_GALLERY_SERVICE);
					break;
				}
			}
		};

		String messageForLoading = context.getResources().getString(
				R.string.loading_txt);
		if (checkSerive == ServiceKeys.ADD_IMAGE_GALLERY_SERVICE) {
			messageForLoading = context.getResources().getString(
					R.string.sending_txt);
		}

		AsyncTaskExecutor<Object, Object, String> loginExecutor = new AsyncTaskExecutor<Object, Object, String>(
				context, loginBackgroundTask, loginResult, messageForLoading);
		loginExecutor.execute();
	}

	public String fetchGalleryHttpGetRequest(String userIdStr, String password,
			String activeStatus) {

		HttpClient httpclient = null;
		HttpGet httppost = null;

		try {
			// Add your data
			String userIdEncode = URLEncoder.encode(userIdStr, "UTF-8");
			String passwordEncode = URLEncoder.encode(password, "UTF-8");
			String activeStatusEncode = URLEncoder
					.encode(activeStatus, "UTF-8");

			String httpGetUrl = ServiceKeys.FETCH_GALLERY_URL + "user_id="
					+ userIdEncode + "&password=" + passwordEncode + "&active="
					+ activeStatusEncode;

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
				FetchGallery fetchGallery = gson
						.fromJson(new InputStreamReader(response.getEntity()
								.getContent()), FetchGallery.class);

				if (fetchGallery.getStatus() == 0
						&& fetchGallery.getData() != null
						&& fetchGallery.getData().size() > 0) {

					try {
						FetchGalleryCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								fetchGallery);
					} catch (Exception e) {
						e.printStackTrace();
					}

					System.out.println(" size() =  "
							+ fetchGallery.getData().size());
					System.out.println(" getMessagetext() =  "
							+ fetchGallery.getMessagetext());
					System.out.println(" getStatus() =  "
							+ fetchGallery.getStatus());

				} else {

					System.out.println(" getMessagetext() =  "
							+ fetchGallery.getMessagetext());
				}
				m_cResponseData = fetchGallery.getMessagetext();
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

	public String updateGalleryHttpPostRequest() {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.UPDATE_GALLERY_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);

			nameValuePairs.add(new BasicNameValuePair("user_id", userIdStr));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("gallery_image_id",
					galleryImageId));
			nameValuePairs.add(new BasicNameValuePair("active", activeStatus));
			// nameValuePairs.add(new BasicNameValuePair("timestamp",
			// timeStamp));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				Gson gson = new Gson();
				AddGalleryImage addGallery = gson
						.fromJson(new InputStreamReader(response.getEntity()
								.getContent()), AddGalleryImage.class);

				if (addGallery.getStatus() == 0) {
					FetchGallery fetchGalleryObj = null;
					try {
						fetchGalleryObj = FetchGalleryCacheManager
								.getObject(Controller.getAppBackgroundContext());
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (int i = 0; i < fetchGalleryObj.getData().size(); i++) {
						// fetchGalleryObj.getData().get(i).getId();

						System.out.println(" 55getid() "
								+ fetchGalleryObj.getData().get(i).getId()
								+ " ==  " + galleryImageId);

						if (fetchGalleryObj.getData().get(i).getId()
								.equals(galleryImageId)) {
							fetchGalleryObj.getData().remove(i);
							try {
								FetchGalleryCacheManager.saveObject(
										Controller.getAppBackgroundContext(),
										null);
								FetchGalleryCacheManager.saveObject(
										Controller.getAppBackgroundContext(),
										fetchGalleryObj);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}

				} else {

					System.out.println(" getMessagetext() =  "
							+ addGallery.getMessageText());
				}

				m_cResponseData = addGallery.getMessageText();

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

	public String updateGalleryHttpPostRequest(Context ctx, String userID,
			String userPassword, String imageIDToUpdate,
			String statuValueToUpdate) {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.UPDATE_GALLERY_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);

			nameValuePairs.add(new BasicNameValuePair("user_id", userID));
			nameValuePairs
					.add(new BasicNameValuePair("password", userPassword));
			nameValuePairs.add(new BasicNameValuePair("gallery_image_id",
					imageIDToUpdate));
			nameValuePairs.add(new BasicNameValuePair("active",
					statuValueToUpdate));
			// nameValuePairs.add(new BasicNameValuePair("timestamp",
			// timeStamp));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				Gson gson = new Gson();
				AddGalleryImage addGallery = gson
						.fromJson(new InputStreamReader(response.getEntity()
								.getContent()), AddGalleryImage.class);

				if (addGallery.getStatus() == 0) {

					FetchGallery fetchGalleryObj = null;
					try {
						fetchGalleryObj = FetchGalleryCacheManager
								.getObject(Controller.getAppBackgroundContext());
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (int i = 0; i < fetchGalleryObj.getData().size(); i++) {
						// fetchGalleryObj.getData().get(i).getId();

						System.out.println(" 55getid() "
								+ fetchGalleryObj.getData().get(i).getId()
								+ " ==  " + imageIDToUpdate);

						if (fetchGalleryObj.getData().get(i).getId()
								.equals(imageIDToUpdate)) {
							fetchGalleryObj.getData().remove(i);
							try {
								FetchGalleryCacheManager.saveObject(
										Controller.getAppBackgroundContext(),
										null);
								FetchGalleryCacheManager.saveObject(
										Controller.getAppBackgroundContext(),
										fetchGalleryObj);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}

				} else {

					System.out.println(" getMessagetext() =  "
							+ addGallery.getMessageText());
				}

				m_cResponseData = addGallery.getMessageText();

			} else {
				m_cResponseData = ""
						+ response.getStatusLine().getReasonPhrase();
			}
		} catch (ClientProtocolException e) {
			// m_cResponseData = e.getLocalizedMessage();
			checkingException(ctx, e.getLocalizedMessage());
		} catch (IOException e) {
			// m_cResponseData = e.getLocalizedMessage();
			checkingException(ctx, e.getLocalizedMessage());
		}
		return m_cResponseData;
	}

	public String addImageGalleryHttpPostRequest() {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.ADD_IMAGE_GALLERY_URL);

			MultipartEntity entity = new MultipartEntity();

			entity.addPart("user_id", new StringBody(userIdStr));
			entity.addPart("password", new StringBody(password));
			entity.addPart("image", new StringBody(imagePath));

			if (!imagePath.equals(null) && !imagePath.equals("")) {

				File file = new File(imagePath);

				System.out.println(" imageTesting " + file.getAbsolutePath());

				entity.addPart("image", new FileBody(file));
			} else {
			}

			httppost.setEntity(entity);

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				Gson gson = new Gson();
				AddGalleryImage addGallery = gson
						.fromJson(new InputStreamReader(response.getEntity()
								.getContent()), AddGalleryImage.class);
				if (addGallery != null) {
					GalleryNetworkingServices.getInstance()
							.fetchGalleryHttpGetRequest(userIdStr, password,
									"1");
				}
				m_cResponseData = addGallery.getMessageText();

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

	@SuppressLint("NewApi")
	public void updateResult(String results, String statusMessage,
			int requestService) {
		System.out.println(" responseSTR  = " + responseSTR);
		if (results.equals(statusMessage)) {

			switch (requestService) {

			case ServiceKeys.UPDATE_GALLERY_SERVICE:
				Utilities.showToast(objActivity.getResources().getString(R.string.gallery_image_updated), objActivity);

				// objActivity.finish();
				// new FoodGalleryFlipper().updateGallery();
				// objActivity.recreate();
				objActivity.finish();
				// context.startActivity(objActivity.getIntent());
				// context.startActivity(objActivity.this,
				// context).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				break;
			case ServiceKeys.ADD_IMAGE_GALLERY_SERVICE:
				Intent intentFood = new Intent(context, FoodTabActivity.class)
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				context.startActivity(intentFood);
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