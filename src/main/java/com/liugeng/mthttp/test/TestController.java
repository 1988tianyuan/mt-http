package com.liugeng.mthttp.test;

import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.router.annotation.HttpController;
import com.liugeng.mthttp.router.annotation.HttpRouter;

@HttpController
@HttpRouter(path = {"/hello"}, method = HttpMethod.GET)
public class TestController {

	@HttpRouter
	public String hello() {
		return "哈哈哈哈哈!";
	}
}
