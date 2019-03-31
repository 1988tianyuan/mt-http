package com.liugeng.mthttp.router.executor;

import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.router.ConnectContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

public interface HttpExecutor {

	void execute(ConnectContext context) throws Exception;
}
