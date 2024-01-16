package com.thefirstlineofcode.april.admin.framework.error;

public class ValidationException extends ApplicationException {
	private static final long serialVersionUID = -4598024049076008117L;
	
	public ValidationException(String message) {
		this(GeneralError.COMMON_VALDATION_ERROR, message);
	}
	
	public ValidationException(Enum<?> error, String message) {
		super(error, message);
	}
}
