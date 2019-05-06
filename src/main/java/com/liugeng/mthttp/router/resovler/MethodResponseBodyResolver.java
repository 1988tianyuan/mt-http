package com.liugeng.mthttp.router.resovler;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.liugeng.mthttp.exception.HttpRequestException;
import org.apache.commons.lang3.ClassUtils;

import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.resovler.serialization.JsonStrategy;
import com.liugeng.mthttp.router.resovler.serialization.SerializationStrategy;
import com.liugeng.mthttp.router.resovler.serialization.TextStrategy;
import com.liugeng.mthttp.utils.io.Resource;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

public class MethodResponseBodyResolver extends AbstractResponseResolver {

	@Override
	public Map<String, SerializationStrategy> initSerialStrategyMap() {
		Map<String, SerializationStrategy> serialStrategyMap = new HashMap<>();
		TextStrategy textStrategy = new TextStrategy();
		serialStrategyMap.put("text/*", textStrategy);
		JsonStrategy jsonStrategy = new JsonStrategy();
		serialStrategyMap.put("application/json", jsonStrategy);
		serialStrategyMap.put("application/*+json", jsonStrategy);
		return serialStrategyMap;
	}

	@Override
	public void resolve(Object response, ConnectContext context, HttpResponseStatus status) {
		try {
			HttpResponseEntity responseEntity = context.getResponse();
			HttpHeaders headers = responseEntity.getResponseHeaders();
			SerializationStrategy strategy = getStrategy(context);
			ByteBuf byteBuf = strategy.serialize(response, getCharset(headers));
			headers.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN + "; " + HttpHeaderValues.CHARSET + "=" + Charset.forName("utf-8"));
			headers.set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
			responseEntity.setBodyBuf(byteBuf);
			responseEntity.setResponseStatus(status);
			FullHttpResponse fullHttpResponse = createFullResponse(responseEntity);
			Channel channel = context.getRequest().getChannel();
			channel.writeAndFlush(fullHttpResponse);
			checkKeepAlive(context.getRequest().getHttpHeaders(), channel);
		} catch (Exception e) {
			throw new HttpRequestException("error during resolve the http response, url: " + context.getRequest().getPath(), e, HttpResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public boolean supportReturnValue(Object returnValue) {
		if (returnValue == null) return true;
		return !ClassUtils.isAssignable(returnValue.getClass(), Resource.class);
	}

}
