package com.liugeng.mthttp.utils.converter;

public interface TypeConverter {
    <T> T convertIfNecessary(Object value, Class<T> requireType) throws Exception;
}
