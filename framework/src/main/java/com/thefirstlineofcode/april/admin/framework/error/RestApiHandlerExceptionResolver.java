package com.thefirstlineofcode.april.admin.framework.error;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MapMethodProcessor;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ModelAndViewMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ViewMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.ViewNameMethodReturnValueHandler;

import com.thefirstlineofcode.april.admin.framework.apidocs.annotations.ErrorStatusMapping;
import com.thefirstlineofcode.april.admin.framework.apidocs.annotations.ErrorStatusMappings;

public class RestApiHandlerExceptionResolver implements HandlerExceptionResolver {
	private static final String HEADER_NAME_APRIL_ERROR_CODE = "April-Error-Code";

	private final Logger logger = LoggerFactory.getLogger(RestApiHandlerExceptionResolver.class);

	private HandlerMethodReturnValueHandlerComposite returnValueHandlers;
	private List<HttpMessageConverter<?>> messageConverters;
	private ContentNegotiationManager contentNegotiationManager;
	private final List<Object> responseBodyAdvice = new ArrayList<Object>();

	private ConcurrentMap<Method, ErrorStatusMappingsHolder> methodErrorStatusMappingsCache = new ConcurrentHashMap<Method, ErrorStatusMappingsHolder>();
	private ConcurrentMap<Class<?>, ErrorStatusMappingsHolder> typeErrorStatusMappingsCache = new ConcurrentHashMap<Class<?>, ErrorStatusMappingsHolder>();

	public void setReturnValueHandlers(HandlerMethodReturnValueHandlerComposite returnValueHandlers) {
		this.returnValueHandlers = returnValueHandlers;
	}

	public void setContentNegotiationManager(ContentNegotiationManager contentNegotiationManager) {
		this.contentNegotiationManager = contentNegotiationManager;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
				Object handler, Exception ex) {
		ServletWebRequest webRequest = new ServletWebRequest(request, response);
		ModelAndViewContainer mavContainer = new ModelAndViewContainer();
		logger.error(ex.getMessage(), ex);
		IError e = getError(ex);
		if (e != null) {
			return handle(webRequest, mavContainer, handler, e);
		} else {
			return handle(webRequest, mavContainer, handler, ex);
		}
	}

