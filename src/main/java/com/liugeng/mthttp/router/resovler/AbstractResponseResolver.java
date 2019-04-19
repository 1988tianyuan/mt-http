package com.liugeng.mthttp.router.resovler;

import static com.google.common.net.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpHeaderValues.*;

import com.liugeng.mthttp.exception.HttpRequestException;
import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.pojo.HttpResponseEntity;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.resovler.serialization.ApplicationStrategy;
import com.liugeng.mthttp.router.resovler.serialization.JsonStrategy;
import com.liugeng.mthttp.router.resovler.serialization.SerializationStrategy;
import com.liugeng.mthttp.router.resovler.serialization.StaticeFileStrategy;
import com.liugeng.mthttp.router.resovler.serialization.TextStrategy;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractResponseResolver implements HttpResponseResolver {

    protected final Map<String, SerializationStrategy> streamSerialStrategyMap;
    protected final Map<String, SerializationStrategy> objectSerialStrategyMap;

    public AbstractResponseResolver() {
        streamSerialStrategyMap = new HashMap<>();
        objectSerialStrategyMap = new HashMap<>();
        objectSerialStrategyMap.put(APPLICATION_JSON.toString(), new JsonStrategy());
        objectSerialStrategyMap.put("text", new TextStrategy());
        streamSerialStrategyMap.put("application", new ApplicationStrategy());
        streamSerialStrategyMap.put("*", new StaticeFileStrategy());
    }

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

    public void resolve(Object returnValue, ConnectContext context, HttpResponseStatus status) {
        HttpHeaders requestHeaders = context.getRequest().getHttpHeaders();
        HttpHeaders responseHeaders = context.getResponse().getResponseHeaders();
        SerializationStrategy strategy = genSerialStrategy(returnValue, requestHeaders.get(ACCEPT), responseHeaders.get(CONTENT_TYPE));
        try {
            ByteBuf byteBuf = strategy.serialize(returnValue, getCharset(responseHeaders));
            resolveByteBuf(byteBuf, context, status);
        } catch (Exception e) {
            throw new HttpRequestException("error during resolve the resource: " + context.getRequest().getPath(), e, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Charset getCharset(HttpHeaders responseHeaders) {
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

    protected SerializationStrategy genSerialStrategy(Object returnValue, String acceptStr, String contentType) {
        boolean isStream = ClassUtils.isAssignable(returnValue.getClass(), InputStream.class);
        if (isStream) {
            return getStrategy(streamSerialStrategyMap, acceptStr, contentType);
        } else {
            return getStrategy(objectSerialStrategyMap, acceptStr, contentType);
        }
    }

    private SerializationStrategy getStrategy(Map<String, SerializationStrategy> strategyMap, String acceptStr, String contentType) {
        SerializationStrategy bestStrategy = null;
        if (StringUtils.isNotBlank(contentType)) {
            bestStrategy = getStrategyByMediaType(strategyMap, contentType);
        }
        if (bestStrategy == null) {
            if (StringUtils.isNotBlank(acceptStr)) {
                String[] accepts = StringUtils.split(StringUtils.replace(acceptStr, " ", ""), ",");
                for (String accept : accepts) {
                    bestStrategy = getStrategyByMediaType(strategyMap, accept);
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

    private SerializationStrategy getStrategyByMediaType(Map<String, SerializationStrategy> strategyMap, String contentType) {
        SerializationStrategy bestStrategy = strategyMap.get(contentType);
        if (bestStrategy == null) {
            return strategyMap.get(getMainMediaType(contentType));
        }
        return bestStrategy;
    }

    private SerializationStrategy getDefaultStrategy() {
        return objectSerialStrategyMap.get("text");
    }

    private String getMainMediaType(String mediaType) {
        String mainAccept = StringUtils.split(mediaType, "/")[0];
        if (StringUtils.isNotBlank(mainAccept)) {
            return mainAccept;
        } else {
            return "*";
        }
    }

    protected abstract void resolveByteBuf(ByteBuf byteBuf, ConnectContext context, HttpResponseStatus status);
}
