package com.liugeng.mthttp.server;

import com.liugeng.mthttp.config.Configurable;

public interface Server extends Configurable {

	void start(HttpServerCallback callback);

	void stop();
}
