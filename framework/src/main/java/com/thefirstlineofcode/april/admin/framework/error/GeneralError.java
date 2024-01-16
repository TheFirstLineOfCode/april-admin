package com.thefirstlineofcode.april.admin.framework.error;

public enum GeneralError {
	@ErrorCode(value = Code.INTERNAL_SERVER_ERROR, description = "System error. For example: Failed to parse parameters, Can't access database, ....")
	INTERNAL_SERVER_ERROR,
	@ErrorCode(value = Code.COMMON_VALDATION_ERROR, description = "Common validtion error.")
	COMMON_VALDATION_ERROR;
	
	public interface Code {
		public static final int INTERNAL_SERVER_ERROR = 990001;
		public static final int COMMON_VALDATION_ERROR = 990002;
	}
}
