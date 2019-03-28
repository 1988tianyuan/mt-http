package com.liugeng.mthttp.test;

import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.router.annotation.HttpController;
import com.liugeng.mthttp.router.annotation.HttpRouter;

@HttpController
@HttpRouter(path = {"/hello"}, method = HttpMethod.GET)
public class TestController {

	@HttpRouter(path = {"/hi"}, method = HttpMethod.GET)
	public String hello(String param) {
		int i = 0;
		return "你传过来的信息是：{ " + param + " }";
	}

	@HttpRouter(path = {"/hihihi"}, method = HttpMethod.GET)
	public String hello(String param1, String param2) {
		int i = 0;
		return "你传过来的信息是：{ " + param1 + " }";
	}
}
