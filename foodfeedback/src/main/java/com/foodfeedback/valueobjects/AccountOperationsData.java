package com.foodfeedback.valueobjects;

import java.io.Serializable;

public class AccountOperationsData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2298435880416912942L;
	private AccountUser user;

	public AccountUser getUser() {
		return user;
	}

	public void setUser(AccountUser user) {
		this.user = user;
	}
}
