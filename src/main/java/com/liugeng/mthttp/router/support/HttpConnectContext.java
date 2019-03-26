package com.liugeng.mthttp.router.support;

import static io.netty.handler.codec.http.HttpHeaderNames.*;

import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.router.ConnectContext;

public class HttpConnectContext implements ConnectContext {

	private HttpRequestEntity request;

	private HttpResponseEntity response;

	private HttpMethod httpMethod;

	private String accept;


	public HttpConnectContext(HttpRequestEntity request) {
		this.request = request;
		this.response = new HttpResponseEntity();
		this.accept = request.getHttpHeaders().get(ACCEPT);
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
}
