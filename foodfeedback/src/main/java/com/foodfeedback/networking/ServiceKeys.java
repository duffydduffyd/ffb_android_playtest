package com.foodfeedback.networking;

public class ServiceKeys {
	public static final boolean INDEMOMODE = false;
	// Test URL
	// public static final String serverURL = "http://foodfeedback.zpstage.com";

	// LIVE URL
	public static final String serverURL = "https://app.foodfeedback.com";

	// Account API service URL
	public static final String ACCOUNT_URL = serverURL + "/account";

	// Message API service URL
	public static final String MESSAGE_URL = serverURL + "/message";

	// Gallery API service URL
	public static final String GALLERY_URL = serverURL + "/gallery";

	// Coaches API service URL
	public static final String COACHES_URL = serverURL + "/coaches";

	// Questions API service URL
	public static final String QUESTION_URL = serverURL + "/question";

	// Tracker API service URL
	public static final String TRACKER_URL = serverURL + "/tracker";
	
	// Add GCMS
	public static final String GCMS_URL = serverURL + "account/add_gcms_device_token";	
	

	// Service URLs for Account API
	public static final String LOGIN_URL = ACCOUNT_URL + "/login";
	public static final String CREATE_ACCOUNT_URL = ACCOUNT_URL + "/create";
	public static final String RESET_PASSWORD_URL = ACCOUNT_URL
			+ "/reset_password";
	public static final String ACCOUNT_UPDATE_URL = ACCOUNT_URL + "/update";
	public static final String CHANGE_PASSWORD_URL = ACCOUNT_URL
			+ "/change_password";
	public static final String ADD_COACH_URL = ACCOUNT_URL + "/add_coach";
	public static final String REMOVE_COACH_URL = ACCOUNT_URL + "/remove_coach";
	public static final String INVITE_COACH_URL = ACCOUNT_URL + "/invite_coach";
	public static final String INVITATION_RESPONSE_URL = ACCOUNT_URL
			+ "/invitation_response";
	public static final String INVITATIONS_URL = ACCOUNT_URL + "/invitations?";
	public static final String SEND_INVITATIONS_URL = ACCOUNT_URL
			+ "/sent_invitations?";
	public static final String INTERACTED_WITH_URL = ACCOUNT_URL
			+ "/interacted_with?";
	public static final String ADD_GCM_DEVICE_TOKEN = ACCOUNT_URL
			+ "/add_gcms_device_token";

	// Service URLs for Message API
	public static final String SEND_MESSAGE_URL = MESSAGE_URL + "/send_message";
	public static final String MESSAGE_READ_URL = MESSAGE_URL + "/message_read";
	public static final String UPDATE_MESSAGE_URL = MESSAGE_URL + "/update";
	public static final String MESSAGE_FROM_USER_URL = MESSAGE_URL
			+ "/messages_from_user?";
	public static final String MESSAGE_UNREAD_URL = MESSAGE_URL
			+ "/messages_unread?";
	public static final String MESSAGE_WITH_IMAGES_URL = MESSAGE_URL
			+ "/messages_with_images?";

	// Service URLs for Gallery API
	public static final String FETCH_GALLERY_URL = GALLERY_URL + "/fetch?";
	// public static final String FETCH_NEW_GALLERY_URL = GALLERY_URL
	// + "/fetch_new?";
	public static final String UPDATE_GALLERY_URL = GALLERY_URL + "/update";
	public static final String ADD_IMAGE_GALLERY_URL = GALLERY_URL
			+ "/add_image";

	// Service URLs for Coaches API
	public static final String FETCH_COACHES_URL = COACHES_URL + "/coaches?";
	public static final String SEARCH_COACHES_URL = COACHES_URL + "/search?";
	public static final String SUGGESTED_COACHES_URL = COACHES_URL
			+ "/suggested?";

	// Service URLs for Questions API
	public static final String QUESTIONNAIRE_URL = QUESTION_URL
			+ "/questionnaire?";
	public static final String POST_ANSWERS_URL = QUESTION_URL
			+ "/post_answers";

	// Service URLs for Tracker API
	public static final String ADD_WEIGHT_URL = TRACKER_URL + "/add_weight";
	public static final String UPDATE_WEIGHT_URL = TRACKER_URL
			+ "/update_weight";
	public static final String DELETE_WEIGHT_URL = TRACKER_URL
			+ "/delete_weight";
	public static final String FETCH_WEIGHTS_URL = TRACKER_URL
			+ "/fetch_weights?";

	// String Constants for Account API
	public static final String LOGIN_SUCCESS = "login successful";
	public static final String CTREATEACCOUNT_SUCCESS = "create successful";
	public static final String RESET_PASSWORD_SUCCESS = "Password reset email has been sent.";
	public static final String ACCOUNT_UPDATE_SUCCESS = "update successful";
	public static final String CHANGE_PASSWORD_SUCCESS = "Password changed successfully.";
	public static final String ADD_COACH_SUCCESS = "Added coach successfully.";
	public static final String REMOVE_COACH_SUCCESS = "Removed coach successfully.";
	public static final String INVITE_COACH_SUCCESS = "Invited coach successfully.";
	public static final String INVITATION_RESPONSE_SUCCESS = "Invited coach successfully.";
	public static final String INVITATIONS_SUCCESS = "Fetched invites successfully.";
	public static final String SEND_INVITATIONS_SUCCESS = "Fetched invites successfully.";
	public static final String SEND_INVITATIONS_FAILURE = "Must include one filtering option.";
	public static final String INTERACTED_WITH_SUCCESS = "Fetched accounts successfully.";
	public static final String ADD_IOS_DEVICE_TOKEN_SUCCESS = "FetcLKJhed accounts successfully.";

