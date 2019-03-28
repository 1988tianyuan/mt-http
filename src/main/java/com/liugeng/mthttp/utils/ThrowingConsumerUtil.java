package com.liugeng.mthttp.utils;

import java.util.function.Consumer;

import com.liugeng.mthttp.server.HttpServer;

public class ThrowingConsumerUtil {

	public static  <T> Consumer<T> throwingConsumerWrapper(ConsumerWrapper<T, Exception> consumerWrapper) {
		return i -> {
			try {
				consumerWrapper.accept(i);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	@FunctionalInterface
	public interface ConsumerWrapper<T, E extends Throwable> {
		public void accept(T t) throws E;
	}
}
