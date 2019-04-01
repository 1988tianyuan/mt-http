package com.liugeng.mthttp.config;

import org.apache.commons.configuration2.PropertiesConfiguration;

public interface Configurable {

	void config(PropertiesConfiguration config);

	String SERVER_EVENTLOOP_BOSS_THREAD = "server.eventLoop.boss.thread.num";
	String SERVER_EVENTLOOP_WORKER_THREAD = "server.eventLoop.worker.thread.num";
	String DISPATCHER_EVENTLOOP_THREAD = "dispatcher.eventLoop.thread.num";
	String HTTP_EXECUTOR_SCAN_PACKAGE = "dispatcher.executor.scan.location";
	String SESSION_EXPIRE_TIME = "http.session.expire.millis";
	String DEFAULT_COOKIE_EXPIRE_TIME = "http.cookie.default.expire.millis";
	String SERVER_BIND_HOST = "server.bind.host";
	String SERVER_BIND_PORT = "server.bind.port";
}
