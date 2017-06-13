package com.foodfeedback.networking;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.foodfeedback.cachemanager.ChatStatusNumberCacheManager;
import com.foodfeedback.cachemanager.CoachStatusNumberCacheManager;
import com.foodfeedback.cachemanager.InteractedWithCacheManager;
import com.foodfeedback.cachemanager.InvitationsCacheManager;
import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.coaches.CoachDetailActivity;
import com.foodfeedback.onboarding.CompleteProfile;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.FoodTabActivity;
import com.foodfeedback.onboarding.QuestionaireActivity;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.AccountOperations;
import com.foodfeedback.valueobjects.ChatNumberUpdate;
import com.foodfeedback.valueobjects.CoachStatusNumber;
import com.foodfeedback.valueobjects.InteractedWith;
import com.foodfeedback.valueobjects.Invitations;
import com.foodfeedback.valueobjects.StatusUpdate;
import com.foodfeedback.valueobjects.UserDetails;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class OnboardingNetworkingServices implements Serializable {

	private static final long serialVersionUID = 1L;

	public OnboardingNetworkingServices(Context ctx) {
		this.context = ctx;
	}

	AccountOperations accountObject;
	StatusUpdate statusUpdates;
	UserDetails userDetailsObj;
	// List<Invitations> studentsList = new ArrayList<Invitations>();
	private String responseSTR;
	private String m_cResponseData;
	private String userIdStr, username, password, firstNameStr, lastNameStr,
			emailStr, choosePasswordStr, sendEmail, bioStr, privateCoachStr,
			coachStr, notificationRemainderWeekStr, profileImageStr;
	private Context context;
	private Activity objActivity;
	private int userId;
	private String newPassword;
	private String coachId;
	private String invitationId;
	private String statusStr;
	private String pendingStatus;
	private String declineStatus;
	private String declineRecentStatus;
	private String acceptedStatus;
	private String token;

	public void requestLoginAuthentication(Context ctx, String emailTxt,
			String passwordTxt, Activity getCtxActivity, int serviceCall) {

		this.username = emailTxt;
		this.password = passwordTxt;
		this.context = ctx;
		this.objActivity = getCtxActivity;

		asyncTaskStartUp(serviceCall);
	}

	public void requestCreateAccountService(Context ctx, String firstNameTxt,
			String lastNameTxt, String emailTxt, String choosePasswordTxt,
			Activity activityContext, int serviceCall) {

		this.context = ctx;
		this.firstNameStr = firstNameTxt;
		this.lastNameStr = lastNameTxt;
		this.emailStr = emailTxt;
		this.choosePasswordStr = choosePasswordTxt;
		this.objActivity = activityContext;

		asyncTaskStartUp(serviceCall);
	}

	public void requestResetPasswordService(Context ctx,
			Activity activityContext, String sendMail, int serviceCall) {

		this.context = ctx;
		this.objActivity = activityContext;
		this.sendEmail = sendMail;

		asyncTaskStartUp(serviceCall);
	}

	public void requestUpdateAccountService(Context ctx, int userID,
			String password, String firstName, String lastName, String email,
			String bio, String coach, String privateCoach,
			String notificationRemainderWeek, String profileImage,
			Activity activityReference, int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userId = userID;
		this.password = password;
		this.firstNameStr = firstName;
		this.lastNameStr = lastName;
		this.emailStr = email;
		this.bioStr = bio;
		this.privateCoachStr = privateCoach;
		this.coachStr = coach;
		this.notificationRemainderWeekStr = notificationRemainderWeek;
		this.profileImageStr = profileImage;

		asyncTaskStartUp(serviceCall);
	}

	public void requestChangePasswordService(Context ctx, int userId,
			String oldPassword, String newPassword, Activity activityReference,
			int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userId = userId;
		this.password = oldPassword;
		this.newPassword = newPassword;

		asyncTaskStartUp(serviceCall);
	}

	public void requestAddCoachService(Context ctx, String userId,
			String password, String coachId, Activity activityReference,
			int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userIdStr = userId;
		this.password = password;
		this.coachId = coachId;

		asyncTaskStartUp(serviceCall);
	}

	public void requestRemoveCoachService(Context ctx, String userId,
			String password, String coachId, Activity activityReference,
			int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userIdStr = userId;
		this.password = password;
		this.coachId = coachId;

		asyncTaskStartUp(serviceCall);
	}

	public void requestInviteCoachService(Context ctx, String userId,
			String password, String coachId, Activity activityReference,
			int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userIdStr = userId;
		this.password = password;
		this.coachId = coachId;

		asyncTaskStartUp(serviceCall);
	}

	public void requestInvitationResponseService(Context ctx, String userId,
			String password, String invitationId, String status,
			Activity activityReference, int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userIdStr = userId;
		this.password = password;
		this.invitationId = invitationId;
		this.statusStr = status;

		asyncTaskStartUp(serviceCall);
	}

	public void requestInvitationsService(Context ctx, String userId,
			String password, Activity activityReference, int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userIdStr = userId;
		this.password = password;

		asyncTaskStartUp(serviceCall);
	}

	public void requestSendInvitationsService(Context ctx, String userId,
			String password, String pendingStatus, String declineStatus,
			String declineRecentStatus, String acceptedStatus,
			Activity activityReference, int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userIdStr = userId;
		this.password = password;
		this.pendingStatus = pendingStatus;
		this.declineStatus = declineStatus;
		this.declineRecentStatus = declineRecentStatus;
		this.acceptedStatus = acceptedStatus;

		asyncTaskStartUp(serviceCall);
	}

	public void requestInteractedWithService(Context ctx, String userId,
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

				case ServiceKeys.LOGIN_REQUEST_SERVICE:
					result = loginHttpPostRequest();
					break;
				case ServiceKeys.CREATE_ACCOUNT_SERVICE:
					result = createAccountHttpPostRequest();
					break;
				case ServiceKeys.RESET_PASSWORD_SERVICE:
					result = resetPasswordHttpPostRequest();
					break;
				case ServiceKeys.ACCOUNT_UPDATE_SERVICE:
					result = accountUpdateHttpPostRequest();
					break;
				case ServiceKeys.CHANGE_PASSWORD_SERVICE:
					result = changePasswordHttpPostRequest();
					break;
				case ServiceKeys.ADD_COACH_SERVICE:
					result = addCoachHttpPostRequest();
					break;
				case ServiceKeys.REMOVE_COACH_SERVICE:
					result = removeCoachHttpPostRequest();
					break;
				case ServiceKeys.INVITE_COACH_SERVICE:
					result = inviteCoachHttpPostRequest();
					break;
				case ServiceKeys.INVITATION_RESPONSE_SERVICE:
					result = invitationResponseHttpPostRequest();
					break;
				case ServiceKeys.INVITATIONS_SERVICE:
					result = invitationsHttpGetRequest(userIdStr, password);
					break;
				case ServiceKeys.SEND_INVITATIONS_SERVICE:
					result = sendInvitationsHttpGetRequest();
					break;
				case ServiceKeys.INTERACTED_WITH_SERVICE:
					result = interactedWithHttpGetRequest(userIdStr, password);
					break;
				case ServiceKeys.ADD_IOS_DEVICE_TOKEN_SERVICE:
					result = addIosDeviceTokenHttpPostRequest();
					break;
				}
				return result;
			}
		};

		Renderer<String> loginResult = new Renderer<String>() {

			@Override
			public void render(String results) {

				switch (checkSerive) {

				case ServiceKeys.LOGIN_REQUEST_SERVICE:
					updateResult(results, ServiceKeys.LOGIN_SUCCESS,
							ServiceKeys.LOGIN_REQUEST_SERVICE);
					break;
				case ServiceKeys.CREATE_ACCOUNT_SERVICE:
					updateResult(results, ServiceKeys.CTREATEACCOUNT_SUCCESS,
							ServiceKeys.CREATE_ACCOUNT_SERVICE);
					break;
				case ServiceKeys.RESET_PASSWORD_SERVICE:
					updateResult(results, ServiceKeys.RESET_PASSWORD_SUCCESS,
							ServiceKeys.RESET_PASSWORD_SERVICE);
					break;
				case ServiceKeys.ACCOUNT_UPDATE_SERVICE:
					updateResult(results, ServiceKeys.ACCOUNT_UPDATE_SUCCESS,
							ServiceKeys.ACCOUNT_UPDATE_SERVICE);
					break;
				case ServiceKeys.CHANGE_PASSWORD_SERVICE:
					updateResult(results, ServiceKeys.CHANGE_PASSWORD_SUCCESS,
							ServiceKeys.CHANGE_PASSWORD_SERVICE);
					break;
				case ServiceKeys.ADD_COACH_SERVICE:
					updateResult(results, ServiceKeys.ADD_COACH_SUCCESS,
							ServiceKeys.ADD_COACH_SERVICE);
					break;
				case ServiceKeys.REMOVE_COACH_SERVICE:
					updateResult(results, ServiceKeys.REMOVE_COACH_SUCCESS,
							ServiceKeys.REMOVE_COACH_SERVICE);
					break;
				case ServiceKeys.INVITE_COACH_SERVICE:
					updateResult(results, ServiceKeys.INVITE_COACH_SUCCESS,
							ServiceKeys.INVITE_COACH_SERVICE);
					break;
				case ServiceKeys.INVITATION_RESPONSE_SERVICE:
					updateResult(results,
							ServiceKeys.INVITATION_RESPONSE_SUCCESS,
							ServiceKeys.INVITATION_RESPONSE_SERVICE);
					break;
				case ServiceKeys.INVITATIONS_SERVICE:
					updateResult(results, ServiceKeys.INVITATIONS_SUCCESS,
							ServiceKeys.INVITATIONS_SERVICE);
					break;
				case ServiceKeys.SEND_INVITATIONS_SERVICE:
					updateResult(results, ServiceKeys.SEND_INVITATIONS_SUCCESS,
							ServiceKeys.SEND_INVITATIONS_SERVICE);
					break;
				case ServiceKeys.INTERACTED_WITH_SERVICE:
					updateResult(results, ServiceKeys.INTERACTED_WITH_SUCCESS,
							ServiceKeys.INTERACTED_WITH_SERVICE);
					break;
				case ServiceKeys.ADD_IOS_DEVICE_TOKEN_SERVICE:
					updateResult(results,
							ServiceKeys.ADD_IOS_DEVICE_TOKEN_SUCCESS,
							ServiceKeys.ADD_IOS_DEVICE_TOKEN_SERVICE);
					break;
				}
			}
		};

		String messageForLoading = context.getResources().getString(
				R.string.loading_txt);

		AsyncTaskExecutor<Object, Object, String> loginExecutor = new AsyncTaskExecutor<Object, Object, String>(
				context, loginBackgroundTask, loginResult, messageForLoading);
		loginExecutor.execute();
	}

	public String addIosDeviceTokenHttpPostRequest() {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.ADD_GCM_DEVICE_TOKEN);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);

			nameValuePairs.add(new BasicNameValuePair("user_id", userIdStr));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("token", token));
			// nameValuePairs.add(new BasicNameValuePair("sandbox", sandBox));

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
				System.out.println(" getMessagetext() =  "
						+ statusUpdates.getMessagetext());
				System.out.println(" getStatus() =  "
						+ statusUpdates.getStatus());
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

	public String interactedWithHttpGetRequest(String userid, String password) {

		HttpClient httpclient = null;
		HttpGet httppost = null;

		try {
			// Add your data
			String userIdEncode = URLEncoder.encode(userid, "UTF-8");
			String passwordEncode = URLEncoder.encode(password, "UTF-8");

			String httpGetUrl = ServiceKeys.INTERACTED_WITH_URL + "user_id="
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
				InteractedWith interactedWith = gson
						.fromJson(new InputStreamReader(response.getEntity()
								.getContent()), InteractedWith.class);

				if (interactedWith.getStatus() == 0) {// &&
					// interactedWith.getData().size()
					// != 0) {
					// TransferObject.setInteractedWith(interactedWith);
					try {
						InteractedWithCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								interactedWith);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				m_cResponseData = interactedWith.getMessagetext();

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

	public String sendInvitationsHttpGetRequest() {

		HttpClient httpclient = null;
		HttpGet httppost = null;

		try {
			// Add your data
			String userIdEncode = URLEncoder.encode(userIdStr, "UTF-8");
			String passwordEncode = URLEncoder.encode(password, "UTF-8");
			String pendingStatusEncode = URLEncoder.encode(pendingStatus,
					"UTF-8");
			String declineStatusEncode = URLEncoder.encode(declineStatus,
					"UTF-8");
			String declineRecentStatusEncode = URLEncoder.encode(
					declineRecentStatus, "UTF-8");
			String acceptedStatusEncode = URLEncoder.encode(acceptedStatus,
					"UTF-8");

			String httpGetUrl = ServiceKeys.SEND_INVITATIONS_URL + "user_id="
					+ userIdEncode + "&password=" + passwordEncode
					+ "&include_pending=" + pendingStatusEncode
					+ "&include_declined=" + declineStatusEncode
					+ "&include_recently_declined=" + declineRecentStatusEncode
					+ "&include_accepted=" + acceptedStatusEncode;

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
				Invitations invitations = gson.fromJson(new InputStreamReader(
						response.getEntity().getContent()), Invitations.class);
				System.out
						.println(" size() =  " + invitations.getData().size());
				System.out.println(" getMessagetext() =  "
						+ invitations.getMessagetext());
				System.out
						.println(" getStatus() =  " + invitations.getStatus());
				System.out.println(" data getStatus() =  "
						+ invitations.getData().get(0).getStatus());
				System.out.println(" data getTimestamp() =  "
						+ invitations.getData().get(0).getTimestamp());
				System.out.println(" data getId() =  "
						+ invitations.getData().get(0).getId());
				System.out.println(" invitee getId() =  "
						+ invitations.getData().get(0).getInvitee().getId());
				System.out.println(" sender getId() =  "
						+ invitations.getData().get(0).getSender().getId());

				m_cResponseData = invitations.getMessagetext();

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

	public String invitationsHttpGetRequest(String userid, String password) {

		HttpClient httpclient = null;
		HttpGet httppost = null;

		try {
			// Add your data
			String userIdEncode = URLEncoder.encode(userid, "UTF-8");
			String passwordEncode = URLEncoder.encode(password, "UTF-8");
			String httpGetUrl = ServiceKeys.INVITATIONS_URL + "user_id="
					+ userIdEncode + "&password=" + passwordEncode;

			httpclient = new DefaultHttpClient();
			httppost = new HttpGet(httpGetUrl);

			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			httppost.setHeader("Accept-Language",Utilities.getLanguageHeaderToSet());

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			System.out.println(" getFirst = " + responseSTR);

			if (statusCode == 200) {

				Gson gson = new Gson();
				Invitations invitations = gson.fromJson(new InputStreamReader(
						response.getEntity().getContent()), Invitations.class);

				if (invitations.getData() != null
						& invitations.getStatus() == 0) {

					try {
						CoachStatusNumber statusNumber = new CoachStatusNumber();
						statusNumber.setCoachStatusNo(invitations.getData()
								.size());
						CoachStatusNumberCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								statusNumber);

						InvitationsCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								invitations);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				m_cResponseData = invitations.getMessagetext();

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

	public String invitationResponseHttpPostRequest() {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {
			StatusUpdate statusUpdates;
			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.INVITATION_RESPONSE_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("user_id", userIdStr));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("invitation_id",
					invitationId));
			nameValuePairs.add(new BasicNameValuePair("status", statusStr));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				Gson gson = new Gson();
				statusUpdates = gson.fromJson(new InputStreamReader(response
						.getEntity().getContent()), StatusUpdate.class);
				System.out.println(" getMessagetext() =  "
						+ statusUpdates.getMessagetext());
				System.out.println(" getStatus() =  "
						+ statusUpdates.getStatus());
				if (statusUpdates.getStatus() == 0) {

					try {
						Invitations invitationsObj = InvitationsCacheManager
								.getObject(Controller.getAppBackgroundContext());

						for (int i = 0; i < invitationsObj.getData().size(); i++) {
							if (invitationsObj.getData().get(i).getId() == (Integer
									.parseInt(invitationId))) {
								invitationsObj.getData().remove(i);
								InvitationsCacheManager.saveObject(
										Controller.getAppBackgroundContext(),
										null);
								InvitationsCacheManager.saveObject(
										Controller.getAppBackgroundContext(),
										invitationsObj);
							}
						}

						interactedWithHttpGetRequest(
								Integer.toString(LoginDetailsCacheManager
										.getObject(
												Controller
														.getAppBackgroundContext())
										.getData().getUser().getId()), password);

						/*
						 * CoachesNetworkingServices .getInstance()
						 * .fetchCoachesHttpGetRequest(
						 * Integer.toString(LoginDetailsCacheManager .getObject(
						 * Controller .getAppBackgroundContext())
						 * .getData().getUser().getId()), password, "1",
						 * context);
						 */
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				m_cResponseData = statusUpdates.getMessagetext();

			} else {
				checkingError(context, response);
			}
		} catch (ClientProtocolException e) {
			checkingException(context, e.getLocalizedMessage());
		} catch (IOException e) {
			checkingException(context, e.getLocalizedMessage());
		}
		// m_cResponseData = statusUpdates.getMessagetext();
		return m_cResponseData;
	}

	public String inviteCoachHttpPostRequest() {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.INVITE_COACH_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

			nameValuePairs.add(new BasicNameValuePair("user_id", userIdStr));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("coach_id", coachId));

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
				System.out.println(" getMessagetext() =  "
						+ statusUpdates.getMessagetext());
				System.out.println(" getStatus() =  "
						+ statusUpdates.getStatus());
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

	public String removeCoachHttpPostRequest() {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.REMOVE_COACH_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

			nameValuePairs.add(new BasicNameValuePair("user_id", userIdStr));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("coach_id", coachId));

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

				try {
					InteractedWith interactedWithObj = InteractedWithCacheManager
							.getObject(Controller.getAppBackgroundContext());

					for (int i = 0; i < interactedWithObj.getData().size(); i++) {
						if (interactedWithObj.getData().get(i).getId() == Integer
								.parseInt(coachId)) {
							interactedWithObj.getData().remove(i);
							InteractedWithCacheManager.saveObject(
									Controller.getAppBackgroundContext(), null);
							InteractedWithCacheManager.saveObject(
									Controller.getAppBackgroundContext(),
									interactedWithObj);
						}
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				/*
				 * CoachList coachListObj = CoachListCacheManager
				 * .getObject(Controller.getAppBackgroundContext());
				 * 
				 * for (int i = 0; i < coachListObj.getData().size(); i++) { if
				 * (coachListObj.getData().get(i).getId() == Integer
				 * .parseInt(coachId)) { coachListObj.getData().remove(i);
				 * CoachListCacheManager.saveObject(
				 * Controller.getAppBackgroundContext(), null);
				 * CoachListCacheManager.saveObject(
				 * Controller.getAppBackgroundContext(), coachListObj); } }
				 */

				System.out.println(" getMessagetext() =  "
						+ statusUpdates.getMessagetext());
				System.out.println(" getStatus() =  "
						+ statusUpdates.getStatus());
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

	public String addCoachHttpPostRequest() {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.ADD_COACH_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

			nameValuePairs.add(new BasicNameValuePair("user_id", userIdStr));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("coach_id", coachId));

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
				System.out.println(" getMessagetext() =  "
						+ statusUpdates.getMessagetext());
				System.out.println(" getStatus() =  "
						+ statusUpdates.getStatus());
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

	public String changePasswordHttpPostRequest() {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.CHANGE_PASSWORD_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

			nameValuePairs.add(new BasicNameValuePair("user_id", Integer
					.toString(userId)));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("new_password",
					newPassword));

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
				System.out.println(" getMessagetext() =  "
						+ statusUpdates.getMessagetext());
				System.out.println(" getStatus() =  "
						+ statusUpdates.getStatus());
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

	public String accountUpdateHttpPostRequest() {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter("http.socket.timeout",
					new Integer(Integer.valueOf(25000)));
			httppost = new HttpPost(ServiceKeys.ACCOUNT_UPDATE_URL);
			// Add your data
			MultipartEntity entity = new MultipartEntity();

			entity.addPart("user_id", new StringBody(Integer.toString(userId)));
			entity.addPart("password", new StringBody(password));
			entity.addPart("first_name", new StringBody(firstNameStr));
			entity.addPart("last_name", new StringBody(lastNameStr));
			entity.addPart("email", new StringBody(emailStr));
			entity.addPart("bio", new StringBody(bioStr));
			entity.addPart("coach", new StringBody(coachStr));
			entity.addPart("private_coach", new StringBody(privateCoachStr));
			entity.addPart("notification_reminder_week", new StringBody(
					notificationRemainderWeekStr));

			System.out.println("file path " + profileImageStr);

			if (profileImageStr != null) {
				File file = savebitmap(profileImageStr);

				entity.addPart("profile_image", new FileBody(file));
			} else {

			}

			httppost.setEntity(entity);

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				try {
					accountObject = null;
					Gson gson = new Gson();
					accountObject = gson.fromJson(new InputStreamReader(
							response.getEntity().getContent()),
							AccountOperations.class);

					LoginDetailsCacheManager
							.saveObject(Controller.getAppBackgroundContext(),
									accountObject);

					if (accountObject.getData() != null)
						QuestionnaireNetworkingServices
								.getInstance()
								.QuestionnaireHttpGetRequest(
										accountObject.getData().getUser()
												.getId(),
										UserDetailsCacheManager
												.getObject(
														Controller
																.getAppBackgroundContext())
												.getPassword());
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
				} catch (JsonIOException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				checkingError(context, response);
			}
		} catch (ClientProtocolException e) {
			checkingException(context, e.getLocalizedMessage());
		} catch (IOException e) {
			checkingException(context, e.getLocalizedMessage());
		}
		System.out.println(" Testing Concurrencty 222 ");
		m_cResponseData = accountObject.getMessagetext();
		return m_cResponseData;
	}

	private File savebitmap(String filename) {

		String newFileName = java.util.UUID.randomUUID().toString();
		String extStorageDirectory = context.getCacheDir().toString();// Environment.getExternalStorageDirectory().toString();
		OutputStream outStream = null;

		File newFile = new File(extStorageDirectory, newFileName + ".jpg");

		try {
			BitmapFactory.Options options = new BitmapFactory.Options();

			options.inJustDecodeBounds = false;
			options.inSampleSize = 4;
			Bitmap bitmap = BitmapFactory.decodeFile(filename, options);

			outStream = new FileOutputStream(newFile);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return newFile;

	}

	public String loginHttpPostRequest() {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.LOGIN_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				accountObject = null;
				Gson gson = new Gson();
				accountObject = gson.fromJson(new InputStreamReader(response
						.getEntity().getContent()), AccountOperations.class);

				if (accountObject.getStatus() == 0) {

					try {
						LoginDetailsCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								accountObject);

						ChatNumberUpdate chatNumberUpdate = new ChatNumberUpdate();
						chatNumberUpdate.setNumberUpdate(0);
						ChatStatusNumberCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								chatNumberUpdate, accountObject.getData()
										.getUser().getId()
										+ "");
						// chatNumberUpdate = null;

						userDetailsObj = null;
						userDetailsObj = new UserDetails();
						userDetailsObj.setPassword(password);
						userDetailsObj.setUserID(Integer.toString(accountObject
								.getData().getUser().getId()));
						if (UserDetailsCacheManager.getObject(Controller
								.getAppBackgroundContext()) != null) {
							userDetailsObj
									.setCameraRoll(UserDetailsCacheManager
											.getObject(
													Controller
															.getAppBackgroundContext())
											.getCameraRoll());
						} else
							userDetailsObj.setCameraRoll(0);

						if (UserDetailsCacheManager.getObject(Controller
								.getAppBackgroundContext()) != null) {
							userDetailsObj
									.setCheckOutLogin(UserDetailsCacheManager
											.getObject(
													Controller
															.getAppBackgroundContext())
											.getCheckOutLogin());
						}

						UserDetailsCacheManager.saveObject(
								Controller.getAppBackgroundContext(), null);
						UserDetailsCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								userDetailsObj);

						new MessagesNetworkingServices(context)
								.messageUnreadHttpGetRequest(
										context,
										Integer.toString(accountObject
												.getData().getUser().getId()),
										password, "200", "");
						interactedWithHttpGetRequest(
								Integer.toString(accountObject.getData()
										.getUser().getId()), password);
						TrackerNetworkingServices.getInstance()
								.fetchWeightsHttpGetRequest(
										Integer.toString(accountObject
												.getData().getUser().getId()),
										password);
						GalleryNetworkingServices.getInstance()
								.fetchGalleryHttpGetRequest(
										Integer.toString(accountObject
												.getData().getUser().getId()),
										password, "1");

						// CoachesNetworkingServices
						// .getInstance()
						// .fetchCoachesHttpGetRequest(
						// Integer.toString(accountObject.getData()
						// .getUser().getId()), password, "1",
						// context);

						invitationsHttpGetRequest(
								Integer.toString(accountObject.getData()
										.getUser().getId()), password);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				m_cResponseData = accountObject.getMessagetext();
			} else {

				checkingError(context, response);
			}
		} catch (ClientProtocolException e) {
			System.out.println(" Testing 11 ");
			checkingException(context, e.getLocalizedMessage());
		} catch (IOException e) {
			System.out.println(" Testing 22 ");
			checkingException(context, e.getLocalizedMessage());
		}
		return m_cResponseData;
	}

	public String createAccountHttpPostRequest() {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.CREATE_ACCOUNT_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);

			nameValuePairs.add(new BasicNameValuePair("first_name",
					firstNameStr));
			nameValuePairs
					.add(new BasicNameValuePair("last_name", lastNameStr));
			nameValuePairs.add(new BasicNameValuePair("email", emailStr));
			nameValuePairs.add(new BasicNameValuePair("password",
					choosePasswordStr));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				try {
					accountObject = null;
					Gson gson = new Gson();
					accountObject = gson.fromJson(new InputStreamReader(
							response.getEntity().getContent()),
							AccountOperations.class);

					if (accountObject.getStatus() == 0) {

						CoachStatusNumber statusNumber = new CoachStatusNumber();
						statusNumber.setCoachStatusNo(0);
						CoachStatusNumberCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								statusNumber);
						// statusNumber = null;

						ChatNumberUpdate chatNumberUpdate = new ChatNumberUpdate();
						chatNumberUpdate.setNumberUpdate(0);
						ChatStatusNumberCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								chatNumberUpdate, accountObject.getData()
										.getUser().getId()
										+ "");
						// chatNumberUpdate = null;

						LoginDetailsCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								accountObject);
						// loginSuccess();
						userDetailsObj = null;
						userDetailsObj = new UserDetails();
						userDetailsObj.setPassword(choosePasswordStr);
						userDetailsObj.setUserID(Integer.toString(accountObject
								.getData().getUser().getId()));
						if (UserDetailsCacheManager.getObject(Controller
								.getAppBackgroundContext()) != null) {
							userDetailsObj
									.setCameraRoll(UserDetailsCacheManager
											.getObject(
													Controller
															.getAppBackgroundContext())
											.getCameraRoll());
						} else
							userDetailsObj.setCameraRoll(0);

						if (UserDetailsCacheManager.getObject(Controller
								.getAppBackgroundContext()) != null) {
							userDetailsObj
									.setCheckOutLogin(UserDetailsCacheManager
											.getObject(
													Controller
															.getAppBackgroundContext())
											.getCheckOutLogin());
						}

						UserDetailsCacheManager.saveObject(
								Controller.getAppBackgroundContext(), null);
						UserDetailsCacheManager.saveObject(
								Controller.getAppBackgroundContext(),
								userDetailsObj);

						if (accountObject.getData() != null) {
							interactedWithHttpGetRequest(
									Integer.toString(accountObject.getData()
											.getUser().getId()),
									choosePasswordStr);
							// CoachesNetworkingServices.getInstance()
							// .fetchCoachesHttpGetRequest(
							// Integer.toString(accountObject
							// .getData().getUser().getId()),
							// choosePasswordStr, "1", context);
							new MessagesNetworkingServices(context)
									.messageUnreadHttpGetRequest(
											context,
											Integer.toString(accountObject
													.getData().getUser()
													.getId()),
											choosePasswordStr, "", "");
						}

						/*
						 * if (accountObject.getData() != null) { if
						 * (accountObject.getData
						 * ().getUser().getProfileImageThumbUrl
						 * ().toString().length() != 0)
						 * TransferObject.setLoginUserThumbImage
						 * (getBitmapFromURL(accountObject
						 * .getData().getUser().getProfileImageThumbUrl())); }
						 */
					}
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
				} catch (JsonIOException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				checkingError(context, response);
			}
		} catch (ClientProtocolException e) {
			checkingException(context, e.getLocalizedMessage());
		} catch (IOException e) {
			checkingException(context, e.getLocalizedMessage());
		}

		m_cResponseData = accountObject.getMessagetext();
		return m_cResponseData;
	}

	public String resetPasswordHttpPostRequest() {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.RESET_PASSWORD_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("email", sendEmail));

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
				System.out.println(" getMessagetext() =  "
						+ statusUpdates.getMessagetext());
				System.out.println(" getStatus() =  "
						+ statusUpdates.getStatus());
				m_cResponseData = statusUpdates.getMessagetext();

			} else {

				checkingError(context, response);
			}
		} catch (ClientProtocolException e) {
			System.out.println(" Testing 11 ");
			checkingException(context, e.getLocalizedMessage());
		} catch (IOException e) {
			System.out.println(" Testing 22 ");
			checkingException(context, e.getLocalizedMessage());
		}

		return m_cResponseData;
	}

	public void updateResult(String results, String statusMessage,
			int requestService) {
		System.out.println(" responseSTR r = " + responseSTR);
		if (results.equals(statusMessage)) {

			// Toast.makeText(context, "" + statusMessage,
			// Toast.LENGTH_SHORT).show();

			switch (requestService) {

			case ServiceKeys.LOGIN_REQUEST_SERVICE:

				// Toast.makeText(context, "" + statusMessage,
				// Toast.LENGTH_SHORT).show();
				// start login success activity homepage.
				Intent intentFood = new Intent(context, FoodTabActivity.class)
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intentFood);
				break;

			case ServiceKeys.CREATE_ACCOUNT_SERVICE:

				// Toast.makeText(context, "" + statusMessage,
				// Toast.LENGTH_SHORT).show();
				Intent intentObj;
				intentObj = new Intent(context, CompleteProfile.class);
				context.startActivity(intentObj);

				break;

			case ServiceKeys.RESET_PASSWORD_SERVICE:

				AlertDialog.Builder builder = new AlertDialog.Builder(
						objActivity);
				builder.setTitle(context.getResources().getString(
						R.string.forpassword_success_title));

				builder.setMessage(
						context.getResources().getString(
								R.string.forpassword_success_message))
						.setCancelable(false)
						.setPositiveButton(
								context.getResources().getString(
										R.string.dialog_ok),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// do things
									}
								});
				AlertDialog alert = builder.create();
				alert.show();

				// Reset Password Link is send by serverside.
				break;
			case ServiceKeys.ACCOUNT_UPDATE_SERVICE:
				// Account Updated.

				switch (ServiceKeys.PROFILE_UPDATE) {

				case ServiceKeys.PROFILE_UPDATE_EDIT:
					// DO NOTHING
					// context.startActivity(new Intent(context,
					// MyFoodActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
					Utilities.showToast(
							context.getResources().getString(
									R.string.saved_success), context);

					objActivity.finish();
					break;

				case ServiceKeys.PROFILE_UPDATE_REGISTRATION:
					context.startActivity(new Intent(context,
							QuestionaireActivity.class));
					break;
				}

				break;
			case ServiceKeys.ADD_COACH_SERVICE:
				break;
			case ServiceKeys.INVITE_COACH_SERVICE:

				showInviteSuccessDialog();
				break;
			case ServiceKeys.REMOVE_COACH_SERVICE:

				Intent intent = new Intent();
				objActivity.setResult(Activity.RESULT_OK, intent);
				objActivity.finish();
				Utilities.showToast("" + statusMessage, context);

				break;
			case ServiceKeys.INVITATION_RESPONSE_SERVICE:
				// Toast.makeText(context, "" + statusMessage,
				// Toast.LENGTH_SHORT).show();

				try {
					CoachStatusNumber statusNumber, updateStatusNo;
					updateStatusNo = new CoachStatusNumber();
					statusNumber = CoachStatusNumberCacheManager
							.getObject(Controller.getAppBackgroundContext());

					updateStatusNo.setCoachStatusNo(statusNumber
							.getCoachStatusNo() - 1);
					CoachStatusNumberCacheManager.saveObject(
							(Controller.getAppBackgroundContext()), null);
					CoachStatusNumberCacheManager.saveObject(
							(Controller.getAppBackgroundContext()),
							updateStatusNo);
				} catch (Exception e) {
					e.printStackTrace();
				}

				Intent intent1 = new Intent();
				objActivity.setResult(Activity.RESULT_OK, intent1);
				objActivity.finish();
				// Intent intent1 = new Intent();
				// objActivity.setResult(Activity.RESULT_OK, intent1);
				// objActivity.finish();
				break;
			case ServiceKeys.INVITATIONS_SERVICE:
				// ParsingOnboardingJsonResponse.parseInvitationsResponse(responseSTR);

				break;
			case ServiceKeys.SEND_INVITATIONS_SERVICE:
				break;
			case ServiceKeys.INTERACTED_WITH_SERVICE:
				break;
			}
		} else {
			Utilities.showErrorToast(results, objActivity);
		}
	}

	private void showInviteSuccessDialog() {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		// alertDialogBuilder.setTitle("Your Title");

		// set dialog message
		alertDialogBuilder
				.setMessage(
						""
								+ CoachDetailActivity.coachName
								+ " "
								+ context.getResources().getString(
										R.string.invite_success_1)
								+ " "
								+ CoachDetailActivity.coachName
								+ " "
								+ context.getResources().getString(
										R.string.invite_success_2))
				.setCancelable(false)
				.setPositiveButton(
						context.getResources().getString(R.string.dialog_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								objActivity.finish();/*
													 * Intent intentFood = new
													 * Intent(objActivity,
													 * FoodTabActivity
													 * .class).setFlags(Intent
													 * .FLAG_ACTIVITY_CLEAR_TOP
													 * ); context
													 * .startActivity(
													 * intentFood);
													 */
							}
						});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
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
