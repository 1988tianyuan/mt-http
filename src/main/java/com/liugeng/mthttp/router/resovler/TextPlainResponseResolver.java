package com.liugeng.mthttp.router.resovler;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.*;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.pojo.HttpSession;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.resovler.serialization.SerializationStrategy;
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
	protected void resolveByteBuf(ByteBuf byteBuf, ConnectContext context, HttpResponseStatus status) {
		HttpResponseEntity responseEntity = context.getResponse();
		HttpHeaders headers = responseEntity.getResponseHeaders();
		headers.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN + "; " + HttpHeaderValues.CHARSET + "=" + Charset.forName("utf-8"));
		headers.set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
		responseEntity.setBodyBuf(byteBuf);
		responseEntity.setResponseStatus(status);
		FullHttpResponse fullHttpResponse = createFullResponse(responseEntity);
		Channel channel = context.getRequest().getChannel();
		channel.writeAndFlush(fullHttpResponse);
		checkKeepAlive(context.getRequest().getHttpHeaders(), channel);
	}

}
