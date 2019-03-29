package com.liugeng.mthttp.test;

import java.nio.file.Path;

import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.router.annotation.HttpController;
import com.liugeng.mthttp.router.annotation.HttpRouter;

@HttpController
@HttpRouter
public class NewTestController {

	@HttpRouter(path = {"/hi"}, method = HttpMethod.GET)
	public String hello(String param, int i) {
		System.out.println(param);
		System.out.println(i);
		return "呵呵呵, 你传了一个数字过来：" + i;
	}
}
