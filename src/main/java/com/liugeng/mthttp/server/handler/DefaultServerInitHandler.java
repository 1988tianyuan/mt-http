package com.liugeng.mthttp.server.handler;

import com.liugeng.mthttp.pojo.HttpRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

import static com.liugeng.mthttp.protocal.HttpRequestCodec.decode;

@Slf4j
public class DefaultServerInitHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf) msg;
		HttpRequest httpRequest = decode(byteBuf);
		System.out.printf("http request:" + httpRequest);
		ByteBuf outerBuf = ctx.alloc().ioBuffer();
		outerBuf.writeBytes("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\ncontent-length: 5\r\n\r\nhello".getBytes(Charset.defaultCharset()));
		ctx.channel().writeAndFlush(outerBuf);
	}
}
