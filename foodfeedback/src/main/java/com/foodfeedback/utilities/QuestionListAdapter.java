package com.foodfeedback.utilities;


import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foodfeedback.onboarding.R;
import com.foodfeedback.valueobjects.Answer;
import com.foodfeedback.valueobjects.Question;

public class QuestionListAdapter extends BaseAdapter {
	private Context mContext;
	private Question questionObject;
	private TextView answerOption, answerDetail;
	private ImageView radioButton;
Typeface tfNormal, tfSpecial;
	public QuestionListAdapter(Context c, Question questionObject) {
		mContext = c;
		this.questionObject = questionObject;
	}

	public int getCount() {
		return questionObject.getAnswers().size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

		convertView = inflater.inflate(R.layout.type_zero_item, null);
		final Answer answerItem = questionObject.getAnswers().get(position);

		tfNormal = Typeface.createFromAsset(
				mContext.getAssets(),
				mContext.getResources().getString(
						R.string.font_standard));
		tfSpecial = Typeface.createFromAsset(
				mContext.getAssets(),
				mContext.getResources().getString(
						R.string.app_font_style_medium));
		
		answerOption = (TextView) convertView.findViewById(R.id.answer);
		answerOption.setTypeface(tfSpecial);
		answerDetail = (TextView) convertView.findViewById(R.id.answer_detail);
		answerDetail.setTypeface(tfNormal);
		radioButton = (ImageView) convertView.findViewById(R.id.radioButton);

		if (answerItem != null) {
			answerOption.setText(answerItem.getAnswer());
			if (answerItem.getAnswer_detail().equals("")) {
				answerDetail.setVisibility(View.GONE);
			} else {
				answerDetail.setVisibility(View.VISIBLE);
				answerDetail.setText(answerItem.getAnswer_detail());
			}
		}

		return convertView;
	}
}
