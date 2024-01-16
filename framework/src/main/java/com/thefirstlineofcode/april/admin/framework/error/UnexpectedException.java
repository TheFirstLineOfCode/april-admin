package com.thefirstlineofcode.april.admin.framework.error;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class UnexpectedException {
	private String message;
	private Class<?> type;
	private String details;
	
	public UnexpectedException(Exception e) {
		this(e.getMessage(), e.getClass(), getExceptionDetails(e));
	}
	
	private static String getExceptionDetails(Exception e) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream(new BufferedOutputStream(output));
		e.printStackTrace(stream);
		stream.flush();
		
		try {
			return output.toString("utf-8");
		} catch (UnsupportedEncodingException e1) {
			return output.toString();
		}
	}
	
	public UnexpectedException(String message, Class<?> type, String details) {
		this.message = message;
		this.type = type;
		this.details = details;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public void setType(Class<?> type) {
		this.type = type;
	}
	
	public String getDetails() {
		return details;
	}
	
	public void setDetails(String details) {
		this.details = details;
	}
}