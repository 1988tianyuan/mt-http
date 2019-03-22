package com.liugeng.mthttp.server.handler;

import java.nio.charset.Charset;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.CombinedHttpHeaders;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public class MantianHttpInitHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		HttpHeaders headers = request.headers();
		int contentLength = Integer.valueOf(headers.get("Content-length"));
		ByteBuf reqBodyBuffer = request.content();
		byte[] body = new byte[contentLength];
		reqBodyBuffer.readBytes(body);
		String bodyStr = new String(body, Charset.defaultCharset());
		System.out.println(bodyStr);

		HttpHeaders repHeaders = new CombinedHttpHeaders(true);
		ByteBuf repBodyBuffer = ctx.alloc().ioBuffer();
		repBodyBuffer.writeBytes("哈哈哈".getBytes(Charset.defaultCharset()));
		repHeaders.set(HttpHeaderNames.CONTENT_LENGTH, repBodyBuffer.readableBytes());
		repHeaders.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
		FullHttpResponse response = new DefaultFullHttpResponse(
			request.protocolVersion(), HttpResponseStatus.OK, repBodyBuffer, repHeaders, repHeaders);
		ctx.channel().writeAndFlush(response);
	}
}
