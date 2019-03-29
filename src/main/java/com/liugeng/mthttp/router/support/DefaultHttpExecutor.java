package com.liugeng.mthttp.router.support;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;

import com.liugeng.mthttp.exception.HttpRequestException;
import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.HttpExecutor;
import com.liugeng.mthttp.router.HttpResponseResolver;
import com.liugeng.mthttp.utils.converter.SimpleTypeConverter;
import com.liugeng.mthttp.utils.converter.TypeConverter;
import io.netty.handler.codec.http.HttpResponseStatus;

public class DefaultHttpExecutor implements HttpExecutor {

	private ExecutedMethodWrapper userMethod;

	public DefaultHttpExecutor(ExecutedMethodWrapper userMethod) {
		this.userMethod = userMethod;
	}

	@Override
	public void execute(ConnectContext context) throws Exception {
		Object[] args = getMethodArgumentValues(context);
		Object returnValue = invokeUserMethod(args);
		createResponse(returnValue, context);
	}

	private void createResponse(Object returnValue, ConnectContext context) {
		HttpResponseResolver resolver = chooseRspResolver(returnValue, context);
		resolver.resolve(returnValue, context, HttpResponseStatus.OK);
	}

	private HttpResponseResolver chooseRspResolver(Object returnValue, ConnectContext context) {
		// todo
		return new TextPlainResponseResolver();
	}

	private Object invokeUserMethod(Object[] args) throws Exception {
		Object userObject = userMethod.getUserObject();
		Method method = userMethod.getUserMethod();
		return method.invoke(userObject, args);
	}

	private Object[] getMethodArgumentValues(ConnectContext context) {
		Map<String, Object> origParams = getReqParams(context);
		Parameter[] parameters = userMethod.getParameters();
		if (parameters == null || parameters.length == 0) {
			return null;
		}
		Object[] argsValues = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			String argName = parameters[i].getName();
			Class<?> argClazz = parameters[i].getType();
			Object argValue = origParams.get(argName);
			argsValues[i] = convertArgType(argClazz, argValue);
		}
		return argsValues;
	}


	private Object convertArgType(Class<?> argClazz, Object argValue) {
		try {
			if (argValue == null) {
				boolean isPrim = ClassUtils.isPrimitiveOrWrapper(argClazz);
				if (isPrim) {
					argValue = "0";
				} else {
					return null;
				}
			}
			TypeConverter converter = new SimpleTypeConverter();
			return converter.convertIfNecessary(argValue, argClazz);
		} catch (Exception e) {
			throw new HttpRequestException(
				String.format("your request parameter: { %s } is not valid, error: %s", argValue, e.getMessage()),
				e, HttpResponseStatus.BAD_REQUEST
			);
		}
	}

	private Map<String, Object> getReqParams(ConnectContext context) {
		Map<String, List<String>> queryParams = context.getRequest().getQueryParams();
		Map<String, Object> reqAttributes = context.getRequest().getAttributes();
		Map<String, Object> origParams = new HashMap<>(reqAttributes);
		setCookieAndSession(origParams, context);
		if (queryParams != null && !queryParams.isEmpty()) {
			queryParams.entrySet()
				.stream()
				.filter(e -> e.getValue() != null && !e.getValue().isEmpty())
				.forEach(e -> origParams.put(e.getKey(), e.getValue().get(0)));
		}
		return origParams;
	}

	private void setCookieAndSession(Map<String, Object> origParams, ConnectContext context) {
		// todo
	}
}
