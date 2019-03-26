package com.liugeng.mthttp.router;

import com.liugeng.mthttp.router.ConnectContext;
import io.netty.handler.codec.http.FullHttpResponse;

public interface HttpResponseResolver {

	void resolve(Object returnValue, ConnectContext context);
}
