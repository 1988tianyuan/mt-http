package com.liugeng.mthttp.pojo;

import java.util.List;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaders;

public class HttpRequestEntity {

	public HttpRequestEntity() {
	}

	public HttpRequestEntity(Map<String, List<String>> queryParams, String path, String method,
		ByteBuf bodyBuf, Channel channel, HttpHeaders httpHeaders) {
		this.queryParams = queryParams;
		this.path = path;
		this.method = method;
		this.bodyBuf = bodyBuf;
		this.channel = channel;
		this.httpHeaders = httpHeaders;
	}

	private Map<String, List<String>> queryParams;

	private String path;

	private String method;

	private ByteBuf bodyBuf;

	private Channel channel;

	private HttpHeaders httpHeaders;

	public HttpHeaders getHttpHeaders() {
		return httpHeaders;
	}

	public void setHttpHeaders(HttpHeaders httpHeaders) {
		this.httpHeaders = httpHeaders;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Map<String, List<String>> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(Map<String, List<String>> queryParams) {
		this.queryParams = queryParams;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public ByteBuf getBodyBuf() {
		return bodyBuf;
	}

	public void setBodyBuf(ByteBuf bodyBuf) {
		this.bodyBuf = bodyBuf;
	}

	@Override
	public String toString() {
		return "HttpRequestEntity{" +
			"queryParams=" + queryParams +
			", path='" + path + '\'' +
			", method='" + method + '\'' +
			", bodyBuf=" + bodyBuf +
			", channel=" + channel +
			", httpHeaders=" + httpHeaders +
			'}';
	}

	public Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Map<String, List<String>> queryParams;
		private String path;
		private String method;
		private ByteBuf bodyBuf;
		private Channel channel;
		private HttpHeaders httpHeaders;

		public Builder queryParams (Map<String, List<String>> queryParams) {
			this.queryParams = queryParams;
			return this;
		}

		public Builder path (String path) {
			this.path = path;
			return this;
		}

		public Builder method (String method) {
			this.method = method;
			return this;
		}

		public Builder bodyBuf (ByteBuf bodyBuf) {
			this.bodyBuf = bodyBuf;
			return this;
		}

		public Builder channel (Channel channel) {
			this.channel = channel;
			return this;
		}

		public Builder httpHeaders (HttpHeaders httpHeaders) {
			this.httpHeaders = httpHeaders;
			return this;
		}

		public HttpRequestEntity build() {
			return new HttpRequestEntity(queryParams, path, method, bodyBuf, channel, httpHeaders);
		}
	}
}
