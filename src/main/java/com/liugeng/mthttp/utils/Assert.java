package com.liugeng.mthttp.utils;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;

import com.liugeng.mthttp.exception.HttpRequestException;
import com.liugeng.mthttp.router.executor.HttpExecutor;
import io.netty.handler.codec.http.HttpResponseStatus;

public class Assert {
    public static void notNull(Object object, String msg){
        if(object == null){
            throw new NullPointerException(msg);
        }
    }

    public static void executorExists(HttpExecutor executor, String path){
        if(executor == null){
            throw new HttpRequestException(String.format("your request path: { %s } is not found!", path), HttpResponseStatus.NOT_FOUND);
        }
    }

    public static void notEmpty(Collection collection, String msg) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new IllegalArgumentException(msg);
        }
    }
}
