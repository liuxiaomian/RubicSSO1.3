package com.rubic.sso.po;

import java.io.Serializable;
import java.sql.Timestamp;

public class Ticket implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String email;
	
	private int userId;
	
	private Timestamp recoverTime;
	
	public Ticket() {
		super();
	}

	public Ticket(String email, int userId,
			Timestamp recoverTime) {
		super();
		this.email = email;
		this.userId = userId;
		this.recoverTime = recoverTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Timestamp getRecoverTime() {
		return recoverTime;
	}

	public void setRecoverTime(Timestamp recoverTime) {
		this.recoverTime = recoverTime;
	}
	
}
