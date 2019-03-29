package com.liugeng.mthttp.pojo;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.CombinedHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpStatusClass;

public class HttpResponseEntity {

	private ByteBuf bodyBuf;

	private HttpResponseStatus responseStatus = HttpResponseStatus.OK;

	// default headers
	private HttpHeaders responseHeaders = new CombinedHttpHeaders(true);

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
}
