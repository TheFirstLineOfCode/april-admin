package com.thefirstlineofcode.april.admin.framework.apidocs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.thefirstlineofcode.april.admin.framework.apidocs.annotations.ApiError;
import com.thefirstlineofcode.april.admin.framework.apidocs.annotations.ApiErrors;
import com.thefirstlineofcode.april.admin.framework.apidocs.annotations.ErrorStatusMapping;
import com.thefirstlineofcode.april.admin.framework.apidocs.annotations.ErrorStatusMappings;
import com.thefirstlineofcode.april.admin.framework.error.ErrorCode;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 100)
public class ErrorStatusMappingsReader implements OperationBuilderPlugin {
	private String rowTemplate = "<tr><td>%d</td><td>%s</td><td>%s</td><td>%d</td></tr>";
	
	@Override
	public void apply(OperationContext context) {
		ApiErrors apiErrors = context.findAnnotation(ApiErrors.class).orNull();
		Error[] errors = getErrors(apiErrors);
		
		ErrorStatusMappings errorStatusMappings = context.findAnnotation(ErrorStatusMappings.class).orNull();
		
		String errorDescription = getErrorDescription(errors, errorStatusMappings);
		if (errorDescription != null) {
			String oldNotes = context.operationBuilder().build().getNotes();
			if (oldNotes != null) {
				context.operationBuilder().notes(oldNotes + errorDescription);
			} else {
				context.operationBuilder().notes(errorDescription);
			}
		}
	}
	
	private String getErrorDescription(Error[] errors, ErrorStatusMappings errorStatusMappings) {
		if (errors == null || errors.length == 0)
			return null;
		
		StringBuilder sb = new StringBuilder();

		sb.append("<h4>返回错误编码对照表</h4>");
		sb.append("<table class='fullwidth'><thead><tr><th width='15%'>Error Code</th><th width='20%'>Error Value</th><th>Description</th><th width='15%'>Http Status Code</th></tr></thead><tbody>");
		
		for (Error error : errors) {
			HttpStatus status = getStatus(error, errorStatusMappings);
			String row = String.format(rowTemplate, error.getCode(), error.getValue(), error.getDescription(), status.value());
			sb.append(row);
		}
		
		sb.append("</tbody></table>");
		
		return sb.toString();
	}

	private HttpStatus getStatus(Error error, ErrorStatusMappings errorStatusMappings) {
		if (errorStatusMappings == null) {
			return HttpStatus.BAD_REQUEST;
		}
		
		for (ErrorStatusMapping errorStatusMapping : errorStatusMappings.value()) {
			if (errorStatusMapping.errorCode() == error.getCode()) {
				return errorStatusMapping.status();
			}
		}
		
		return HttpStatus.BAD_REQUEST;
	}

	private Error[] getErrors(ApiErrors apiErrors) {
		if (apiErrors == null)
			return new Error[0];
		
		List<Error> errors = new ArrayList<Error>();
		Class<? extends Enum<?>>[] autoDetectedTypes = apiErrors.autoDetectedTypes();
		for (Class<? extends Enum<?>> type : autoDetectedTypes) {
			addToErrors(errors, detectErrorsFromType(type));
		}
		
		for (ApiError apiError : apiErrors.errors()) {
			Class<? extends Enum<?>> type = apiError.type();
			List<Error> someErrors = detectErrorsFromType(type);
			
			if (someErrors.isEmpty())
				continue;
			
			int[] codes = apiError.codes();
			if (apiError.strategy() == ApiError.Strategy.EXCLUDES) {
				someErrors = removeExcludes(someErrors, codes);
			} else {
				someErrors = reserveIncludes(someErrors, codes);
			}
			
			addToErrors(errors, someErrors);
		}
		
		return errors.toArray(new Error[errors.size()]);
	}
	
	 private void addToErrors(List<Error> errors, List<Error> someErrors) {
		for (Error error : someErrors) {
			addToErrors(errors, error);
		}
	}

	private void addToErrors(List<Error> errors, Error error) {
		if (!errors.contains(error)) {
			errors.add(error);
		}
	}

	private List<Error> reserveIncludes(List<Error> errors, int[] codes) {
		 List<Error> includes = new ArrayList<Error>();
		 
		 for (int code : codes) {
			 Error error = findErrorFromList(errors, code);
			 
			 if (error != null) {
				 includes.add(error);
			 }
		 }
		 
		return includes;
	}

	private Error findErrorFromList(List<Error> errors, int code) {
		for (Error error : errors) {
			if (code == error.getCode())
				return error;
		}
		
		return null;
	}

	private List<Error> removeExcludes(List<Error> errors, int[] codes) {
		for (int code : codes) {
			errors.remove(new Error(code, null, null));
		}
		
		return errors;
	}

	private List<Error> detectErrorsFromType(Class<? extends Enum<?>> type) {
		List<Error> errors = new ArrayList<Error>();
		
		try {
			Method valuesMethod = type.getMethod("values", new Class<?>[0]);
			Object[] values = (Object[])valuesMethod.invoke(null, new Object[0]);
			
			for (Object value : values) {
				ErrorCode errorCode = getErrorCode((Enum<?>)value);
				errors.add(new Error(errorCode.value(), getErrorValueString(type, value), errorCode.description()));
			}
		} catch (Exception e) {
			throw new RuntimeException("Can't read api error info.", e);
		}
		
		return errors;
	}

	private String getErrorValueString(Class<? extends Enum<?>> type, Object value) {
		return String.format("%s.%s", type.getSimpleName(), value.toString());
	}

	private <T extends Enum<?>> ErrorCode getErrorCode(T error) {
	        Field errorField = null;
	        try {
	            errorField = error.getClass().getField(error.name());
	        } catch (Exception e) {
	            return null;
	        }
	        
	        return errorField.getAnnotation(ErrorCode.class);
	    }

	private class Error {
		private int code;
		private String value;
		private String description;
		
		public Error(int code, String value, String description) {
			this.code = code;
			this.value = value;
			this.description = description;
		}
		
		public int getCode() {
			return code;
		}
		
		public String getValue() {
			return value;
		}
		
		public String getDescription() {
			return description;
		}
		
		@Override
		public int hashCode() {
			return Integer.hashCode(code);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Error) {
				Error other = (Error)obj;
				
				return other.code == this.code;
			}
			
			return false;
		}
		
	}
	
	@Override
	public boolean supports(DocumentationType delimiter) {
		return SwaggerPluginSupport.pluginDoesApply(delimiter);
	}
}