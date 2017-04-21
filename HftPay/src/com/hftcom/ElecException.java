package com.hftcom;


public class ElecException extends RuntimeException {

	private static final long serialVersionUID = 5580564560409616189L;
	private String message;

	public ElecException(String message){
		super();
		this.message = message;
	}
	
	public String getErrMsg(){
		return this.message;
	}
	
	public String getMessage(){
		return this.message;
	}
}
