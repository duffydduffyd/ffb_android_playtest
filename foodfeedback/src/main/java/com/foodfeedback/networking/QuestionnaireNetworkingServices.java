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
import android.content.Intent;

import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.coaches.CoachSearchListActivity;
import com.foodfeedback.onboarding.Controller;
import com.foodfeedback.onboarding.QuestionaireActivity;
import com.foodfeedback.onboarding.R;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.QuestionsAnswers;
import com.foodfeedback.valueobjects.TransferObject;
import com.google.gson.Gson;

public class QuestionnaireNetworkingServices implements Serializable {

	private static final long serialVersionUID = 1L;

	private QuestionnaireNetworkingServices() {
		// private constructor
	}

	private static class SingletonHolder {
		public static final QuestionnaireNetworkingServices INSTANCE = new QuestionnaireNetworkingServices();
	}

	public static QuestionnaireNetworkingServices getInstance() {
		return SingletonHolder.INSTANCE;
	}

	protected Object readResolve() {
		return getInstance();
	}

	private QuestionsAnswers questionAnswers;
	private String responseSTR;
	private String m_cResponseData;
	private String userIdStr, password;
	private Context context;
	private Activity objActivity;
	private int questionnaireId;
	private String answerIds;
	private String gender;
	private String birthday;
	private int userId;

	public void requestQuestionnaireService(Context ctx, int userId,
			String password, Activity activityReference, int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userId = userId;
		this.password = password;
		// this.questionnaireId = questionnaireId;

		asyncTaskStartUp(serviceCall);
	}

	public void requestPostAnswersService(Context ctx, String userId,
			String password, int questionnaireId, String answerIds,
			String timestamp, String gender, Activity activityReference,
			int serviceCall) {

		this.context = ctx;
		this.objActivity = activityReference;

		this.userIdStr = userId;
		this.password = password;
		this.questionnaireId = questionnaireId;
		this.answerIds = answerIds;
		this.gender = gender;
		this.birthday = timestamp;

		asyncTaskStartUp(serviceCall);
	}

	private void asyncTaskStartUp(int serviceCall) {

		final int checkSerive = serviceCall;

		BackgroundTask<Object, Object, String> loginBackgroundTask = new BackgroundTask<Object, Object, String>() {

			@Override
			public String execute(Object... params) {

				String result = null;

				switch (checkSerive) {

				case ServiceKeys.QUESTIONNAIRE_SERVICE:
					System.out.println(" Testing Concurrencty 444 ");
					result = QuestionnaireHttpGetRequest(userId, password);
					break;
				case ServiceKeys.POST_ANSWERS_SERVICE:
					result = postAnswersHttpPostRequest();
					break;

				}
				return result;
			}
		};

		Renderer<String> loginResult = new Renderer<String>() {

			@Override
			public void render(String results) {

				switch (checkSerive) {

				case ServiceKeys.QUESTIONNAIRE_SERVICE:
					updateResult(results, ServiceKeys.QUESTIONNAIRE_SUCCESS,
							ServiceKeys.QUESTIONNAIRE_SERVICE);
					break;
				case ServiceKeys.POST_ANSWERS_SERVICE:
					updateResult(results, ServiceKeys.POST_ANSWERS_SUCCESS,
							ServiceKeys.POST_ANSWERS_SERVICE);
					break;
				}
			}
		};

		String messageForLoading = context.getResources().getString(
				R.string.finding_coaches);
		
		switch (checkSerive) {
		
		case ServiceKeys.QUESTIONNAIRE_SERVICE:
			messageForLoading = context.getResources().getString(
					R.string.loading_txt);
			break;
		case ServiceKeys.POST_ANSWERS_SERVICE:
			messageForLoading = context.getResources().getString(
					R.string.finding_coaches);
			break;
		}
		
		AsyncTaskExecutor<Object, Object, String> loginExecutor = new AsyncTaskExecutor<Object, Object, String>(
				context, loginBackgroundTask, loginResult, messageForLoading);
		loginExecutor.execute();
	}

