package com.liugeng.mthttp.router.resovler;

import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.router.ConnectContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

public class ApplicationResolver extends AbstractResponseResolver {

	@Override
	protected void resolveByteBuf(ByteBuf byteBuf, ConnectContext context, HttpResponseStatus status) {
		HttpResponseEntity responseEntity = context.getResponse();
		HttpHeaders headers = responseEntity.getResponseHeaders();
		headers.set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
		responseEntity.setBodyBuf(byteBuf);
		responseEntity.setResponseStatus(status);
		FullHttpResponse fullHttpResponse = createFullResponse(responseEntity);
		Channel channel = context.getRequest().getChannel();
		channel.writeAndFlush(fullHttpResponse);
		checkKeepAlive(context.getRequest().getHttpHeaders(), channel);
	}
}
