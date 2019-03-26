package com.liugeng.mthttp.pojo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.liugeng.mthttp.constant.HttpMethod;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaders;

public class HttpRequestEntity {

	public HttpRequestEntity() {
	}

	public HttpRequestEntity(Map<String, List<String>> queryParams, String path, HttpMethod method,
		ByteBuf bodyBuf, Channel channel, HttpHeaders httpHeaders) {
		this.queryParams = queryParams;
		this.path = path;
		this.method = method;
		this.bodyBuf = bodyBuf;
		this.channel = channel;
		this.httpHeaders = httpHeaders;
		this.attributes = new HashMap<>();
	}

	private Map<String, List<String>> queryParams;

	private String path;

	private HttpMethod method;

	private ByteBuf bodyBuf;

	private Channel channel;

	private HttpHeaders httpHeaders;

	private Map<String, Object> attributes;

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttribute(String key, Object value) {
		if (this.attributes == null ) {
			this.attributes = new HashMap<>();
		}
		attributes.put(key, value);
	}

	public Object getAttribute(String key) {
		if (attributes == null) {
			throw new NullPointerException("attributes in requestEntity is not initialized !");
		}
		return attributes.get(key);
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

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

	public HttpMethod getMethod() {
		return method;
	}

	public void setMethod(HttpMethod method) {
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
		private HttpMethod method;
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

		public Builder method (HttpMethod method) {
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
