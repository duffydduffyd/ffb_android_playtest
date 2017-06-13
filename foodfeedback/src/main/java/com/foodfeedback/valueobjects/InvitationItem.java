package com.foodfeedback.valueobjects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class InvitationItem implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3706325563690770156L;
	private int id;
	private int status;
	@SerializedName("timestamp")
	private long timeStamp;
	private Invitee invitee;
	private Sender sender;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Invitee getInvitee() {
		return invitee;
	}

	public void setInvitee(Invitee invitee) {
		this.invitee = invitee;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getTimestamp() {
		return timeStamp;
	}

	public void setTimestamp(long timestamp) {
		this.timeStamp = timestamp;
	}

	public Sender getSender() {
		return sender;
	}

	public void setSender(Sender sender) {
		this.sender = sender;
	}

}
// {
// "status": 0,
// "messagetext": "Fetched invites successfully.",
// "data": [
// {
// "status": -1,
// "timestamp": 1385016542,
// "invitee": {
// "bio": "",
// "first_name": "Mohammed",
// "last_name": "Gouse",
// "profile_image_url": "",
// "profile_image_thumb_url": "",
// "coach": 0,
// "id": 96
// },
// "id": 84,
// "sender": {
// "bio": "",
// "first_name": "Ghouse",
// "last_name": "Mohammed",
// "profile_image_url": "",
// "profile_image_thumb_url": "",
// "coach": 0,
// "id": 97
// }
// }
// ]
// }
