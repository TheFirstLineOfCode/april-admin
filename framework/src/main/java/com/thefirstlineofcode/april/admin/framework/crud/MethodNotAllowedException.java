package com.thefirstlineofcode.april.admin.framework.crud;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.METHOD_NOT_ALLOWED)
public class MethodNotAllowedException extends RuntimeException {
	private static final long serialVersionUID = -2363243246566399905L;
}
