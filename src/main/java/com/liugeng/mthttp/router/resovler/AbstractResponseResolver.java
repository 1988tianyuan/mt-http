package com.liugeng.mthttp.router.resovler;

import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.pojo.HttpSession;
import com.liugeng.mthttp.router.ConnectContext;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.CookieDecoder;
import io.netty.handler.codec.http.cookie.CookieEncoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import java.util.List;

public abstract class AbstractResponseResolver implements HttpResponseResolver {

    protected FullHttpResponse createFullResponse(HttpResponseEntity responseEntity) {
        HttpHeaders headers = setCookieIfNecessary(responseEntity);
        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                responseEntity.getResponseStatus(), responseEntity.getBodyBuf());
        response.headers();
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                responseEntity.getResponseStatus(), responseEntity.getBodyBuf(), headers, headers);
    }

    private HttpHeaders setCookieIfNecessary(HttpResponseEntity responseEntity) {
        Cookies cookies = responseEntity.getCookies();
        HttpHeaders httpHeaders = responseEntity.getResponseHeaders();
        if (cookies != null && !cookies.getCookieMap().isEmpty()) {
            for (Cookie cookie : cookies.getCookieMap().values()) {
                httpHeaders.add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
            }
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