	// String Constants for Message API
	public static final String SEND_MESSAGE_SUCCESS = "Sent messages successfully.";
	public static final String MESSAGE_READ_SUCCESS = "Read messages successfully.";
	public static final String UPDATE_MESSAGE_SUCCESS = "Updated messages successfully";
	public static final String MESSAGE_FROM_USER_SUCCESS = "Fetched messages from user.";
	public static final String MESSAGE_UNREAD_SUCCESS = "Fetched unread messages successfully.";
	public static final String MESSAGE_WITH_IMAGES_SUCCESS = "Fetched messages with images successfully.";

	// String Constants for Gallery API
	public static final String FETCH_GALLERY_SUCCESS = "Fetched gallery successfully.";
	public static final String FETCH_NEW_SUCCESS = "Fetched new gallery images successfully.";
	public static final String UPDATE_GALLERY_SUCCESS = "Gallery image updated.";
	public static final String ADD_IMAGE_GALLERY_SUCCESS = "Created gallery image successfully.";

	// String Constants for Coaches API
	public static final String FETCH_COACHES_SUCCESS = "Fetched coaches successfully.";
	public static final String SEARCH_COACHES_SUCCESS = "Fetched coaches successfully.";
	public static final String SUGGESTED_COACHES_SUCCESS = "Fetched suggested coaches successfully.";

	// String Constants for Questions API
	public static final String QUESTIONNAIRE_SUCCESS = "Fetched questionnaire successfully.";
	public static final String POST_ANSWERS_SUCCESS = "Posted answers successfully.";

	// String Constants for Tracker API
	public static final String ADD_WEIGHT_SUCCESS = "Created weight successfully.";
	public static final String UPDATE_WEIGHT_SUCCESS = "Updated weight successfully.";
	public static final String DELETE_WEIGHT_SUCCESS = "Deleted weight successfully.";
	public static final String FETCH_WEIGHTS_SUCCESS = "Fetched weights successfully.";

	// Integer Constants for Account API
	public static final int LOGIN_REQUEST_SERVICE = 1;
	public static final int CREATE_ACCOUNT_SERVICE = 2;
	public static final int RESET_PASSWORD_SERVICE = 3;
	public static final int ACCOUNT_UPDATE_SERVICE = 4;
	public static final int CHANGE_PASSWORD_SERVICE = 5;
	public static final int ADD_COACH_SERVICE = 6;
	public static final int REMOVE_COACH_SERVICE = 7;
	public static final int INVITE_COACH_SERVICE = 8;
	public static final int INVITATION_RESPONSE_SERVICE = 9;
	public static final int INVITATIONS_SERVICE = 10;
	public static final int SEND_INVITATIONS_SERVICE = 11;
	public static final int INTERACTED_WITH_SERVICE = 12;

	public static final int ADD_IOS_DEVICE_TOKEN_SERVICE = 101;

	// Integer Constants for Message API
	public static final int SEND_MESSAGE_SERVICE = 13;
	public static final int MESSAGE_READ_SERVICE = 14;
	public static final int UPDATE_MESSAGE_SERVICE = 15;
	public static final int MESSAGE_FROM_USER_SERVICE = 16;
	public static final int MESSAGE_UNREAD_SERVICE = 17;
	public static final int MESSAGE_WITH_IMAGES_SERVICE = 18;

	// Integer Constants for Gallery API
	public static final int FETCH_GALLERY_SERVICE = 19;
	// public static final int FETCH_NEW_GALLERY_SERVICE = 20;
	public static final int UPDATE_GALLERY_SERVICE = 21;
	public static final int ADD_IMAGE_GALLERY_SERVICE = 22;

	// Integer Constants for Coaches API
	public static final int FETCH_COACHES_SERVICE = 23;
	public static final int SEARCH_COACHES_SERVICE = 24;
	public static final int SUGGESTED_COACHES_SERVICE = 25;

	// Integer Constants for Questionnaire API
	public static final int QUESTIONNAIRE_SERVICE = 26;
	public static final int POST_ANSWERS_SERVICE = 27;

	// Integer Constants for Tracker API
	public static final int ADD_WEIGHT_SERVICE = 28;
	public static final int UPDATE_WEIGHT_SERVICE = 29;
	public static final int DELETE_WEIGHT_SERVICE = 30;
	public static final int FETCH_WEIGHTS_SERVICE = 31;

	public static int SEARCH_BYNAME = 0;
	public static final int SEARCH_BYNAME_MESSAGES = 300;
	public static final int SEARCH_BYNAME_COACHES = 400;

	public static int PROFILE_UPDATE = 0;
	public static final int PROFILE_UPDATE_REGISTRATION = 300;
	public static final int PROFILE_UPDATE_EDIT = 400;

	public static final int CHAT_IMAGE_NO_STATUS = 100;
	public static final int CHAT_IMAGE_YES_STATUS = 200;

	public static final int CHECK_INCOMING_STATUS = 222;
	public static final int CHECK_OUTGOING_STATUS = 111;

	public static final String NO_PHTOTBG = "noPhtoBg";
	public static final String NO_PROFILEBG = "noProfileBg";
	public static final String NO_PROFILE_PHOTO = "noProfilePhoto";

	public static final int COACH_ITEM_TYPE_MYCOACH = 115;
	public static final int COACH_ITEM_TYPE_INVITATION = 116;
	public static final int COACH_ITEM_TYPE_PENDINGTITLE = 117;

}
