package com.foodfeedback.valueobjects;

import java.io.Serializable;

import android.graphics.Bitmap;

import com.foodfeedback.onboarding.ChatEntryAdapter;

public class TransferObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7948692455571290433L;
	public static AccountOperations accountOperations;
	public static QuestionsAnswers questionsAnswers;
	public static SuggestedCoaches suggestedCoaches;
	public static String createAccountFirstName;
	public static String createAccountLastName;
	public static String createAccountEmail;
	public static String loginPassword;
	public static CoachList coachList;
	public static InteractedWith interactedWith;
	public static CoachList searchList;
	public static Invitations invitations;
	public static FetchGallery fetchGallery;
	public static MessageList messageFromUser;

	public static Bitmap loginUserThumbImage;
	public static Bitmap interactedWithBitmap;
	public static Bitmap defaultBitmapImage;
	
//	public static ArrayList<String> ids;
//	public static ArrayList<String> names;
	
	public static ChatEntryAdapter chatEntryAdapter;

	public static ChatEntryAdapter getChatEntryAdapter() {
		return chatEntryAdapter;
	}

	public static void setChatEntryAdapter(ChatEntryAdapter chatEntryAdapter) {
		TransferObject.chatEntryAdapter = chatEntryAdapter;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/*public static ArrayList<String> getIds() {
		return ids;
	}

	public static void setIds(ArrayList<String> ids) {
		TransferObject.ids = ids;
	}

	public static ArrayList<String> getNames() {
		return names;
	}

	public static void setNames(ArrayList<String> names) {
		TransferObject.names = names;
	}*/

	public static Bitmap getDefaultBitmapImage() {
		return defaultBitmapImage;
	}

	public static void setDefaultBitmapImage(Bitmap defaultBitmapImage) {
		TransferObject.defaultBitmapImage = defaultBitmapImage;
	}

	public static Bitmap getInteractedWithBitmap() {
		return interactedWithBitmap;
	}

	public static void setInteractedWithBitmap(Bitmap interactedWithBitmap) {
		TransferObject.interactedWithBitmap = interactedWithBitmap;
	}

	/*
	 * public static CoachList getCoachList() { return coachList; }
	 * 
	 * public static void setCoachList(CoachList coachList) {
	 * TransferObject.coachList = coachList; }
	 */

	public static CoachList getCoachList() {
		return coachList;
	}

	public static void setCoachList(CoachList coachList) {
		TransferObject.coachList = coachList;
	}

	public static Bitmap getLoginUserThumbImage() {
		return loginUserThumbImage;
	}

	public static void setLoginUserThumbImage(Bitmap loginUserThumbImage) {
		TransferObject.loginUserThumbImage = loginUserThumbImage;
	}

	public static MessageList getMessageFromUser() {
		return messageFromUser;
	}

	public static void setMessageFromUser(MessageList messageFromUser) {
		TransferObject.messageFromUser = messageFromUser;
	}

	public static FetchGallery getFetchGallery() {
		return fetchGallery;
	}

	public static void setFetchGallery(FetchGallery fetchGallery) {
		TransferObject.fetchGallery = fetchGallery;
	}

	public static Invitations getInvitations() {
		return invitations;
	}

	public static void setInvitations(Invitations invitations) {
		TransferObject.invitations = invitations;
	}

	public static CoachList getSearchList() {
		return searchList;
	}

	public static void setSearchList(CoachList searchList) {
		TransferObject.searchList = searchList;
	}

	public static InteractedWith getInteractedWith() {
		return interactedWith;
	}

	public static void setInteractedWith(InteractedWith interactedWith) {
		TransferObject.interactedWith = interactedWith;
	}

	public static String getLoginPassword() {
		return loginPassword;
	}

	public static void setLoginPassword(String loginPassword) {
		TransferObject.loginPassword = loginPassword;
	}

	public static String getCreateAccountFirstName() {
		return createAccountFirstName;
	}

	public static void setCreateAccountFirstName(String createAccountFirstName) {
		TransferObject.createAccountFirstName = createAccountFirstName;
	}

	public static String getCreateAccountLastName() {
		return createAccountLastName;
	}

	public static void setCreateAccountLastName(String createAccountLastName) {
		TransferObject.createAccountLastName = createAccountLastName;
	}

	public static String getCreateAccountEmail() {
		return createAccountEmail;
	}

	public static void setCreateAccountEmail(String createAccountEmail) {
		TransferObject.createAccountEmail = createAccountEmail;
	}

	public static QuestionsAnswers getQuestionsAnswers() {
		return questionsAnswers;
	}

	public static void setQuestionsAnswers(QuestionsAnswers questionsAnswers) {
		TransferObject.questionsAnswers = questionsAnswers;
	}

	public static AccountOperations getAccountOperations() {
		return accountOperations;
	}

	public static void setAccountOperations(AccountOperations accountOperations) {
		TransferObject.accountOperations = accountOperations;
	}

	/*
	 * public static String getCreateAccountPassword() { return
	 * createAccountPassword; }
	 * 
	 * public static void setCreateAccountPassword(String createAccountPassword)
	 * { TransferObject.createAccountPassword = createAccountPassword; }
	 */

	public static SuggestedCoaches getSuggestedCoaches() {
		return suggestedCoaches;
	}

	public static void setSuggestedCoaches(SuggestedCoaches suggestedCoaches) {
		TransferObject.suggestedCoaches = suggestedCoaches;
	}
}
