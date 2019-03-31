package com.liugeng.mthttp.router;

import java.util.Objects;

import com.liugeng.mthttp.constant.HttpMethod;

public class HttpExecutorMappingInfo {

	public HttpExecutorMappingInfo(String path, HttpMethod method) {
		this.path = path;
		this.method = method;
	}

	private String path;

	private HttpMethod method;

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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		HttpExecutorMappingInfo that = (HttpExecutorMappingInfo)o;
		return Objects.equals(path, that.path) &&
			method == that.method;
	}

	@Override
	public int hashCode() {
		return Objects.hash(path, method);
	}
}