	private ModelAndView handle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer, Object handler,
			Exception ex) {
		webRequest.getResponse().setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		webRequest.getResponse().setHeader(HEADER_NAME_APRIL_ERROR_CODE,
                Integer.toString(GeneralError.Code.INTERNAL_SERVER_ERROR));

		UnexpectedException ue = new UnexpectedException(ex);
		webRequest.getResponse().setContentType(MediaType.APPLICATION_JSON_VALUE);
		try {
			this.returnValueHandlers.handleReturnValue(
					ue, getReturnValueType(((HandlerMethod)handler).getMethod(), ue), mavContainer, webRequest);
		} catch (Exception e) {
			logger.error("Can't process error data.", e);
			return null;
		}

		mavContainer.setRequestHandled(true);
		return new ModelAndView();
	}

	private IError getError(Exception ex) {
		if (ex == null)
			return null;

		Throwable current = ex;
		if (current instanceof IError) {
			return (IError)current;
		}

		if (current instanceof InvocationTargetException) {
			if (((InvocationTargetException)current).getTargetException() != null) {
				current = ((InvocationTargetException)current).getTargetException();

				return getError((Exception)current);
			}
		}

		return null;
	}

	protected ModelAndView handle(ServletWebRequest webRequest, ModelAndViewContainer mavContainer,
			Object handler, IError e) {
		if (handler instanceof HandlerMethod) {
			Method method = ((HandlerMethod)handler).getMethod();
			Class<?> type = ((HandlerMethod)handler).getBeanType();

			ErrorStatusMappingsHolder holder = getErrorStatusMappingsHolder(method, methodErrorStatusMappingsCache);
			HttpStatus status = getMappedStatus(holder, e.getErrorCode());

			if (status == null) {
				holder = getErrorStatusMappingsHolder(type, typeErrorStatusMappingsCache);
				status = getMappedStatus(holder, e.getErrorCode());
			}

			if (status == null) {
				status = HttpStatus.BAD_REQUEST;
			}

			webRequest.getResponse().setStatus(status.value());
		} else {
			webRequest.getResponse().setStatus(HttpStatus.BAD_REQUEST.value());
		}

		webRequest.getResponse().setHeader(HEADER_NAME_APRIL_ERROR_CODE, Integer.toString(e.getErrorCode()));
		Object returnValue = e.getData();

		if (returnValue != null) {
			webRequest.getResponse().setContentType(MediaType.APPLICATION_JSON_VALUE);
			try {
				this.returnValueHandlers.handleReturnValue(
						returnValue, getReturnValueType(((HandlerMethod)handler).getMethod(), returnValue), mavContainer, webRequest);
			} catch (Exception ex) {
				logger.error("Can't process error data.", ex);
				return null;
			}
		}

		mavContainer.setRequestHandled(true);
		return new ModelAndView();
	}

	private HttpStatus getMappedStatus(ErrorStatusMappingsHolder holder, int errorCode) {
		if (holder.getMappings() != null) {
			for (ErrorStatusMapping mapping : holder.getMappings().value()) {
				if (mapping.errorCode() == errorCode) {
					return mapping.status();
				}
			}
		}

		return null;
	}

	private <T extends AnnotatedElement> ErrorStatusMappingsHolder getErrorStatusMappingsHolder(T key, ConcurrentMap<T, ErrorStatusMappingsHolder> cache) {
		ErrorStatusMappingsHolder holder = cache.get(key);
		if (holder == null) {
			ErrorStatusMappings mappings = key.getAnnotation(ErrorStatusMappings.class);
			holder = new ErrorStatusMappingsHolder(mappings);
			ErrorStatusMappingsHolder previous = cache.putIfAbsent(key, holder);
			if (previous != null) {
				holder = previous;
			}
		}

		return holder;
	}

	private MethodParameter getReturnValueType(Method method,Object returnValue) {
		return new ReturnValueMethodParameter(method,returnValue);
	}

	public List<HttpMessageConverter<?>> getMessageConverters() {
		return this.messageConverters;
	}

	private class ReturnValueMethodParameter extends MethodParameter {

		private final Object returnValue;

		public ReturnValueMethodParameter(Method method,Object returnValue) {
			super(method, -1);
			this.returnValue = returnValue;
		}

		@Override
		public Class<?> getParameterType() {
			return this.returnValue.getClass();
		}
	}

	public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
		this.messageConverters = messageConverters;
	}

	public void setResponseBodyAdvice(List<ResponseBodyAdvice<?>> responseBodyAdvice) {
		this.responseBodyAdvice.clear();
		if (responseBodyAdvice != null) {
			this.responseBodyAdvice.addAll(responseBodyAdvice);
		}
	}

	public List<HandlerMethodReturnValueHandler> getDefaultReturnValueHandlers() {
		List<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>();

		// Single-purpose return value types
		handlers.add(new ModelAndViewMethodReturnValueHandler());
		handlers.add(new ModelMethodProcessor());
		handlers.add(new ViewMethodReturnValueHandler());
		handlers.add(new HttpEntityMethodProcessor(
				getMessageConverters(), this.contentNegotiationManager, this.responseBodyAdvice));

		// Annotation-based return value types
		handlers.add(new ModelAttributeMethodProcessor(false));
		handlers.add(new RequestResponseBodyMethodProcessor(
				getMessageConverters(), this.contentNegotiationManager, this.responseBodyAdvice));

		// Multi-purpose return value types
		handlers.add(new ViewNameMethodReturnValueHandler());
		handlers.add(new MapMethodProcessor());

		// Catch-all
		handlers.add(new ModelAttributeMethodProcessor(true));

		return handlers;
	}

	private static class ErrorStatusMappingsHolder {
		private ErrorStatusMappings mappings;

		public ErrorStatusMappingsHolder(ErrorStatusMappings mappings) {
			this.mappings = mappings;
		}

		public ErrorStatusMappings getMappings() {
			return mappings;
		}
	}
}