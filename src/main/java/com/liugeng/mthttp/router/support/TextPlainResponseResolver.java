package com.liugeng.mthttp.router.support;

import java.nio.charset.Charset;

import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.HttpResponseResolver;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.CombinedHttpHeaders;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class TextPlainResponseResolver implements HttpResponseResolver {

	@Override
	public void resolve(Object returnValue, ConnectContext context, HttpResponseStatus status) {
		byte[] responseBytes = returnValue.toString().getBytes(Charset.defaultCharset());
		ByteBuf byteBuf = Unpooled.buffer(responseBytes.length);
		byteBuf.writeBytes(responseBytes);
		HttpResponseEntity responseEntity = context.getResponse();
		HttpHeaders headers = responseEntity.getResponseHeaders();
		headers.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
		headers.set(HttpHeaderNames.CONTENT_LENGTH, responseBytes.length);
		responseEntity.setBodyBuf(byteBuf);
		responseEntity.setResponseStatus(status);
		FullHttpResponse fullHttpResponse = createFullResponse(context);
		Channel channel = context.getRequest().getChannel();
		channel.writeAndFlush(fullHttpResponse);
		checkKeepAlive(context.getRequest().getHttpHeaders(), channel);
	}

	private FullHttpResponse createFullResponse(ConnectContext connectContext) {
		HttpResponseEntity responseEntity = connectContext.getResponse();
		HttpHeaders headers = responseEntity.getResponseHeaders();
		return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
			responseEntity.getResponseStatus(), responseEntity.getBodyBuf(), headers, headers);
	}

	private void checkKeepAlive(HttpHeaders httpHeaders, Channel channel) {
		if (!(HttpHeaderValues.KEEP_ALIVE.toString().equals(httpHeaders.get(HttpHeaderNames.CONNECTION)))) {
			channel.close();
		}
	}
}
