package com.liugeng.mthttp.utils;

import com.liugeng.mthttp.exception.HttpRequestException;
import com.liugeng.mthttp.router.HttpExecutor;
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
}
