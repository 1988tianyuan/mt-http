package com.liugeng.mthttp.router.annotation;

import static com.liugeng.mthttp.constant.HttpMethod.*;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.liugeng.mthttp.constant.HttpMethod;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpRouter {

	HttpMethod method() default GET;

	String[] path() default {""};

	String[] produces() default {"*/*"};
}
