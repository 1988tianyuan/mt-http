package com.liugeng.mthttp.exception;

public class TypeMismatchException extends Exception{

    public <T> TypeMismatchException(Object value, Class<T> requireType) {
        super("the value : { " + value + " } couldn't be converted to this type: " + requireType);
    }
}
