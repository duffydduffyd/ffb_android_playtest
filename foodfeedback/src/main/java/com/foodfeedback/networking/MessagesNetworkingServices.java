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

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.foodfeedback.cachemanager.InteractedWithCacheManager;
import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.MessagesFromUserCacheManager;
import com.foodfeedback.cachemanager.UnReadCacheManager;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.FoodTabActivity;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.AccountOperations;
import com.foodfeedback.valueobjects.InteractedWith;
import com.foodfeedback.valueobjects.MessageList;
import com.google.gson.Gson;

public class MessagesNetworkingServices implements Serializable {

	private static final long serialVersionUID = 1L;

	public MessagesNetworkingServices(Context ctx) {
		this.context = ctx;
	}

	private String responseSTR;
	private String m_cResponseData;
	private String userIdStr, password;
	private Context context;
	private String receiverId;
	private String textMessage;
	private String imagePath;
//	private String messageId;
//	private String activeStatus;
//	private String senderId;
//	private String limitStr;
//	private String sinceId;
//	private String maxId;
//	private String minResults;

	// public static final int UPDATE_MESSAGESS = 1004;

	public void requestSendMessageService(Context ctx, String userId,
			String password, String receiverId, String textMessage,
			String imagePath, int serviceCall) {

		this.context = ctx;

		this.userIdStr = userId;
		this.password = password;
		this.receiverId = receiverId;
		this.textMessage = textMessage;
		this.imagePath = imagePath;

		asyncTaskStartUp(serviceCall);
	}
	
	
	private void asyncTaskStartUp(int serviceCall) {

		final int checkSerive = serviceCall;

		BackgroundTask<Object, Object, String> loginBackgroundTask = new BackgroundTask<Object, Object, String>() {

			@Override
			public String execute(Object... params) {

				String result = null;

				switch (checkSerive) {

				case ServiceKeys.SEND_MESSAGE_SERVICE:
					result = sendMessageHttpPostRequest(userIdStr, password,
							receiverId, textMessage, imagePath);
					break;
//				case ServiceKeys.MESSAGE_READ_SERVICE:
//					result = messageReadHttpPostRequest(context, userIdStr,
//							password, messageId);
//					break;
//				// case ServiceKeys.UPDATE_MESSAGE_SERVICE:
//				// result = updateMessageHttpPostRequest();
//				// break;
//				case ServiceKeys.MESSAGE_FROM_USER_SERVICE:
//					result = messageFromUserHttpGetRequest(context, userIdStr,
//							password, senderId, limitStr, sinceId, maxId,
//							minResults);
//					break;
//				case ServiceKeys.MESSAGE_UNREAD_SERVICE:
//					result = messageUnreadHttpGetRequest(context, userIdStr,
//							password, limitStr, sinceId);
//					break;
//				case ServiceKeys.MESSAGE_WITH_IMAGES_SERVICE:
//					result = messageWithImagesHttpGetRequest(context,
//							userIdStr, password, activeStatus, sinceId,
//							limitStr);
//					break;
				}
				return result;
			}
		};

		Renderer<String> loginResult = new Renderer<String>() {

			@Override
			public void render(String results) {

				switch (checkSerive) {

				case ServiceKeys.SEND_MESSAGE_SERVICE:
					updateResult(
							results,
							context.getResources().getString(
									R.string.photo_sent_success),
							ServiceKeys.SEND_MESSAGE_SERVICE);
					break;
//				case ServiceKeys.MESSAGE_READ_SERVICE:
//					updateResult(results, ServiceKeys.MESSAGE_READ_SUCCESS,
//							ServiceKeys.MESSAGE_READ_SERVICE);
//					break;
//				case ServiceKeys.UPDATE_MESSAGE_SERVICE:
//					updateResult(results, ServiceKeys.UPDATE_MESSAGE_SUCCESS,
//							ServiceKeys.UPDATE_MESSAGE_SERVICE);
//					break;
//				case ServiceKeys.MESSAGE_FROM_USER_SERVICE:
//					updateResult(results,
//							ServiceKeys.MESSAGE_FROM_USER_SUCCESS,
//							ServiceKeys.MESSAGE_FROM_USER_SERVICE);
//					break;
//				case ServiceKeys.MESSAGE_UNREAD_SERVICE:
//					updateResult(results, ServiceKeys.MESSAGE_UNREAD_SUCCESS,
//							ServiceKeys.MESSAGE_UNREAD_SERVICE);
//					break;
//				case ServiceKeys.MESSAGE_WITH_IMAGES_SERVICE:
//					updateResult(results,
//							ServiceKeys.MESSAGE_WITH_IMAGES_SUCCESS,
//							ServiceKeys.MESSAGE_WITH_IMAGES_SERVICE);
//					break;
				}
			}
		};
		String messageForLoading = context.getResources().getString(
				R.string.loading_txt);
		if (checkSerive == ServiceKeys.SEND_MESSAGE_SERVICE) {
			messageForLoading = context.getResources().getString(
					R.string.sending_txt);
		}
		AsyncTaskExecutor<Object, Object, String> loginExecutor = new AsyncTaskExecutor<Object, Object, String>(
				context, loginBackgroundTask, loginResult, messageForLoading);
		loginExecutor.execute();
	}

//	public void requestMessageReadService(Context ctx, String userId,
//			String oldPassword, String messageId, Activity activityReference,
//			int serviceCall) {
//
//		this.context = ctx;
//
//		this.userIdStr = userId;
//		this.password = oldPassword;
//		this.messageId = messageId;
//
//		asyncTaskStartUp(serviceCall);
//	}

