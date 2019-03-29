package com.liugeng.mthttp.server.handler;

import static io.netty.handler.codec.http.HttpUtil.*;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.support.HttpConnectContext;
import com.liugeng.mthttp.router.HttpExecutor;
import com.liugeng.mthttp.router.support.HttpExecutorMappingInfo;
import com.liugeng.mthttp.utils.Assert;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

@ChannelHandler.Sharable
public class HttpDispatcherHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private final Map<HttpExecutorMappingInfo, HttpExecutor> executorMap;

	public HttpDispatcherHandler(Map<HttpExecutorMappingInfo, HttpExecutor> executorMap) {
		this.executorMap = executorMap;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext handlerContext, FullHttpRequest request) throws Exception {
		HttpRequestEntity requestEntity= resolveRequest(handlerContext.channel(), request);
		ConnectContext connectContext = new HttpConnectContext(requestEntity);
		dispatch(connectContext);
	}

	private HttpRequestEntity resolveRequest(Channel channel, FullHttpRequest request) {
		Charset charset = getCharset(request, Charset.defaultCharset());
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri(), charset);
		HttpMethod httpMethod = HttpMethod.valueOf(request.method().name());
		return new HttpRequestEntity
			.Builder()
			.method(httpMethod).path(queryStringDecoder.path())
			.queryParams(queryStringDecoder.parameters()).bodyBuf(request.content())
			.channel(channel).httpHeaders(request.headers())
			.build();
	}

	private void dispatch(ConnectContext connectContext) throws Exception {
		HttpRequestEntity requestEntity = connectContext.getRequest();
		HttpExecutorMappingInfo mappingInfo = new HttpExecutorMappingInfo(requestEntity.getPath(), requestEntity.getMethod());
		HttpExecutor executor = getExecutor(mappingInfo);
		Assert.executorExists(executor, mappingInfo.getPath());
		handleCookies(connectContext);
		executor.execute(connectContext);
	}

	private HttpExecutor getExecutor(HttpExecutorMappingInfo mappingInfo) {
		return this.executorMap.get(mappingInfo);
	}

	private void handleCookies(ConnectContext connectContext) {
		HttpHeaders httpHeaders = connectContext.getRequest().getHttpHeaders();
		if (httpHeaders.contains("cookies")) {
			ServerCookieDecoder cookieDecoder = ServerCookieDecoder.STRICT;
			Set<Cookie> cookieSet = cookieDecoder.decode(httpHeaders.get(HttpHeaderNames.COOKIE));
			Cookies cookies = new Cookies(cookieSet);
			connectContext.addCookies(cookies);
		}
	}

}
