package com.liugeng.mthttp.router.executor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.liugeng.mthttp.constant.StringConstants;
import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.pojo.HttpSession;
import com.liugeng.mthttp.router.ExecutedMethodWrapper;
import com.liugeng.mthttp.router.resovler.MethodResponseBodyResolver;

import com.liugeng.mthttp.exception.HttpRequestException;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.resovler.HttpResponseResolver;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;

public class DefaultMethodHttpExecutor extends AbstractMethodHttpExecutor {

	private ExecutedMethodWrapper userMethod;

	public DefaultMethodHttpExecutor(ExecutedMethodWrapper userMethod, MethodResponseBodyResolver resolver) {
		this.userMethod = userMethod;
		addResolver(resolver);
	}

	@Override
	public void execute(ConnectContext context) throws Exception {
		Object[] args = getMethodArgumentValues(context);
		Object returnValue = invokeUserMethod(args);
		createResponse(returnValue, context);
	}

	private void createResponse(Object returnValue, ConnectContext context) throws Exception {
		setSessionId(context.getSession(), context.getResponseCookies());
		HttpResponseResolver resolver = selectResolver(returnValue);
		resolver.resolve(returnValue, context, HttpResponseStatus.OK);
	}

	/**
	 * save current session id into response cookies
	 * @param session
	 * @param responseCookies
	 */
	private void setSessionId(HttpSession session, Cookies responseCookies) {
		String sessionId = session.getId();
		Cookie cookie = new DefaultCookie(StringConstants.SESSION_ID, sessionId);
		cookie.setMaxAge(config.getLong(DEFAULT_COOKIE_EXPIRE_TIME, 1800));
		cookie.setPath("/");
		cookie.setDomain(config.getString(SERVER_BIND_HOST));
		responseCookies.addCookie(cookie);
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
