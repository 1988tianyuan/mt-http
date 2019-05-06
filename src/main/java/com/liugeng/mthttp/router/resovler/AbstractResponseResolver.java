package com.liugeng.mthttp.router.resovler;

import static com.google.common.net.HttpHeaders.*;

import com.liugeng.mthttp.exception.HttpRequestException;
import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.resovler.serialization.SerializationStrategy;
import com.liugeng.mthttp.utils.io.Resource;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractResponseResolver implements HttpResponseResolver {

    protected final Map<String, SerializationStrategy> serialStrategyMap;

    public AbstractResponseResolver() {
        this.serialStrategyMap = initSerialStrategyMap();
    }

    public abstract Map<String, SerializationStrategy> initSerialStrategyMap();

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

    protected Charset getCharset(HttpHeaders responseHeaders) {
        String contentTypeStr = responseHeaders.get(HttpHeaderNames.CONTENT_TYPE);
        String charsetStr;
        if (contentTypeStr != null && contentTypeStr.contains("charset=")) {
             int beginIndex = StringUtils.indexOfIgnoreCase(contentTypeStr, "charset=") + 8;
             charsetStr = StringUtils.substring(contentTypeStr, beginIndex);
            try {
                return Charset.forName(charsetStr);
            } catch (UnsupportedCharsetException e) {
                // do nothing
            }
        }
        return Charset.defaultCharset();
    }

    protected SerializationStrategy getStrategy(ConnectContext context) {
        HttpHeaders requestHeaders = context.getRequest().getHttpHeaders();
        HttpHeaders responseHeaders = context.getResponse().getResponseHeaders();
        String acceptStr = requestHeaders.get(ACCEPT);
        String contentType = responseHeaders.get(CONTENT_TYPE);
        SerializationStrategy bestStrategy = null;
        if (StringUtils.isNotBlank(contentType)) {
            bestStrategy = getStrategyByMediaType(contentType);
        }
        if (bestStrategy == null) {
            if (StringUtils.isNotBlank(acceptStr)) {
                String[] accepts = StringUtils.split(StringUtils.replace(acceptStr, " ", ""), ",");
                for (String accept : accepts) {
                    bestStrategy = getStrategyByMediaType(accept);
                    if (bestStrategy != null) {
                        return bestStrategy;
                    }
                }
            }
        }
        if (bestStrategy != null) {
            return bestStrategy;
        }
        return getDefaultStrategy();
    }

    private SerializationStrategy getStrategyByMediaType(String contentType) {
        SerializationStrategy bestStrategy = serialStrategyMap.get(contentType);
        if (bestStrategy == null) {
            return serialStrategyMap.get(getMainMediaType(contentType));
        }
        return bestStrategy;
    }

    private SerializationStrategy getDefaultStrategy() {
        return serialStrategyMap.get("text/*");
    }

    private String getMainMediaType(String mediaType) {
        String mainAccept = StringUtils.split(mediaType, "/")[0];
        if (StringUtils.isNotBlank(mainAccept)) {
            return mainAccept;
        } else {
            return "*";
        }
    }
}
