package com.liugeng.mthttp.router;

import java.nio.charset.Charset;

import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.pojo.HttpResponseEntity;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class DefaultHttpExecutor implements HttpExecutor {

	@Override
	public void execute(ChannelHandlerContext context, HttpRequestEntity requestEntity) {
		HttpResponseEntity responseEntity = new HttpResponseEntity();
		String method = requestEntity.getMethod();
		switch (method) {
			case "GET":
				doGet(requestEntity, responseEntity);
				break;
			case "POST":
				doPost(requestEntity, responseEntity);
				break;
			case "PUT":
				doPut(requestEntity, responseEntity);
				break;
			case "DELETE":
				doDelete(requestEntity, responseEntity);
				break;
			default:
				return;
		}
		HttpHeaders headers = responseEntity.getResponseHeaders();
		headers.set(HttpHeaderNames.CONTENT_LENGTH, responseEntity.getBodyBuf().readableBytes());

		FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
			responseEntity.getResponseStatus(), responseEntity.getBodyBuf(), headers, headers);
		context.channel().writeAndFlush(fullHttpResponse);
	}

	protected void doGet(HttpRequestEntity requestEntity, HttpResponseEntity responseEntity) {
		System.out.println("GET请求: " + requestEntity);
		ByteBuf repBodyBuffer = requestEntity.getChannel().alloc().ioBuffer();
		repBodyBuffer.writeBytes("哈哈哈哈哈！".getBytes(Charset.defaultCharset()));
		responseEntity.setBodyBuf(repBodyBuffer);
	}

	protected void doPost(HttpRequestEntity requestEntity, HttpResponseEntity responseEntity) {
		ByteBuf reqBodyBuffer = requestEntity.getBodyBuf();
		HttpHeaders headers = requestEntity.getHttpHeaders();
		byte[] body = new byte[Integer.valueOf(headers.get(HttpHeaderNames.CONTENT_LENGTH))];
		reqBodyBuffer.readBytes(body);
		String bodyStr = new String(body, Charset.defaultCharset());
		System.out.println("POST请求：" + bodyStr);
		ByteBuf repBodyBuffer = requestEntity.getChannel().alloc().ioBuffer();
		repBodyBuffer.writeBytes("我收到啦！".getBytes(Charset.defaultCharset()));
		responseEntity.setBodyBuf(repBodyBuffer);
	}

	protected void doPut(HttpRequestEntity requestEntity, HttpResponseEntity responseEntity) {

	}

	protected void doDelete(HttpRequestEntity requestEntity, HttpResponseEntity responseEntity) {

	}
}
