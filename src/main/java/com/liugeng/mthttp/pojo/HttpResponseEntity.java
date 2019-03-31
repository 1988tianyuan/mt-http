package com.liugeng.mthttp.pojo;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;

public class HttpResponseEntity {

	private ByteBuf bodyBuf;

	private HttpResponseStatus responseStatus = HttpResponseStatus.OK;

	private Cookies cookies;

	// default headers
	private HttpHeaders responseHeaders = new DefaultHttpHeaders();

	public ByteBuf getBodyBuf() {
		return bodyBuf;
	}

	public void setBodyBuf(ByteBuf bodyBuf) {
		this.bodyBuf = bodyBuf;
	}

	public HttpResponseStatus getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(HttpResponseStatus responseStatus) {
		this.responseStatus = responseStatus;
	}

	public HttpHeaders getResponseHeaders() {
		return responseHeaders;
	}
	public void setResponseHeaders(HttpHeaders responseHeaders) {
		this.responseHeaders = responseHeaders;
	}


	public Cookies getCookies() {
		return cookies;
	}

	public void setCookies(Cookies cookies) {
		this.cookies = cookies;
	}
}
