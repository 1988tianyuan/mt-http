package com.liugeng.mthttp.router;

import com.liugeng.mthttp.pojo.HttpRequestEntity;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

public interface HttpExecutor {

	void execute(ChannelHandlerContext context, HttpRequestEntity requestEntity);
}
