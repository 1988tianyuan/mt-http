package com.liugeng.mthttp.router;

import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.pojo.HttpSession;

public interface ConnectContext {

	HttpRequestEntity getRequest();

	HttpResponseEntity getResponse();

	void setRequest(HttpRequestEntity request);

	void setResponse(HttpResponseEntity response);

	HttpMethod getHttpMethod();

	void setHttpMethod(HttpMethod httpMethod);

	Cookies getRequestCookies();

	void setRequestCookies(Cookies requestCookies);

	void addRequestCookies(Cookies cookies);

	void addResponseCookies(Cookies cookies);

	Cookies getResponseCookies();

	void setResponseCookies(Cookies requestCookies);

	HttpSession getSession();

	void setSession(HttpSession session);
}

