package com.liugeng.mthttp.router.support;

import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.HttpExecutor;
import com.liugeng.mthttp.router.annotation.CookieValue;
import com.liugeng.mthttp.router.annotation.HttpRequestBody;
import com.liugeng.mthttp.utils.converter.*;
import org.apache.commons.lang3.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.liugeng.mthttp.router.support.AbstractHttpExecutor.ArgTypeToken.*;
import static com.liugeng.mthttp.router.support.AbstractHttpExecutor.ArgTypeToken.REFERENCE_TYPE;

public abstract class AbstractHttpExecutor implements HttpExecutor {

    private static final Map<AbstractHttpExecutor.ArgTypeToken, TypeConverter> converterMap = new HashMap<>();

    static {
        converterMap.put(REQUEST_BODY, new RequestBodyTypeConverter());
        converterMap.put(PRIMITIVE_VALUE, new PrimitiveTypeConverter());
        converterMap.put(REQUEST_COOKIE, new CookieValueTypeConverter());
        converterMap.put(REFERENCE_TYPE, new ReferenceTypeConverter());
        converterMap.put(RESPONSE_COOKIES, new ResponseCookiesTypeConverter());
    }

    protected ArgTypeToken checkArgType(Parameter parameter) {
        Class<?> argType = parameter.getType();
        Annotation[] annotations = parameter.getDeclaredAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == HttpRequestBody.class) {
                return REQUEST_BODY;
            } else if (annotation.annotationType() == CookieValue.class) {
                return REQUEST_COOKIE;
            }
        }
        if (ClassUtils.isPrimitiveOrWrapper(argType)
                || ClassUtils.isAssignable(argType, String.class)
                || ClassUtils.isAssignable(argType, Cookies.class)) {
            return PRIMITIVE_VALUE;
        }
        return REFERENCE_TYPE;
    }

    protected Object convertArgType(ArgTypeToken token, Parameter parameter, ConnectContext context) throws Exception {
        TypeConverter converter = converterMap.get(token);
        return converter.convertIfNecessary(parameter, context);
    }

    public enum ArgTypeToken {
        REQUEST_BODY, RESPONSE_COOKIES, PRIMITIVE_VALUE,
        REQUEST_COOKIE, REFERENCE_TYPE
    }
}
