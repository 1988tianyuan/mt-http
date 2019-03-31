package com.liugeng.mthttp.utils.converter;

import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.router.ConnectContext;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Parameter;
import java.util.Map;

public class ReferenceTypeConverter extends TypeConverter {

    @Override
    public Object convertIfNecessary(Parameter parameter, ConnectContext context) throws Exception {
        Map<String, Object> origParams = getReqParams(context);
        Class<?> requireType = parameter.getType();
        if (ClassUtils.isAssignable(requireType, ConnectContext.class)) {
            return context;
        } else if (ClassUtils.isAssignable(requireType, Cookies.class)) {
            return context.getResponseCookies();
        }
        // todo
        return null;
    }
}