	// public void requestUpdateMessageService(Context ctx, String userId,
	// String password, String messageId, String activeStatus,
	// Activity activityContext, int serviceCall) {
	//
	// this.context = ctx;
	// this.userIdStr = userId;
	// this.password = password;
	// this.messageId = messageId;
	// this.activeStatus = activeStatus;
	//
	// asyncTaskStartUp(serviceCall);
	// }

//	public void requestMessageFromUserService(Context ctx, String userId,
//			String password, String senderId, String limitStr, String sinceId,
//			String maxId, String minResults, Activity activityReference,
//			int serviceCall) {
//
//		this.context = ctx;
//
//		this.userIdStr = userId;
//		this.password = password;
//		this.senderId = senderId;
//		this.limitStr = limitStr;
//		this.sinceId = sinceId;
//		this.maxId = maxId;
//		this.minResults = minResults;
//
//		asyncTaskStartUp(serviceCall);
//	}

//	public void requestMessageUnreadService(Context ctx, String userId,
//			String password, String limitStr, String sinceId,
//			Activity activityReference, int serviceCall) {
//
//		this.context = ctx;
//
//		this.userIdStr = userId;
//		this.password = password;
//		this.limitStr = limitStr;
//		this.sinceId = sinceId;
//
//		asyncTaskStartUp(serviceCall);
//	}



//	public String messageWithImagesHttpGetRequest(Context context,
//			String userIdStr, String password, String activeStatus,
//			String sinceId, String limitStr) {
//
//		HttpClient httpclient = null;
//		HttpGet httppost = null;
//
//		try {
//			// Add your data
//			String userIdEncode = URLEncoder.encode(userIdStr, "UTF-8");
//			String passwordEncode = URLEncoder.encode(password, "UTF-8");
//			String activeStatusEncode = URLEncoder
//					.encode(activeStatus, "UTF-8");
//			String sinceIdEncode = URLEncoder.encode(sinceId, "UTF-8");
//			String limitStrEncode = URLEncoder.encode(limitStr, "UTF-8");
//
//			String httpGetUrl = ServiceKeys.MESSAGE_WITH_IMAGES_URL
//					+ "user_id=" + userIdEncode + "&password=" + passwordEncode
//					+ "&active=" + activeStatusEncode + "&since_id="
//					+ sinceIdEncode + "&limit=" + limitStrEncode;
//
//			httpclient = new DefaultHttpClient();
//			httppost = new HttpGet(httpGetUrl);
//
//			httppost.setHeader("Content-Type",
//					"application/x-www-form-urlencoded");
//
//			// Execute HTTP Post Request
//			HttpResponse response = httpclient.execute(httppost);
//
//			int statusCode = response.getStatusLine().getStatusCode();
//
//			// responseSTR =
//			// inputStreamToString(response.getEntity().getContent()).toString();
//
//			if (statusCode == 200) {
//
//				Gson gson = new Gson();
//				MessageList messageWithImagesObj = gson
//						.fromJson(new InputStreamReader(response.getEntity()
//								.getContent()), MessageList.class);
//
//				// if(messageWithImagesObj.getStatus() == 0)
//				// MessageWithImagesCacheManager.saveObject(Controller.getAppBackgroundContext(),
//				// messageWithImagesObj);
//
//			} else {
//				// checkingError(context, response);
//			}
//		} catch (ClientProtocolException e) {
//			// checkingException(context, e.getLocalizedMessage());
//		} catch (IOException e) {
//			// checkingException(context, e.getLocalizedMessage());
//		}
//		return m_cResponseData;
//	}

