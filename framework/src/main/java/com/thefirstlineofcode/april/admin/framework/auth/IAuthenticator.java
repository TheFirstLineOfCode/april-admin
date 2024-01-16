package com.thefirstlineofcode.april.admin.framework.auth;

public interface IAuthenticator {
	Object getCredentials(Object principal) throws PrincipalNotFoundException;
	boolean exists(Object principal);
}
