package com.liugeng.mthttp.router.support;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.HttpExecutor;
import com.liugeng.mthttp.router.HttpResponseResolver;

public class DefaultHttpExecutor implements HttpExecutor {

	private ExecutedMethodWrapper userMethod;

	public DefaultHttpExecutor(ExecutedMethodWrapper userMethod) {
		this.userMethod = userMethod;
	}

	@Override
	public void execute(ConnectContext context) throws Exception {
		HttpRequestEntity requestEntity = context.getRequest();
		Map<String, List<String>> queryParams = requestEntity.getQueryParams();
		Map<String, Object> reqAttributes = requestEntity.getAttributes();
		Object[] args = getMethodArgumentValues(queryParams, reqAttributes, context);
		Object returnValue = invokeUserMethod(args);
		createResponse(returnValue, context);
	}

	private void createResponse(Object returnValue, ConnectContext context) {
		HttpResponseResolver resolver = chooseRspResolver(returnValue, context);
		resolver.resolve(returnValue, context);
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

	private Object[] getMethodArgumentValues(Map<String, List<String>> queryParams, Map<String, Object> reqAttributes, ConnectContext context) {
		Map<String, Object> origParams = getReqParams(queryParams, reqAttributes, context);
		Parameter[] parameters = userMethod.getParameters();
		if (parameters == null || parameters.length == 0) {
			return null;
		}
		Object[] argsValues = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			String argName = parameters[i].getName();
			Class argClazz = parameters[i].getType();
			Object argValue = origParams.get(argName);
			argsValues[i] = convertArgType(argClazz, argValue);
		}
		return argsValues;
	}

	private Object convertArgType(Class argClazz, Object argValue) {
		// todo
		return argValue;
	}

	private Map<String, Object> getReqParams(Map<String, List<String>> queryParams, Map<String, Object> reqAttributes, ConnectContext context) {
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
