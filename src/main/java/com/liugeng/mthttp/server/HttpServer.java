package com.liugeng.mthttp.server;

import com.liugeng.mthttp.server.handler.DefaultServerInitHandler;
import com.liugeng.mthttp.server.handler.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServer implements Server {

	private final NioEventLoopGroup workerGroup;

	private final NioEventLoopGroup bossGroup;

	private final ServerBootstrap bootstrap;

	private final int port;

	public HttpServer(int port) {
		this(port, 0, 0);
	}

	public HttpServer(int port, int bossThread, int workThread) {
		bossGroup = new NioEventLoopGroup(bossThread);
		workerGroup = new NioEventLoopGroup(workThread);
		bootstrap = new ServerBootstrap();
		this.port = port;
	}

	@Override
	public void start(HttpServerCallback callback) {
		bootstrap.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.handler(new ServerInitializer())
			.childHandler(new DefaultServerInitHandler());

		bootstrap.bind(port)
			.addListener(future -> {
				if(future.isSuccess()){
					log.debug("{}端口绑定成功！", port);
					callback.onSuccess();
				}else {
					log.debug("{}端口绑定失败，请切换其他端口！", port);
					callback.onError();
				}
			});
	}

	@Override
	public void stop() {
		bossGroup.shutdownGracefully().syncUninterruptibly();
		workerGroup.shutdownGracefully().syncUninterruptibly();
	}
}
