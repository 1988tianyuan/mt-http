package com.liugeng.mthttp.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liugeng.mthttp.exception.HttpRequestException;
import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.HttpResponseResolver;
import com.liugeng.mthttp.router.support.HttpConnectContext;
import com.liugeng.mthttp.router.support.TextPlainResponseResolver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
public class ExceptionCaughtHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ExceptionCaughtHandler.class);

    public static final ExceptionCaughtHandler INSTANCE = new ExceptionCaughtHandler();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(ctx.channel().id() + "出现异常：" + cause, cause);
        ConnectContext connectContext = new HttpConnectContext(createEmptyRequest(ctx.channel()));
        HttpResponseResolver responseResolver = new TextPlainResponseResolver();
        if (cause instanceof HttpRequestException) {
            HttpRequestException hrException = (HttpRequestException) cause;
            responseResolver.resolve(cause.getMessage(), connectContext, hrException.getHttpStatus());
        } else {
            responseResolver.resolve("inner server error: " + cause.getMessage(), connectContext,
                HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpRequestEntity createEmptyRequest(Channel channel) {
        return new HttpRequestEntity()
            .builder()
            .channel(channel)
            .build();
    }
}
