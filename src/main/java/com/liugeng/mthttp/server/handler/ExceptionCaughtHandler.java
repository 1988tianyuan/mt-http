package com.liugeng.mthttp.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liugeng.mthttp.exception.HttpRequestException;
import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.resovler.HttpResponseResolver;
import com.liugeng.mthttp.router.HttpConnectContext;
import com.liugeng.mthttp.router.resovler.TextPlainResponseResolver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

@ChannelHandler.Sharable
public class ExceptionCaughtHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ExceptionCaughtHandler.class);

    public static final ExceptionCaughtHandler INSTANCE = new ExceptionCaughtHandler();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(ctx.channel().id() + "出现异常：" + cause, cause);
        ConnectContext connectContext = new HttpConnectContext(createTmpRequest(ctx.channel()));
        HttpResponseResolver responseResolver = new TextPlainResponseResolver();
        try {
            if (cause instanceof HttpRequestException) {
                HttpRequestException hrException = (HttpRequestException) cause;
                responseResolver.resolve(cause.getMessage(), connectContext, hrException.getHttpStatus());
            } else {
                responseResolver.resolve("inner server error: " + cause.getMessage(), connectContext,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {

        }
        ctx.channel().close();
    }

    private HttpRequestEntity createTmpRequest(Channel channel) {
        return new HttpRequestEntity()
            .builder()
            .channel(channel)
            .httpHeaders(new DefaultHttpHeaders())
            .build();
    }
}
