package com.liugeng.mthttp.router;

import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.pojo.HttpResponseEntity;

public interface ConnectContext {

	HttpRequestEntity getRequest();

	HttpResponseEntity getResponse();

	void setRequest(HttpRequestEntity request);

	void setResponse(HttpResponseEntity response);

}

