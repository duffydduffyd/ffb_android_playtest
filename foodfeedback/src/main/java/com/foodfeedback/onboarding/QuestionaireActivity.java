package com.foodfeedback.onboarding;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodfeedback.cachemanager.LoginDetailsCacheManager;
import com.foodfeedback.cachemanager.UserDetailsCacheManager;
import com.foodfeedback.networking.QuestionnaireNetworkingServices;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.utilities.QuestionListAdapter;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.Question;
import com.foodfeedback.valueobjects.QuestionsAnswers;
import com.foodfeedback.valueobjects.TransferObject;
import com.google.analytics.tracking.android.EasyTracker;
import com.localytics.android.LocalyticsSession;

public class QuestionaireActivity extends Activity {

	private TextView centreTitleTxt;
	private LinearLayout leftButton, rightButton;

	private LocalyticsSession localyticsSession;

	RelativeLayout relativeLayout_anyAgeRadio, relativeLayout_aroundMyAge;
	String password;
	Button btnSubmitOrContinue;
	EditText month, day, year;
	QuestionsAnswers questionsAnswersObj;

	int currentQuestionNumber = 0;
	long timestamp = 0;
	String gender = "";
	boolean anyAgeSelected = true;
	Typeface tfNormal,tfSpecial;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// setUpViewIds();
		// setupFonts();
		// addOnClickListeners();
		tfNormal = Typeface.createFromAsset(
				this.getAssets(),
				this.getResources().getString(
						R.string.font_standard));
		tfSpecial = Typeface.createFromAsset(
				this.getAssets(),
				this.getResources().getString(
						R.string.app_font_style_medium));
		questionsAnswersObj = TransferObject.getQuestionsAnswers();

		if (questionsAnswersObj.getData().getQuestions().size() > 0) {
			displayQuestion(0);
		}

