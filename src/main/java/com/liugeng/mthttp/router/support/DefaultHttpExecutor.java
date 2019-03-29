package com.liugeng.mthttp.router.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liugeng.mthttp.router.annotation.HttpRequestBody;
import com.liugeng.mthttp.utils.converter.ReferenceTypeConverter;
import com.liugeng.mthttp.utils.converter.RequestBodyTypeConverter;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.ClassUtils;

import com.liugeng.mthttp.exception.HttpRequestException;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.HttpExecutor;
import com.liugeng.mthttp.router.HttpResponseResolver;
import com.liugeng.mthttp.utils.converter.PrimitiveTypeConverter;
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
		try {
			Map<String, Object> requestParams = getReqParams(context);
			Parameter[] parameters = userMethod.getParameters();
			if (parameters == null || parameters.length == 0) {
				return null;
			}
			Object[] argsValues = new Object[parameters.length];
			for (int i = 0; i < parameters.length; i++) {
				String argName = parameters[i].getName();
				TypeConverter converter = switchConverter(parameters[i]);
				Object argValue = requestParams.get(argName);
				Class<?> argClazz = parameters[i].getType();
				argsValues[i] = converter.convertIfNecessary(argValue, argClazz);
			}
			return argsValues;
		} catch (Exception e) {
			throw new HttpRequestException(
					String.format("your request { %s } is not valid, error: %s", context.getRequest(), e.getMessage()),
					e, HttpResponseStatus.BAD_REQUEST
			);
		}
	}

	private TypeConverter switchConverter(Parameter parameter) {
		Annotation[] argAnnotations = parameter.getDeclaredAnnotations();
		Class<?> argClazz = parameter.getType();
		if (argAnnotations.length > 0) {
			for (Annotation annotation : argAnnotations) {
				if (annotation.annotationType() == HttpRequestBody.class) {
					return new RequestBodyTypeConverter();
				}
			}
		}
		if (ClassUtils.isPrimitiveOrWrapper(argClazz) || ClassUtils.isAssignable(argClazz, String.class)) {
			return new PrimitiveTypeConverter();
		}
		return new ReferenceTypeConverter();
	}

	private Map<String, Object> getReqParams(ConnectContext context) {
		Map<String, List<String>> queryParams = context.getRequest().getQueryParams();
		Map<String, Object> reqAttributes = context.getRequest().getAttributes();
		Map<String, Object> origParams = new HashMap<>(reqAttributes);
		if (queryParams != null && !queryParams.isEmpty()) {
			queryParams.entrySet()
				.stream()
				.filter(e -> e.getValue() != null && !e.getValue().isEmpty())
				.forEach(e -> origParams.put(e.getKey(), e.getValue().get(0)));
		}
		return origParams;
	}
}
