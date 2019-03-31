package com.liugeng.mthttp.router.resovler;

import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.router.ConnectContext;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;

public abstract class AbstractResponseResolver implements HttpResponseResolver {

    protected FullHttpResponse createFullResponse(HttpResponseEntity responseEntity) {
        HttpHeaders headers = setCookieIfNecessary(responseEntity);
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                responseEntity.getResponseStatus(), responseEntity.getBodyBuf(), headers, headers);
    }

    private HttpHeaders setCookieIfNecessary(HttpResponseEntity responseEntity) {
        Cookies cookies = responseEntity.getCookies();
        HttpHeaders httpHeaders = responseEntity.getResponseHeaders();
        if (cookies != null && !cookies.getCookieMap().isEmpty()) {
            httpHeaders.set(HttpHeaderNames.SET_COOKIE, cookies.getCookieMap().values());
        }
        return httpHeaders;
    }

    protected void checkKeepAlive(HttpHeaders httpHeaders, Channel channel) {
        if (!(HttpHeaderValues.KEEP_ALIVE.toString().equals(httpHeaders.get(HttpHeaderNames.CONNECTION)))) {
            channel.close();
        }
    }

    public abstract void resolve(Object returnValue, ConnectContext context, HttpResponseStatus status);
}
