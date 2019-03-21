package com.liugeng.mthttp.server;

public interface Server {

	void start(HttpServerCallback callback);

	void stop();
}
