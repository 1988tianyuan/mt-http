package com.liugeng.mthttp.exception;

import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpRequestException extends RuntimeException {

	private HttpResponseStatus httpStatus;

	public HttpRequestException(String message, HttpResponseStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public HttpRequestException(String message, Throwable cause, HttpResponseStatus httpStatus) {
		super(message, cause);
		this.httpStatus = httpStatus;
	}

	public HttpResponseStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpResponseStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
}
