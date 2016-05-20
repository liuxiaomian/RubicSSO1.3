package com.rubic.sso.util.exception;

@SuppressWarnings("serial")
public class IllegalPasswordException extends Exception {

	public IllegalPasswordException(){
		super();
	}

	public IllegalPasswordException(String msg){
		super(msg);
	}
}
