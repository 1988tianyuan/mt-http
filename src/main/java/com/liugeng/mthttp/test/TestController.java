package com.liugeng.mthttp.test;

import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.router.annotation.CookieValue;
import com.liugeng.mthttp.router.annotation.HttpController;
import com.liugeng.mthttp.router.annotation.HttpRequestBody;
import com.liugeng.mthttp.router.annotation.HttpRouter;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;

@HttpController
@HttpRouter(path = {"/hello"})
public class TestController {

	@HttpRouter(path = {"/hi"}, method = HttpMethod.GET)
	public String hello(String param, @CookieValue(key = "cookie_key") String value) {
        System.out.println("cookie value is:" + value);
        String s = "你传过来的信息是：{ " + param + " }";
        System.out.println(s);
		return s;
	}

	@HttpRouter(path = {"/hihihi"}, method = HttpMethod.GET)
	public String hello(Cookies cookies) {
	    cookies.addCookie("hehehe", "123456");
		return "你没有说话";
	}
}
