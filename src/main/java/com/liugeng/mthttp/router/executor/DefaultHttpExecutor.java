package com.liugeng.mthttp.router.executor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.liugeng.mthttp.router.ExecutedMethodWrapper;
import com.liugeng.mthttp.router.resovler.TextPlainResponseResolver;

import com.liugeng.mthttp.exception.HttpRequestException;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.resovler.HttpResponseResolver;
import io.netty.handler.codec.http.HttpResponseStatus;

public class DefaultHttpExecutor extends AbstractHttpExecutor {

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
			Parameter[] parameters = userMethod.getParameters();
			if (parameters == null || parameters.length == 0) {
				return null;
			}
			Object[] args = new Object[parameters.length];
			for (int i = 0; i < parameters.length; i++) {
                ArgTypeToken token = checkArgType(parameters[i]);
                args[i] = convertArgType(token, parameters[i], context);
            }
			return args;
		} catch (Exception e) {
			throw new HttpRequestException(
					String.format("your request { %s } is not valid, error: %s", context.getRequest(), e.getMessage()),
					e, HttpResponseStatus.BAD_REQUEST
			);
		}
	}


}
