package com.liugeng.mthttp.test;

import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.router.annotation.HttpController;
import com.liugeng.mthttp.router.annotation.HttpRouter;

@HttpController
@HttpRouter(path = {"/hehehe"}, method = HttpMethod.GET)
public class NewTestController {

	@HttpRouter
	public String hello() {
		return "呵呵呵!";
	}
}
