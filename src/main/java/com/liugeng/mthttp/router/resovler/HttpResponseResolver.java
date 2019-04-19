package com.liugeng.mthttp.router.resovler;

import com.liugeng.mthttp.router.ConnectContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public interface HttpResponseResolver {

	void resolve(Object returnValue, ConnectContext context, HttpResponseStatus status) throws Exception;
}
