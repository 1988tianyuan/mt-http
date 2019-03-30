package com.liugeng.mthttp.test;

import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.router.annotation.CookieValue;
import com.liugeng.mthttp.router.annotation.HttpController;
import com.liugeng.mthttp.router.annotation.HttpRequestBody;
import com.liugeng.mthttp.router.annotation.HttpRouter;

@HttpController
@HttpRouter(path = {"/hello"})
public class TestController {

	@HttpRouter(path = {"/hi"}, method = HttpMethod.GET)
	public String hello(String param, @CookieValue(key = "cookie_key") String value) {
        System.out.println(value);
		return "你传过来的信息是：{ " + param + " }";
	}

	@HttpRouter(path = {"/hihihi"}, method = HttpMethod.GET)
	public String hello() {
		return "你没有说话";
	}
}
