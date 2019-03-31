package com.liugeng.mthttp.router.resovler;

import java.nio.charset.Charset;

import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.pojo.HttpSession;
import com.liugeng.mthttp.router.ConnectContext;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

public class TextPlainResponseResolver extends AbstractResponseResolver {

	@Override
	public void resolve(Object returnValue, ConnectContext context, HttpResponseStatus status) {
		byte[] responseBytes = returnValue.toString().getBytes(Charset.defaultCharset());
		ByteBuf byteBuf = Unpooled.buffer(responseBytes.length);
		byteBuf.writeBytes(responseBytes);
		HttpResponseEntity responseEntity = context.getResponse();
		HttpHeaders headers = responseEntity.getResponseHeaders();
		headers.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN + "; " + HttpHeaderValues.CHARSET + "=" + Charset.forName("utf-8"));
		headers.set(HttpHeaderNames.CONTENT_LENGTH, responseBytes.length);
		responseEntity.setBodyBuf(byteBuf);
		responseEntity.setResponseStatus(status);
		FullHttpResponse fullHttpResponse = createFullResponse(responseEntity);
		Channel channel = context.getRequest().getChannel();
		channel.writeAndFlush(fullHttpResponse);
		checkKeepAlive(context.getRequest().getHttpHeaders(), channel);
	}


}
