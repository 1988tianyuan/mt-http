package com.liugeng.mthttp.router;

import static io.netty.handler.codec.http.HttpUtil.*;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.pojo.HttpResponseEntity;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

public class HttpDispatcher {

	private final Map<String, HttpExecutor> executorMap;

	public HttpDispatcher(Map<String, HttpExecutor> executorMap) {
		this.executorMap = executorMap;
	}

	public HttpExecutor getExecutor(String path) {
		return this.executorMap.get(path);
	}

	public void dispatch(ChannelHandlerContext ctx, FullHttpRequest request) {
		Charset charset = getCharset(request, Charset.defaultCharset());
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri(), charset);
		HttpRequestEntity requestEntity = new HttpRequestEntity.Builder()
			.method(request.method().name()).path(queryStringDecoder.path())
			.queryParams(queryStringDecoder.parameters()).bodyBuf(request.content())
			.channel(ctx.channel()).httpHeaders(request.headers())
			.build();

		HttpExecutor executor = getExecutor(queryStringDecoder.path());
		executor.execute(ctx, requestEntity);
	}


}