	public String QuestionnaireHttpGetRequest(int userId, String password) {

		HttpClient httpclient = null;
		HttpGet httppost = null;

		try {
			// Add your data
			String userIdEncode = URLEncoder.encode(Integer.toString(userId),
					"UTF-8");
			String passwordEncode = URLEncoder.encode(password, "UTF-8");
			// String questionnaireIdEncode = URLEncoder.encode(questionnaireId,
			// "UTF-8");

			String httpGetUrl = ServiceKeys.QUESTIONNAIRE_URL + "user_id="
					+ userIdEncode + "&password=" + passwordEncode;// +
			// "&questionnaire_id="

			httpclient = new DefaultHttpClient();
			httppost = new HttpGet(httpGetUrl);

			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			httppost.setHeader("Accept-Language",Utilities.getLanguageHeaderToSet());
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {
				questionAnswers = null;
				Gson gson = new Gson();
				questionAnswers = gson.fromJson(new InputStreamReader(response
						.getEntity().getContent()), QuestionsAnswers.class);
				TransferObject.setQuestionsAnswers(questionAnswers);
				m_cResponseData = questionAnswers.getMessagetext();

			} else {
				checkingError(context, response);
			}
		} catch (ClientProtocolException e) {
			checkingException(context, e.getLocalizedMessage());
		} catch (IOException e) {
			checkingException(context, e.getLocalizedMessage());
		}
		System.out.println(" Testing Concurrencty 555 ");
		return m_cResponseData;
	}

	public String postAnswersHttpPostRequest() {

		HttpClient httpclient = null;
		HttpPost httppost = null;

		try {

			httpclient = new DefaultHttpClient();
			httppost = new HttpPost(ServiceKeys.POST_ANSWERS_URL);
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("user_id", userIdStr));
			nameValuePairs.add(new BasicNameValuePair("password", password));

			nameValuePairs.add(new BasicNameValuePair("questionnaire_id",
					Integer.toString(questionnaireId)));
			nameValuePairs.add(new BasicNameValuePair("answer_ids", answerIds));
			nameValuePairs.add(new BasicNameValuePair("gender", gender));
			if (!birthday.equals(""))
				nameValuePairs.add(new BasicNameValuePair("birthday", String
						.valueOf(birthday)));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			httppost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {
				questionAnswers = null;
				Gson gson = new Gson();
				questionAnswers = gson.fromJson(new InputStreamReader(response
						.getEntity().getContent()), QuestionsAnswers.class);

				System.out.println(" getStatus()  =  "
						+ questionAnswers.getStatus());
				System.out.println(" getMessagetext() =  "
						+ questionAnswers.getMessagetext());

				try {
					CoachesNetworkingServices
							.getInstance()
							.suggestedCoachesHttpGetRequest(
									Integer.toString(LoginDetailsCacheManager
											.getObject(
													Controller
															.getAppBackgroundContext())
											.getData().getUser().getId()),
									UserDetailsCacheManager.getObject(
											Controller
													.getAppBackgroundContext())
											.getPassword());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// System.out.println(" getId() =  " +
				// questionAnswers.getData().getId());
				// System.out.println("size" +
				// questionAnswers.getData().getQuestions().size());
				// System.out.println(" getQuestion() =  " +
				// questionAnswers.getData().getQuestions().get(0).getQuestion());
				// System.out.println(" size() =  " +
				// questionAnswers.getData().getQuestions().get(0).getAnswers().size());
				// System.out.println(" getAnswer_detail()" +
				// questionAnswers.getData().getQuestions().get(0).getAnswers().get(0).getAnswer_detail());

				m_cResponseData = questionAnswers.getMessagetext();

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

			case ServiceKeys.QUESTIONNAIRE_SERVICE:
				System.out.println(" Testing Concurrencty 666 ");
				// Toast.makeText(context, "" + statusMessage,
				// Toast.LENGTH_SHORT).show();
				// TransferObject.setQuestionsAnswers(questionAnswers);
				context.startActivity(new Intent(context,
						QuestionaireActivity.class));

				break;
			case ServiceKeys.POST_ANSWERS_SERVICE:

				if (TransferObject.getSuggestedCoaches().getData()
						.getMatching_coaches().size() == 0
						&& TransferObject.getSuggestedCoaches().getData()
								.getPartially_matching_coaches().size() == 0) {
					// Utilities.showErrorToast(" NO Result ", objActivity);
					
					Utilities.showToast(context.getResources().getString(
							R.string.no_results), context);
					
				} else {
					// TransferObject.setSuggestedCoaches(suggestedCoaches);
					// context.startActivity(new Intent(context,
					// CoachSearchListActivity.class));
					objActivity.startActivityForResult(new Intent(context,
							CoachSearchListActivity.class), 205);
				}

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