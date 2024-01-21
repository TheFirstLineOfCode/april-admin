package com.thefirstlineofcode.april.admin.core.auth;

public interface IAuthenticator {
	Object getCredentials(Object principal) throws PrincipalNotFoundException;
	boolean exists(Object principal);
}
