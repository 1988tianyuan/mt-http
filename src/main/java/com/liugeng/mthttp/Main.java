package com.liugeng.mthttp;

import com.liugeng.mthttp.server.HttpServer;
import com.liugeng.mthttp.server.HttpServerCallback;
import com.liugeng.mthttp.server.Server;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

	public static void main(String[] args) throws InterruptedException {
		Server server = new HttpServer(8989);
		server.start(new HttpServerCallback() {
			@Override
			public void onSuccess() {
				log.debug("启动成功啦！端口是：{}", 8989);
			}

			@Override
			public void onError() {
				log.debug("启动失败了...");
				server.stop();
			}
		});
	}
}
