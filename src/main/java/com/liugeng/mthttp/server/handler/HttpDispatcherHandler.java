package com.liugeng.mthttp.server.handler;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpUtil.*;

import java.nio.charset.Charset;
import java.util.Map;

import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.support.HttpConnectContext;
import com.liugeng.mthttp.router.HttpExecutor;
import com.liugeng.mthttp.router.support.HttpExecutorMappingInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

public class HttpDispatcherHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private final Map<HttpExecutorMappingInfo, HttpExecutor> executorMap;

	public HttpDispatcherHandler(Map<HttpExecutorMappingInfo, HttpExecutor> executorMap) {
		this.executorMap = executorMap;
	}

	public HttpExecutor getExecutor(HttpExecutorMappingInfo mappingInfo) {
		return this.executorMap.get(mappingInfo);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext handlerContext, FullHttpRequest request) throws Exception {
		System.out.println("当前dispatcher线程：" + Thread.currentThread().getName());
		Charset charset = getCharset(request, Charset.defaultCharset());
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri(), charset);
		HttpMethod httpMethod = HttpMethod.valueOf(request.method().name());
		HttpRequestEntity requestEntity = new HttpRequestEntity.Builder()
			.method(httpMethod).path(queryStringDecoder.path())
			.queryParams(queryStringDecoder.parameters()).bodyBuf(request.content())
			.channel(handlerContext.channel()).httpHeaders(request.headers())
			.build();
		ConnectContext connectContext = new HttpConnectContext(requestEntity);
		dispatch(connectContext);
		handlerContext.channel().writeAndFlush(createFullResponse(connectContext));
	}

	private void dispatch(ConnectContext connectContext) throws Exception {
		HttpRequestEntity requestEntity = connectContext.getRequest();
		HttpExecutorMappingInfo mappingInfo = getMappingInfo(requestEntity);
		HttpExecutor executor = getExecutor(mappingInfo);
		executor.execute(connectContext);
	}

	private HttpExecutorMappingInfo getMappingInfo(HttpRequestEntity requestEntity) {
		String path = requestEntity.getPath();
		return new HttpExecutorMappingInfo(path, requestEntity.getMethod());
	}

	private FullHttpResponse createFullResponse(ConnectContext connectContext) {
		HttpResponseEntity responseEntity = connectContext.getResponse();
		HttpHeaders headers = responseEntity.getResponseHeaders();
		if (headers.get(HttpHeaderNames.CONTENT_LENGTH) == null) {
			headers.set(HttpHeaderNames.CONTENT_LENGTH, responseEntity.getBodyBuf().readableBytes());
		}
		return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
			responseEntity.getResponseStatus(), responseEntity.getBodyBuf(), headers, headers);
	}

}
