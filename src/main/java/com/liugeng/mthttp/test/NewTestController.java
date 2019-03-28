package com.liugeng.mthttp.test;

import java.nio.file.Path;

import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.router.annotation.HttpController;
import com.liugeng.mthttp.router.annotation.HttpRouter;

@HttpController
@HttpRouter(path = {"/hehehe"})
public class NewTestController {

	@HttpRouter(path = {"/hi"}, method = HttpMethod.GET)
	public String hello(String param) {
		int i = 0;
		System.out.println(param);
		return "呵呵呵!";
	}
}
