package com.liugeng.mthttp.router.executor;

import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.annotation.CookieValue;
import com.liugeng.mthttp.router.annotation.HttpRequestBody;
import com.liugeng.mthttp.router.resovler.HttpResponseResolver;
import com.liugeng.mthttp.utils.Assert;
import com.liugeng.mthttp.utils.converter.*;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.lang3.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractHttpExecutor implements HttpExecutor {

    private volatile List<HttpResponseResolver> resolverList;

    protected PropertiesConfiguration config;

    @Override
    public void config(PropertiesConfiguration config) {
        this.config = config;
    }

    protected HttpResponseResolver selectResolver(Object returnValue) {
        Assert.notEmpty(resolverList, "resolverList is empty! can't resolve your response!");
        for (HttpResponseResolver resolver : resolverList) {
            if (resolver.supportReturnValue(returnValue)) {
                return resolver;
            }
        }
        throw new IllegalArgumentException("can't resolve this returnValue type: " + returnValue.getClass());
    }

    public void setResolverList(List<HttpResponseResolver> resolverList) {
        this.resolverList = resolverList;
    }

    public void addResolverList(List<HttpResponseResolver> resolverList) {
        initResolverList();
        this.resolverList.addAll(resolverList);
    }

    public void addResolver(HttpResponseResolver resolver) {
        initResolverList();
        this.resolverList.add(resolver);
    }

    private void initResolverList() {
        if (this.resolverList == null) {
            this.resolverList = new LinkedList<>();
        }
    }
}
