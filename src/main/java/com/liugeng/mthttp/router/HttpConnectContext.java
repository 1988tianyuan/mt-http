package com.liugeng.mthttp.router;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.router.ConnectContext;

import java.awt.font.TextHitInfo;

public class HttpConnectContext implements ConnectContext {

	private HttpRequestEntity request;

	private HttpResponseEntity response;

	private HttpMethod httpMethod;

	private Cookies requestCookies;

	private Cookies responseCookies;

	public HttpConnectContext(HttpRequestEntity request) {
		this.request = request;
		this.response = new HttpResponseEntity();
		this.requestCookies = new Cookies(Sets.newHashSet());
		this.responseCookies = new Cookies(Sets.newHashSet());
		this.response.setCookies(responseCookies);
	}

	@Override
	public void addRequestCookies(Cookies cookies) {
		requestCookies.addCookies(cookies);
	}

	@Override
	public void addResponseCookies(Cookies cookies) {
		responseCookies.addCookies(cookies);
	}

	@Override
	public Cookies getResponseCookies() {
		return responseCookies;
	}

	@Override
	public void setResponseCookies(Cookies responseCookies) {
		this.responseCookies = responseCookies;
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
