package com.liugeng.mthttp.router;

import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.pojo.HttpResponseEntity;

public interface ConnectContext {

	HttpRequestEntity getRequest();

	HttpResponseEntity getResponse();

	void setRequest(HttpRequestEntity request);

	void setResponse(HttpResponseEntity response);

	HttpMethod getHttpMethod();

	void setHttpMethod(HttpMethod httpMethod);

	Cookies getRequestCookies();

	void setRequestCookies(Cookies requestCookies);

	void addCookies(Cookies cookies);

}

