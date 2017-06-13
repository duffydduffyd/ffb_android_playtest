package com.foodfeedback.onboarding;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.foodfeedback.networking.OnboardingNetworkingServices;
import com.foodfeedback.networking.ServiceKeys;
import com.foodfeedback.utilities.Utilities;
import com.foodfeedback.valueobjects.TransferObject;

public class LoginActivityListener implements OnClickListener {

	Context context;

	EditText emailTxt, passwordTxt;

	String username, password;

	private Activity activityContext;

	AlertDialog alertDialog;

	public LoginActivityListener(LoginActivity loginActivity) {

		this.activityContext = loginActivity;
	}

	/**
	 * This constructor is used to pass reference of login fields to the
	 * listener.
	 */
	public LoginActivityListener(EditText emailEditText,
			EditText passwordEditText, Activity ctxActivity) {

		this.emailTxt = emailEditText;
		this.passwordTxt = passwordEditText;
		this.activityContext = ctxActivity;
	}

	@Override
	public void onClick(View v) {

		int ViewId = v.getId();

		switch (ViewId) {

		case R.id.createaccountButton:

			v.getContext().startActivity(
					new Intent(v.getContext(), RegisterActivity.class));

			break;

		case R.id.forget_password:

			context = v.getContext();
			showForgetPasswordDialog();

			break;

		case R.id.loginButton:
			TransferObject.setLoginPassword(passwordTxt.getText().toString());
			checkConnectivityForLoginService(v.getContext(), emailTxt.getText()
					.toString(), passwordTxt.getText().toString());

			break;
		}
	}

	/**
	 * This method is used to show Alert Dialog with custom message.
	 */
	public void showAlert(String messageTxt, Context ctx) {

		AlertDialog dialog = new AlertDialog.Builder(ctx).setTitle("Error")
				.setMessage(messageTxt).setNegativeButton("Cancel", null)
				.create();
		dialog.show();
	}

	/**
	 * This method is used to check internet connection. if connection is
	 * available API call for login account.
	 */
	private void checkConnectivityForLoginService(Context ctx, String emailTxt,
			String passwordTxt) {

		this.context = ctx;

		if (Utilities.isConnectingToInternet(context.getApplicationContext())) {

			new OnboardingNetworkingServices(ctx)
					.requestLoginAuthentication(ctx, emailTxt, passwordTxt,
							activityContext, ServiceKeys.LOGIN_REQUEST_SERVICE);

		} else {
			Utilities.showErrorToast(
					context.getResources().getString(
							R.string.internet_connection), activityContext);
		}

	}

	/**
	 * This method is used to show Dialog that receives the e-mail of the user
	 * for password reset.
	 */
	private void showForgetPasswordDialog() {

		LayoutInflater li = LayoutInflater.from(context);
		View promptsView = li.inflate(R.layout.forgotpassword_dialog, null);

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		alertDialogBuilder.setView(promptsView);

		final EditText userInputEmail = (EditText) promptsView
				.findViewById(R.id.editText1dialog);

		
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Send",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								checkConnectivityForResetPassword(context,
										activityContext, userInputEmail
												.getText().toString().trim());

							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		alertDialog = alertDialogBuilder.create();
		alertDialog.setCancelable(false);
		alertDialog.show();
	}

	public void checkConnectivityForResetPassword(Context ctx,
			Activity activityContext, String email) {

		String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

		if (email.toString().trim().matches(emailPattern)
				&& email.toString().trim().length() != 0) {

			if (Utilities.isConnectingToInternet(context
					.getApplicationContext())) {

				new OnboardingNetworkingServices(context)
						.requestResetPasswordService(context, activityContext,
								email.toString().trim(),
								ServiceKeys.RESET_PASSWORD_SERVICE);
			} else {
				Utilities.showErrorToast(
						ctx.getResources().getString(
								R.string.internet_connection), activityContext);
			}
		} else {
			Utilities.showErrorToast(
					ctx.getResources().getString(R.string.an_email_must),
					activityContext);
		}

	}
}