		this.localyticsSession = new LocalyticsSession(
				this.getApplicationContext()); // Context used to access device
		// resources
		this.localyticsSession.open(); // open the session
		this.localyticsSession.tagScreen("All others");
		this.localyticsSession.upload(); // upload any data

	}

	private void displayQuestion(final int questionNumberToDisplay) {

		// I have been asked to display the question with poisition
		// questionNumberToDisplay
		final Question question = questionsAnswersObj.getData().getQuestions()
				.get(questionNumberToDisplay);
		if (currentQuestionNumber < questionsAnswersObj.getData()
				.getQuestions().size()) {
			if (question.getType() == 0 || question.getType() == 2) {

				// Inflate the simple question type or a gender question type
				setContentView(R.layout.question_answer);

				centreTitleTxt = (TextView) findViewById(R.id.center_txt);
				centreTitleTxt.setTypeface(tfSpecial);
				centreTitleTxt.setText(getResources().getString(
						R.string.find_coach));

				leftButton = (LinearLayout) findViewById(R.id.leftbutton);
				leftButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						finish();
					}
				});

				rightButton = (LinearLayout) findViewById(R.id.rightbutton);
				rightButton.setVisibility(View.GONE);

				// Set the continue button to disabled first
				btnSubmitOrContinue = (Button) findViewById(R.id.question_complete_continue);
				btnSubmitOrContinue.setEnabled(false);
				btnSubmitOrContinue
						.setBackgroundResource(R.drawable.unfocus_redimage);

				TextView questionTitle = (TextView) findViewById(R.id.question_heading);
				questionTitle.setText(question.getQuestion());
				questionTitle.setTypeface(tfSpecial);
				currentQuestionNumber = questionNumberToDisplay;

				final ListView answerListView = (ListView) findViewById(R.id.answer_options);

				answerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

				final QuestionListAdapter adapter = new QuestionListAdapter(
						this.getApplicationContext(), question) {
					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {

						View v = super.getView(position, convertView, parent);
						ImageView icon = (ImageView) v
								.findViewById(R.id.radioButton);

						if (answerListView.isItemChecked(position)) {
							// If something is clicked. then enable the button
							btnSubmitOrContinue.setEnabled(true);
							btnSubmitOrContinue
									.setBackgroundResource(R.drawable.redbar_button);

							icon.setImageDrawable(getResources().getDrawable(
									R.drawable.radiobutton_focus));
							questionsAnswersObj
									.getData()
									.getQuestions()
									.get(questionNumberToDisplay)
									.setSelectedAnswerId(
											question.getAnswers().get(position)
													.getId()
													+ "");

						} else {
							icon.setImageDrawable(getResources().getDrawable(
									R.drawable.radiobutton_unfocus));
						}
						return v;
					}
				};
				answerListView.setAdapter(adapter);
				answerListView
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View viewClicked, int position, long arg3) {
								answerListView.setSelection(position);
								ImageView icon = (ImageView) viewClicked
										.findViewById(R.id.radioButton);
								icon.setImageDrawable(getResources()
										.getDrawable(
												R.drawable.radiobutton_focus));
								adapter.notifyDataSetChanged();
							}
						});

			} else if (question.getType() == 1) {
				// For the age type of question.

				// inflate the view
				setContentView(R.layout.question_age);

				centreTitleTxt = (TextView) findViewById(R.id.center_txt);
				centreTitleTxt.setText(getResources().getString(
						R.string.find_coach));
				centreTitleTxt.setTypeface(tfSpecial);

				leftButton = (LinearLayout) findViewById(R.id.leftbutton);
				leftButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						finish();
					}
				});

				rightButton = (LinearLayout) findViewById(R.id.rightbutton);
				rightButton.setVisibility(View.GONE);

				TextView age_title = (TextView) findViewById(R.id.age_title);
				age_title.setText(question.getQuestion());
				age_title.setTypeface(tfNormal);

				month = (EditText) findViewById(R.id.month);
				day = (EditText) findViewById(R.id.day);
				year = (EditText) findViewById(R.id.year);

				month.addTextChangedListener(new WatcherListener());
				day.addTextChangedListener(new WatcherListener());
				year.addTextChangedListener(new WatcherListener());

				final ImageView anyAgeRadioButton, aroundMyAgeRadioButton;
				anyAgeRadioButton = (ImageView) findViewById(R.id.anyage_radiobutton);
				aroundMyAgeRadioButton = (ImageView) findViewById(R.id.aroundage_radiobutton);

				TextView anyAgeOptionText, aroundMyAgeOptionText;
				anyAgeOptionText = (TextView) findViewById(R.id.anyage_txt1);
				aroundMyAgeOptionText = (TextView) findViewById(R.id.aroundage_txt1);

				RelativeLayout anyAgeRelativeLayout, aroundMyAgeRelativeLayout;
				anyAgeRelativeLayout = (RelativeLayout) findViewById(R.id.anyage_layout);
				aroundMyAgeRelativeLayout = (RelativeLayout) findViewById(R.id.aroundmyage_layout);

				// Set the continue button to disabled first
				btnSubmitOrContinue = (Button) findViewById(R.id.question_complete_continue);
				btnSubmitOrContinue.setEnabled(false);
				btnSubmitOrContinue
						.setBackgroundResource(R.drawable.unfocus_redimage);

				anyAgeRelativeLayout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						anyAgeSelected = true;
						anyAgeRadioButton.setImageDrawable(getResources()
								.getDrawable(R.drawable.radiobutton_focus));
						aroundMyAgeRadioButton.setImageDrawable(getResources()
								.getDrawable(R.drawable.radiobutton_unfocus));
						btnSubmitOrContinue.setEnabled(true);
						btnSubmitOrContinue
								.setBackgroundResource(R.drawable.redbar_button);
						// Hardcoded
						questionsAnswersObj.getData().getQuestions()
								.get(questionNumberToDisplay)
								.setSelectedAnswerId("10");

					}
				});

				aroundMyAgeRelativeLayout
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								anyAgeSelected = false;
								aroundMyAgeRadioButton
										.setImageDrawable(getResources()
												.getDrawable(
														R.drawable.radiobutton_focus));
								anyAgeRadioButton
										.setImageDrawable(getResources()
												.getDrawable(
														R.drawable.radiobutton_unfocus));
								// btnSubmitOrContinue.setEnabled(false);
								// btnSubmitOrContinue
								// .setBackgroundResource(R.drawable.unfocus_redimage);

								btnSubmitOrContinue.setEnabled(true);
								btnSubmitOrContinue
										.setBackgroundResource(R.drawable.redbar_button);
								questionsAnswersObj.getData().getQuestions()
										.get(questionNumberToDisplay)
										.setSelectedAnswerId("11");
							}
						});

			}

			if (questionNumberToDisplay == questionsAnswersObj.getData()
					.getQuestions().size() - 1) {
				// Submit
				btnSubmitOrContinue.setText(getResources().getString(
						R.string.submit));
			} else {
				btnSubmitOrContinue.setText(getResources().getString(
						R.string.continue_tag));
			}
			btnSubmitOrContinue
					.setTypeface(tfSpecial);

			btnSubmitOrContinue.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// Set the answer information
					if (question.getType() == 1 && !anyAgeSelected) {
						// This is the age information - go ahead and set the
						// selected value and the selected timestamp
						int monthValue = 0, dayValue = 0, yearValue = 0;
						if (month != null && day != null && year != null) {
							if (!month.getText().equals(""))
								monthValue = Integer.parseInt(month.getText()
										.toString());
							if (!day.getText().equals(""))
								dayValue = Integer.parseInt(day.getText()
										.toString());
							if (!year.getText().equals(""))
								yearValue = Integer.parseInt(year.getText()
										.toString());

							Calendar cal = Calendar.getInstance();
							cal.set(yearValue, monthValue, dayValue);
							timestamp = cal.getTimeInMillis() / 1000;
						}

					} else if (question.getType() == 2) {

						// this means the gender page was shown
						if (questionsAnswersObj.getData().getQuestions()
								.get(questionNumberToDisplay)
								.getSelectedAnswerId().equals("12")) {
							// Any gender
							gender = "";
						} else if (questionsAnswersObj.getData().getQuestions()
								.get(questionNumberToDisplay)
								.getSelectedAnswerId().equals("13")) {
							// Any gender
							gender = "0";
						} else if (questionsAnswersObj.getData().getQuestions()
								.get(questionNumberToDisplay)
								.getSelectedAnswerId().equals("14")) {
							// Any gender
							gender = "1";
						}

					} else if (question.getType() == 0) {
						// Everything is already set
					}

					if (questionNumberToDisplay == questionsAnswersObj
							.getData().getQuestions().size() - 1) {
						// If Submit - then go ahead and send all data
						String answers = "";
						for (int i = 0; i < questionsAnswersObj.getData()
								.getQuestions().size(); i++) {
							if (i == questionsAnswersObj.getData()
									.getQuestions().size() - 1) {
								answers = answers
										+ questionsAnswersObj.getData()
												.getQuestions().get(i)
												.getSelectedAnswerId();
							} else {
								answers = answers
										+ questionsAnswersObj.getData()
												.getQuestions().get(i)
												.getSelectedAnswerId() + ",";
							}

						}

						try {
							checkConnectivityForPostAnswers(
									v.getContext(),
									Integer.toString(LoginDetailsCacheManager
											.getObject(
													Controller
															.getAppBackgroundContext())
											.getData().getUser().getId()),
									UserDetailsCacheManager.getObject(
											Controller
													.getAppBackgroundContext())
											.getPassword(), TransferObject
											.getQuestionsAnswers().getData()
											.getId(), answers, "" + timestamp,
									gender);
						} catch (Exception e) {
							e.printStackTrace();
						}

						Log.i("FoodFeedback", " All Answers " + answers);

						Log.i("FoodFeedback",
								"All answers are complete. Now try calling the service and submit the results"
										+ questionsAnswersObj.getData()
												.getQuestions()
												.get(questionNumberToDisplay)
												.getSelectedAnswerId());
					} else {
						// Else just move to next screen

						// if type is 1 - then go ahead and set the answer
						Log.i("FoodFeedback",
								"Work is still there. However the value to display is"
										+ questionsAnswersObj.getData()
												.getQuestions()
												.get(questionNumberToDisplay)
												.getSelectedAnswerId());
						displayQuestion(questionNumberToDisplay + 1);

					}

				}
			});

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 205 && resultCode == RESULT_OK) {

			finish();
		}
	}

	private void checkConnectivityForPostAnswers(Context ctx, String userId,
			String password, int completedQuestionnaire, String answerIds,
			String timestamp, String gender) {

		if (Utilities.isConnectingToInternet(ctx.getApplicationContext())) {

			if (timestamp.equals("0")) {
				timestamp = "";
			}
			QuestionnaireNetworkingServices.getInstance()
					.requestPostAnswersService(ctx, userId, password,
							completedQuestionnaire, answerIds, timestamp,
							gender, this, ServiceKeys.POST_ANSWERS_SERVICE);

		} else {
			Utilities.showErrorToast(
					getResources().getString(R.string.internet_connection),
					this);
		}

	}

	public class WatcherListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			validateAndActivateButton();
		}
	}

	private void validateAndActivateButton() {
		Calendar cal = Calendar.getInstance();
		int yearCurrent = cal.get(Calendar.YEAR);
		if (month.getText().toString().trim().length() != 0
				&& day.getText().toString().trim().length() != 0
				&& year.getText().toString().trim().length() != 0) {

			int monthInt = Integer.parseInt(month.getText().toString());
			int dayInt = Integer.parseInt(day.getText().toString());
			int yearInt = Integer.parseInt(year.getText().toString());
			if (monthInt <= 12 && dayInt <= 31 && yearInt <= (yearCurrent - 11)) {
				// set the continue button
				// .setBackgroundResource(R.drawable.redbar_button);
				// ageContinueButton.setEnabled(true);
				btnSubmitOrContinue.setEnabled(true);
				btnSubmitOrContinue
						.setBackgroundResource(R.drawable.redbar_button);
			} else {
				// ageContinueButton
				// .setBackgroundResource(R.drawable.unfocus_redimage);
				// ageContinueButton.setEnabled(false);
				btnSubmitOrContinue.setEnabled(false);
				btnSubmitOrContinue
						.setBackgroundResource(R.drawable.unfocus_redimage);
			}
		} else {
			// ageContinueButton
			// .setBackgroundResource(R.drawable.unfocus_redimage);
			// ageContinueButton.setEnabled(false);
			btnSubmitOrContinue.setEnabled(false);
			btnSubmitOrContinue
					.setBackgroundResource(R.drawable.unfocus_redimage);
		}
	}

	@Override
	public void onBackPressed() {

		System.out.println("onBackPressed");
		finish();
	}

	public void onResume() {
		super.onResume();
		this.localyticsSession.open();
	}

	public void onPause() {
		this.localyticsSession.close();
		this.localyticsSession.upload();
		super.onPause();
	}

	@Override
	public void onStart() {
		super.onStart();
		System.out.println("google Analytics Start");

		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		System.out.println("google Analytics Stop");

		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}
}
