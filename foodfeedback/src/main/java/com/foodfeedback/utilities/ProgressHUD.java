package com.foodfeedback.utilities;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodfeedback.onboarding.R;

public class ProgressHUD extends Dialog {
	public ProgressHUD(Context context) {
		super(context);
	}

	public ProgressHUD(Context context, int theme) {
		super(context, theme);
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
		AnimationDrawable spinner = (AnimationDrawable) imageView
				.getBackground();
		spinner.start();
	}

	public void setMessage(CharSequence message) {
		if (message != null && message.length() > 0) {
			findViewById(R.id.message).setVisibility(View.VISIBLE);
			TextView txt = (TextView) findViewById(R.id.message);
			txt.setText(message);
			txt.invalidate();
		}
	}

	public static ProgressHUD show(Context context, CharSequence message,
			boolean indeterminate, boolean cancelable) {
		ProgressHUD dialog = new ProgressHUD(context, R.style.ProgressHUD);
		dialog.setTitle("");
		dialog.setContentView(R.layout.progress_hud);
		dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);

		if (message == null || message.length() == 0) {
			dialog.findViewById(R.id.message).setVisibility(View.GONE);
		} else {
			TextView txt = (TextView) dialog.findViewById(R.id.message);
			txt.setText(message);
		}
		dialog.setCancelable(cancelable);
		// dialog.setOnCancelListener(cancelListener);
		dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.dimAmount = 0.2f;
		dialog.getWindow().setAttributes(lp);
		// dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		dialog.show();
		return dialog;
	}

	public static ProgressHUD showMessageChat(Context context, CharSequence message, boolean indeterminate, boolean cancelable) {
		ProgressHUD dialog =null;
		try{
			dialog = new ProgressHUD(context,R.style.ProgressHUD);
			dialog.setTitle("");
			dialog.setContentView(R.layout.progress_hud_chat);
		
			if(message == null || message.length() == 0) {
				dialog.findViewById(R.id.message).setVisibility(View.GONE);			
			} else {
				TextView txt = (TextView)dialog.findViewById(R.id.message);
				txt.setText(message);
			}
			dialog.setCancelable(cancelable);
			dialog.getWindow().getAttributes().gravity=Gravity.CENTER;
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialog.show();
			
		}catch(Exception ex){
			
		}
			return dialog;
	}
}
