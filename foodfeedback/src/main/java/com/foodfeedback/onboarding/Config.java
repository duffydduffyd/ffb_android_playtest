package com.foodfeedback.onboarding;

public interface Config {

    public static final String APNS_MESSAGE = "APNS_Message";
    public static final String APNS_MESSAGENOIMAGE = "APNS_MessageNoImage";
    public static final String APNS_COACHINVITE = "APNS_CoachInvite";
    public static final String APNS_COACHINVITEACCEPT = "APNS_CoachInviteAccept";
    public static final String APNS_COACHINVITEDECLINE = "APNS_CoachInviteDecline";
    public static final String APNS_REMINDERWEEKLY = "APNS_ReminderWeekly";

    static final boolean SECOND_SIMULATOR = false;

    // Google project id
    //static final String GOOGLE_SENDER_ID = "204955736787"; // Older
    static final String GOOGLE_SENDER_ID = "510528166334"; //feedback@foodfeedback.com
     

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM Android Example";

    // Broadcast reciever name to show gcm registration messages on screen
    static final String DISPLAY_REGISTRATION_MESSAGE_ACTION =
    // "com.androidexample.gcm.DISPLAY_REGISTRATION_MESSAGE";
    "com.android.foodfeedback.gcm.DISPLAY_REGISTRATION_MESSAGE";

    // Broadcast reciever name to show user messages on screen
    static final String DISPLAY_MESSAGE_ACTION = "com.android.foodfeedback.gcm.DISPLAY_MESSAGE";

    // Parse server message with this name
    static final String EXTRA_MESSAGE = "message";

    // Parse server message with this name
    static final String EXTRA_TITLE = "title";

    // Parse server message with this name
    static final String SID = "sid";

    static final String PID = "pid";

    /*
     * static final String CHAT_SCREEN =
     * "com.android.foodfeedback.message.ChatActivity";
     */
}
