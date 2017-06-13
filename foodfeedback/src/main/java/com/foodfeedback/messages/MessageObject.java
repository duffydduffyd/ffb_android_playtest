package com.foodfeedback.messages;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;

import com.foodfeedback.onboarding.R;

/**
 * Encapsulates information about a Chat entry
 */
public final class MessageObject {

	private String photoBgPath;
	private String profileBgPath;

	private String profilePath;
	private String textChat;
	private String timeChat;

	private String messageImageID;

	private int checkImageStatus;
	private int checkInOutStatus;
	private String messageID;

	public MessageObject(String photoBg, String profilebg, String profilePhoto,
			final String textChat, String messageImageID, String chatTime,
			int checkImageStatus, int checkInOutStatus, String messageID) {

		this.photoBgPath = photoBg;
		this.profileBgPath = profilebg;
		this.profilePath = profilePhoto;
		this.textChat = textChat;
		this.messageImageID = messageImageID;
		this.timeChat = chatTime;
		this.checkImageStatus = checkImageStatus;
		this.checkInOutStatus = checkInOutStatus;
		this.messageID = messageID;

	}

	public int getCheckInOutStatus() {
		return checkInOutStatus;
	}

	public void setCheckInOutStatus(int checkInOutStatus) {
		this.checkInOutStatus = checkInOutStatus;
	}

	public int getCheckImageStatus() {
		return checkImageStatus;
	}

	public void setCheckImageStatus(int checkImageStatus) {
		this.checkImageStatus = checkImageStatus;
	}

	public String getPhotoBgPath() {
		return photoBgPath;
	}

	public void setPhotoBgPath(String photoBgPath) {
		this.photoBgPath = photoBgPath;
	}

	public String getProfileBgPath() {
		return profileBgPath;
	}

	public void setProfileBgPath(String profileBgPath) {
		this.profileBgPath = profileBgPath;
	}

	public String getProfilePath() {
		return profilePath;
	}

	public void setProfilePath(String profilePath) {
		this.profilePath = profilePath;
	}

	public String getTextChat() {
		return textChat;
	}

	public void setTextChat(String textChat) {
		this.textChat = textChat;
	}

	public String getFormattedTimeChat(Context ctx) {
		long someValue = Long.parseLong(timeChat);
		Date result  = new Date(someValue*1000);
		
		String formattedTimestamp = "";
		
//		
//		try {
//			SimpleDateFormat df = new SimpleDateFormat(
//					"EEE dd, MMM yyyy hh:mm aaa", Locale.ENGLISH);
//			result = df.parse(timeChat);
//		} catch (ParseException e) {
//			try {
//				SimpleDateFormat df = new SimpleDateFormat(
//						"MMM dd, yyyy, hh:mm aaa", Locale.ENGLISH);
//				result = df.parse(timeChat);
//			} catch (ParseException e1) {
//				// e1.printStackTrace();
//
//			}
//			// e.printStackTrace();
//		}
//		if (result == null) {
//			return timeChat;
//		}
		if (isToday(result)) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("h:mm aaa",
						Locale.ENGLISH);

				formattedTimestamp = ctx.getString(R.string.today_text) + ", "
						+ formatter.format(result);

			} catch (Exception ex) {
				System.out.println(result);
				System.out.println(formattedTimestamp);

			}
		} else if (isYesterday(result)) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("h:mm aaa",
						Locale.ENGLISH);
				formattedTimestamp = ctx.getString(R.string.yesterday_test)
						+ ", " + formatter.format(result);

			} catch (Exception ex) {
				System.out.println(result);
				System.out.println(formattedTimestamp);

			}
		} else {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat(
						"MMM dd, yyyy, h:mm aaa", Locale.ENGLISH);
				formattedTimestamp = formatter.format(result);

			} catch (Exception ex) {
				System.out.println(result);
				System.out.println(formattedTimestamp);

			}
		}

		return formattedTimestamp;
	}

	// public String getTimeChat(Context ctx) {
	//
	// return timeChat;
	// }

	public static boolean isToday(Date date) {
		return isSameDay(date, Calendar.getInstance().getTime());
	}

	public static boolean isYesterday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return isSameDay(date, cal.getTime());
	}

	public static boolean isSameDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return isSameDay(cal1, cal2);
	}

	/**
	 * <p>
	 * Checks if two calendars represent the same day ignoring time.
	 * </p>
	 * 
	 * @param cal1
	 *            the first calendar, not altered, not null
	 * @param cal2
	 *            the second calendar, not altered, not null
	 * @return true if they represent the same day
	 * @throws IllegalArgumentException
	 *             if either calendar is <code>null</code>
	 */
	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
				&& cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1
					.get(Calendar.DAY_OF_YEAR) == cal2
				.get(Calendar.DAY_OF_YEAR));
	}

	public void setTimeChat(String timeChat) {
		this.timeChat = timeChat;
	}

	public String getMessageImageID() {
		return messageImageID;
	}

	public void setMessageImageID(String messageImageID) {
		this.messageImageID = messageImageID;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}
}