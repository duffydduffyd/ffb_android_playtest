package com.foodfeedback.onboarding;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.foodfeedback.valueobjects.TransferObject;

public class RegisterActivityListener implements OnClickListener {

	EditText firstNameTxt, lastNameTxt, emailTxt, choosePasswordTxt;

	RegisterActivityListener() {

	}

	public RegisterActivityListener(EditText firstNameEditText,
			EditText lastNameEditText, EditText emailEditText,
			EditText choosePasswordEditText, Activity ctxActivity) {

		this.firstNameTxt = firstNameEditText;
		this.lastNameTxt = lastNameEditText;
		this.choosePasswordTxt = choosePasswordEditText;
		this.emailTxt = emailEditText;
	}

	@Override
	public void onClick(View v) {
		int ViewId = v.getId();

		switch (ViewId) {

		case R.id.leftbutton:

			v.getContext().startActivity(
					new Intent(v.getContext(), LoginActivity.class)
							.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

			break;

		case R.id.register_btn:

			TransferObject.setLoginPassword(choosePasswordTxt.getText()
					.toString());
			TransferObject.setCreateAccountFirstName(firstNameTxt.getText()
					.toString());
			TransferObject.setCreateAccountLastName(lastNameTxt.getText()
					.toString());
			TransferObject.setCreateAccountEmail(emailTxt.getText().toString());

			Intent intentObj;
			intentObj = new Intent(v.getContext(), AcceptTermsActivity.class);
			v.getContext().startActivity(intentObj);

			break;
		}
	}
}
