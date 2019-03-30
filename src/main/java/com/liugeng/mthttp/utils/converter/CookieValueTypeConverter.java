package com.liugeng.mthttp.utils.converter;

import com.liugeng.mthttp.exception.HttpRequestException;
import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.annotation.CookieValue;

import java.lang.reflect.Parameter;

public class CookieValueTypeConverter extends TypeConverter {

    @Override
    public Object convertIfNecessary(Parameter parameter, ConnectContext context) throws Exception {
        CookieValue cookieValue = parameter.getAnnotationsByType(CookieValue.class)[0];
        String key = cookieValue.key();
        Cookies cookies = context.getRequestCookies();
        return cookies.getCookieValue(key);
    }
}
