package com.liugeng.mthttp.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class MantianHttpInitHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		System.out.println("当前channel的线程：" + Thread.currentThread().getName());
		request.retain();
		ctx.fireChannelRead(request);
	}
}
