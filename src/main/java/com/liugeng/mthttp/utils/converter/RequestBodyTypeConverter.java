package com.liugeng.mthttp.utils.converter;

import com.liugeng.mthttp.router.ConnectContext;

import java.lang.reflect.Parameter;

public class RequestBodyTypeConverter extends TypeConverter {

    @Override
    public Object convertIfNecessary(Parameter parameter, ConnectContext context) throws Exception {
        return null;
    }
}
