package com.liugeng.mthttp.router.support;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.router.ConnectContext;

public class HttpConnectContext implements ConnectContext {

	private HttpRequestEntity request;

	private HttpResponseEntity response;

	private HttpMethod httpMethod;

	private Cookies requestCookies;

	public HttpConnectContext(HttpRequestEntity request) {
		this.request = request;
		this.response = new HttpResponseEntity();
		this.requestCookies = new Cookies(Sets.newHashSet());
	}

	@Override
	public void addCookies(Cookies cookies) {
		if (requestCookies == null) {
			setRequestCookies(cookies);
		} else {
			requestCookies.addCookies(cookies);
		}
	}

	@Override
	public void setRequest(HttpRequestEntity request) {
		this.request = request;
	}

	@Override
	public void setResponse(HttpResponseEntity response) {
		this.response = response;
	}

	@Override
	public HttpRequestEntity getRequest() {
		return request;
	}

	@Override
	public HttpResponseEntity getResponse() {
		return response;
	}

	@Override
	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	@Override
	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	@Override
	public Cookies getRequestCookies() {
		return requestCookies;
	}

	@Override
	public void setRequestCookies(Cookies requestCookies) {
		this.requestCookies = requestCookies;
	}


}
