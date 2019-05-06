package com.liugeng.mthttp.router.resovler;

import java.util.HashMap;
import java.util.Map;

import com.liugeng.mthttp.exception.HttpRequestException;
import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.resovler.serialization.ApplicationStrategy;
import com.liugeng.mthttp.router.resovler.serialization.SerializationStrategy;
import com.liugeng.mthttp.router.resovler.serialization.TextStrategy;
import com.liugeng.mthttp.utils.io.Resource;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.commons.lang3.ClassUtils;

public class ResourceResolver extends AbstractResponseResolver {

	@Override
	public Map<String, SerializationStrategy> initSerialStrategyMap() {
		Map<String, SerializationStrategy> serialStrategyMap = new HashMap<>();
		serialStrategyMap.put("application", new ApplicationStrategy());
		return serialStrategyMap;
	}

	@Override
	public void resolve(Object resource, ConnectContext context, HttpResponseStatus status) {
		SerializationStrategy strategy = getStrategy(context);
		try {
			ByteBuf byteBuf = strategy.serialize(resource, getCharset(context.getResponse().getResponseHeaders()));
			HttpResponseEntity responseEntity = context.getResponse();
			HttpHeaders headers = responseEntity.getResponseHeaders();
			headers.set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
			responseEntity.setBodyBuf(byteBuf);
			responseEntity.setResponseStatus(status);
			FullHttpResponse fullHttpResponse = createFullResponse(responseEntity);
			Channel channel = context.getRequest().getChannel();
			channel.writeAndFlush(fullHttpResponse);
			checkKeepAlive(context.getRequest().getHttpHeaders(), channel);
		} catch (Exception e) {
			throw new HttpRequestException("error during resolve the resource: " + context.getRequest().getPath(), e, HttpResponseStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public boolean supportReturnValue(Object returnValue) {
		return returnValue != null && ClassUtils.isAssignable(returnValue.getClass(), Resource.class);
	}
}
