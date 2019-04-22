package com.liugeng.mthttp.utils.converter;

import com.liugeng.mthttp.router.ConnectContext;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TypeConverter {

    protected Map<String, Object> getReqParams(ConnectContext context) {
        Map<String, List<String>> queryParams = context.getRequest().getQueryParams();
        Map<String, Object> reqAttributes = context.getRequest().getAttributes();
        Map<String, Object> origParams = new HashMap<>(reqAttributes);
        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.entrySet()
                    .stream()
                    .filter(e -> e.getValue() != null && !e.getValue().isEmpty())
                    .forEach(e -> origParams.put(e.getKey(), e.getValue().get(0)));
        }
        return origParams;
    }


    public abstract Object convertIfNecessary(Parameter parameter, ConnectContext context) throws Exception;
}
