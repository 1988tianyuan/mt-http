package com.liugeng.mthttp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liugeng.mthttp.config.Configurable;
import com.liugeng.mthttp.server.HttpServer;
import com.liugeng.mthttp.server.HttpServerCallback;
import com.liugeng.mthttp.server.Server;
import com.liugeng.mthttp.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;


public class Main {

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	private static final Lock startingLock = new ReentrantLock();

	public static void main(String[] args) {
		String configLocation;
		PropertiesConfiguration config;
		Server server = null;
		try {
			startingLock.lock();
			configLocation = System.getProperty("config", ClassUtils.getDefaultClassLoader().getResource("server.properties").getFile());
			File configFile = FileUtils.getFile(URLDecoder.decode(configLocation, "UTF-8"));
			config = new PropertiesConfiguration();
			config.read(new FileReader(configFile));
			server = new HttpServer(config.getInt(Configurable.SERVER_BIND_PORT, 80));
			server.config(config);
		} catch (Exception e) {
			log.error("error during initialize server config", e);
			System.exit(1);
		} finally {
			startingLock.unlock();
		}
		final Server finalServer = server;
		server.start(new HttpServerCallback() {
			@Override
			public void onSuccess() {

			}
			@Override
			public void onError() {
				finalServer.stop();
			}
		});
	}
}