	public String messageFromUserHttpGetRequest(Context context,
			String userIdStr, String password, String senderId,
			String limitStr, String sinceId, String maxId, String minResults) {

		HttpClient httpclient = null;
		HttpGet httppost = null;
		this.context = context;

		Log.i("Foodfeedback", "Message from User HTTP Request");

		try {
			// Add your data
			String userIdEncode = URLEncoder.encode(userIdStr, "UTF-8");
			String passwordEncode = URLEncoder.encode(password, "UTF-8");
			String senderIdEncode = URLEncoder.encode(senderId, "UTF-8");
			String limitStrEncode = URLEncoder.encode(limitStr, "UTF-8");
			// String sinceIdEncode = URLEncoder.encode(sinceId, "UTF-8");
			String httpGetUrl;
			if (maxId.equals("")) {
				httpGetUrl = ServiceKeys.MESSAGE_FROM_USER_URL + "user_id="
						+ userIdEncode + "&password=" + passwordEncode
						+ "&sender_id=" + senderIdEncode + "&limit="
						+ limitStrEncode;
			} else {
				String maxIdEncode = URLEncoder.encode(maxId, "UTF-8");
				httpGetUrl = ServiceKeys.MESSAGE_FROM_USER_URL + "user_id="
						+ userIdEncode + "&password=" + passwordEncode
						+ "&sender_id=" + senderIdEncode + "&limit="
						+ limitStrEncode + "&since_id=" + "" + "&max_id="
						+ maxIdEncode;
			}

			httpclient = new DefaultHttpClient();
			httppost = new HttpGet(httpGetUrl);

			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			httppost.setHeader("Accept-Language",Utilities.getLanguageHeaderToSet());
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			AccountOperations accountObject = null;
			try {
				accountObject = LoginDetailsCacheManager.getObject(Controller
						.getAppBackgroundContext());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (statusCode == 200) {
				Gson gson = new Gson();
				MessageList messageFromUserObj = gson
						.fromJson(new InputStreamReader(response.getEntity()
								.getContent()), MessageList.class);

				if (messageFromUserObj.getStatus() == 0 &&  messageFromUserObj.getData()!=null
						&& messageFromUserObj.getData().size() != 0) {
					StringBuffer messageIds = new StringBuffer();
					if (messageFromUserObj.getMessagetext().equals(
							ServiceKeys.MESSAGE_FROM_USER_SUCCESS)) {
						for (int i = 0; i < messageFromUserObj.getData().size(); i++) {

							if (Integer.parseInt(messageFromUserObj.getData()
									.get(i).getRead()) == 0) {
								if (i == messageFromUserObj.getData().size() - 1) {
									messageIds.append(messageFromUserObj
											.getData().get(i).getId());

									messageReadHttpPostRequest(context,
											userIdStr, password,
											messageIds.toString());

								} else {
									messageIds.append(messageFromUserObj
											.getData().get(i).getId()
											+ ",");
								}
							}
						}
					}
					// TransferObject.setMessageFromUser(messageFromUserObj);
					if (maxId.equals("")) {
						try {
							if (messageFromUserObj.getData().size() != 0) {

								MessagesFromUserCacheManager.saveObject(
										Controller.getAppBackgroundContext(),
										null, senderId);

								MessagesFromUserCacheManager.saveObject(
										Controller.getAppBackgroundContext(),
										messageFromUserObj, senderId);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						try {
							MessageList temp = MessagesFromUserCacheManager
									.getObject(Controller
											.getAppBackgroundContext(),
											senderId);
							if (messageFromUserObj.getData().size() != 0) {
								messageFromUserObj.getData().addAll(
										temp.getData());
							}
							MessagesFromUserCacheManager.saveObject(
									Controller.getAppBackgroundContext(),
									messageFromUserObj, senderId);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					if (messageFromUserObj.getData() != null
							&& messageFromUserObj.getData().size() != 0) {
						// String thumbInteractedURL;
						InteractedWith interactedWithObj = null;
						try {
							interactedWithObj = InteractedWithCacheManager
									.getObject(Controller
											.getAppBackgroundContext());
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}

				m_cResponseData = messageFromUserObj.getMessagetext();

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

	public String messageUnreadHttpGetRequest(Context context,
			String userIdStr, String password, String limitStr, String sinceId) {

		HttpClient httpclient = null;
		HttpGet httppost = null;
		String httpGetUrl = null;
		MessageList messageUnreadObj = null;

		try {
			// Add your data
			String userIdEncode = URLEncoder.encode(userIdStr, "UTF-8");
			String passwordEncode = URLEncoder.encode(password, "UTF-8");
			String limitStrEncode = URLEncoder.encode(limitStr, "UTF-8");
			String sinceIdEncode = URLEncoder.encode(sinceId, "UTF-8");

			httpGetUrl = ServiceKeys.MESSAGE_UNREAD_URL + "user_id="
					+ userIdEncode + "&password=" + passwordEncode + "&limit="
					+ limitStrEncode + "&since_id=" + sinceIdEncode;

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
				messageUnreadObj = gson.fromJson(new InputStreamReader(response
						.getEntity().getContent()), MessageList.class);

				if (messageUnreadObj.getStatus() == 0)
					try {
						UnReadCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								messageUnreadObj);
					} catch (Exception e) {
						e.printStackTrace();
					}

				m_cResponseData = messageUnreadObj.getMessagetext();

			} else {
				// checkingError(context, response);
			}
		} catch (ClientProtocolException e) {
			// checkingException(context, e.getLocalizedMessage());
		} catch (IOException e) {
			// checkingException(context, e.getLocalizedMessage());
		}
		return m_cResponseData;
	}

	public String updateMessageHttpPostRequest(Context mContext,
			String mUserID, String mPassword, String mMessageID,
			String mActiveStatus) {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.UPDATE_MESSAGE_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);

			nameValuePairs.add(new BasicNameValuePair("user_id", mUserID));
			nameValuePairs.add(new BasicNameValuePair("password", mPassword));
			nameValuePairs
					.add(new BasicNameValuePair("message_id", mMessageID));
			nameValuePairs.add(new BasicNameValuePair("active", mActiveStatus));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				Gson gson = new Gson();
				MessageList updateMessageObj = gson
						.fromJson(new InputStreamReader(response.getEntity()
								.getContent()), MessageList.class);

				m_cResponseData = updateMessageObj.getMessagetext();

			} else {
				checkingError(mContext, response);
			}
		} catch (ClientProtocolException e) {
			checkingException(mContext, e.getLocalizedMessage());
		} catch (IOException e) {
			checkingException(mContext, e.getLocalizedMessage());
		}
		return m_cResponseData;
	}

	public String messageReadHttpPostRequest(Context context, String userIdStr,
			String password, String messageId) {

		HttpClient httpclient = null;
		HttpPost httppost = null;
		this.context = context;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.MESSAGE_READ_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

			nameValuePairs.add(new BasicNameValuePair("user_id", userIdStr));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("message_id", messageId));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				Gson gson = new Gson();
				MessageList readMessageObj = gson
						.fromJson(new InputStreamReader(response.getEntity()
								.getContent()), MessageList.class);

				if (readMessageObj.getMessagetext().equals(
						ServiceKeys.MESSAGE_READ_SUCCESS))
					messageUnreadHttpGetRequest(context, userIdStr, password,
							"30", "");

				m_cResponseData = readMessageObj.getMessagetext();

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

	public String sendMessageHttpPostRequest(String userID, String password,
			String receiverId, String textMessage, String imagePath) {

		boolean isImageMessage = false;
		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.SEND_MESSAGE_URL);
			// Add your data
			// httppost.setHeader("Content-Type",
			// "application/x-www-form-urlencoded");

			MultipartEntity entity = new MultipartEntity();

			entity.addPart("user_id", new StringBody(userID));
			entity.addPart("password", new StringBody(password));
			entity.addPart("receiver_id", new StringBody(receiverId));

			if (textMessage != null && textMessage != "") {
				entity.addPart("text", new StringBody(textMessage));
				isImageMessage = false;
			}

			if (imagePath != null && imagePath != "") {
				File file = new File(imagePath);
				System.out.println(" imageTesting " + file.getAbsolutePath());
				entity.addPart("image", new FileBody(file));
				isImageMessage = true;
			}

			httppost.setEntity(entity);

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				Gson gson = new Gson();
				MessageList sendMessageObj = gson
						.fromJson(new InputStreamReader(response.getEntity()
								.getContent()), MessageList.class);

				if (imagePath != null && imagePath != "")
					GalleryNetworkingServices.getInstance()
							.fetchGalleryHttpGetRequest(userID, password, "1");

				if (sendMessageObj != null && sendMessageObj.getData() != null
						&& sendMessageObj.getData().size() > 0) {

					System.out.println(" sendMessageObj size() =  "
							+ sendMessageObj.getData().size());
					System.out.println(" sendMessageObj  getMessagetext() =  "
							+ sendMessageObj.getMessagetext());
					System.out.println(" getStatus() =  "
							+ sendMessageObj.getStatus());

				}

				if (sendMessageObj.getStatus() == 0) {
					// Sent successfully
					if (isImageMessage)
						m_cResponseData = context
								.getString(R.string.photo_sent_success);
					else
						m_cResponseData = context
								.getString(R.string.message_sent_success);
				} else {
					m_cResponseData = sendMessageObj.getMessagetext();
				}

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

	/*
	 * private File savebitmap(String filename) {
	 * 
	 * String newFileName = java.util.UUID.randomUUID().toString(); String
	 * extStorageDirectory = context.getCacheDir().toString();//
	 * Environment.getExternalStorageDirectory().toString(); OutputStream
	 * outStream = null;
	 * 
	 * File newFile = new File(extStorageDirectory, newFileName + ".jpg");
	 * 
	 * try { BitmapFactory.Options options = new BitmapFactory.Options();
	 * 
	 * options.inJustDecodeBounds = false; options.inSampleSize = 4; Bitmap
	 * bitmap = BitmapFactory.decodeFile(filename, options);
	 * 
	 * outStream = new FileOutputStream(newFile);
	 * bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
	 * outStream.flush(); outStream.close(); } catch (Exception e) {
	 * e.printStackTrace(); }
	 * 
	 * return newFile;
	 * 
	 * }
	 */

	public void updateResult(String results, String statusMessage,
			int requestService) {
		System.out.println(" responseSTR r = " + responseSTR);
		if (results.equals(statusMessage)) {

			// Toast.makeText(context, "" + statusMessage,
			// Toast.LENGTH_SHORT).show();

			switch (requestService) {

			case ServiceKeys.SEND_MESSAGE_SERVICE:
				Utilities.showToast("" + statusMessage, context);
				Intent intentFood = new Intent(context, FoodTabActivity.class)
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				context.startActivity(intentFood);

				break;

			case ServiceKeys.MESSAGE_FROM_USER_SERVICE:
				// System.out.println(" 1212SenderID =" + senderId);
				// Intent intent = new Intent(context, ChatActivity.class);
				// intent.putExtra("coachID", senderId);
				// ((Activity) context).startActivityForResult(intent,
				// UPDATE_MESSAGESS);

				break;
			case ServiceKeys.MESSAGE_UNREAD_SERVICE:
				break;
			case ServiceKeys.MESSAGE_WITH_IMAGES_SERVICE:
				break;

			}
		} else {
			// Utilities.showErrorToast(results, objActivity);
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

//	public Bitmap getBitmapFromURL(String src) {
//		try {
//			URL url = new URL(src);
//			HttpURLConnection connection = (HttpURLConnection) url
//					.openConnection();
//			connection.setDoInput(true);
//			connection.connect();
//			InputStream input = connection.getInputStream();
//			Bitmap myBitmap = BitmapFactory.decodeStream(input);
//			return myBitmap;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

//	public Bitmap getCroppedBitmap(Bitmap bitmap) {
//		Bitmap output;
//		// if (bitmap != null) {
//		try {
//			output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
//					Config.ARGB_8888);
//			Canvas canvas = new Canvas(output);
//
//			final int color = 0xff424242;
//			final Paint paint = new Paint();
//			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
//					bitmap.getHeight());
//
//			paint.setAntiAlias(true);
//			canvas.drawARGB(0, 0, 0, 0);
//			paint.setColor(color);
//			// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//			canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
//					bitmap.getWidth() / 2, paint);
//			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//			canvas.drawBitmap(bitmap, rect, rect, paint);
//		} catch (NullPointerException e) {
//			output = TransferObject.getDefaultBitmapImage();
//		}
//		return output;
//	}
}