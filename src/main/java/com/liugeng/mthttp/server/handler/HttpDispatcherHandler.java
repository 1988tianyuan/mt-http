package com.liugeng.mthttp.server.handler;

import static io.netty.handler.codec.http.HttpUtil.*;
import static com.liugeng.mthttp.constant.StringConstants.*;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.constant.StringConstants;
import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.pojo.HttpSession;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.HttpConnectContext;
import com.liugeng.mthttp.router.executor.HttpExecutor;
import com.liugeng.mthttp.router.HttpExecutorMappingInfo;
import com.liugeng.mthttp.utils.Assert;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

@ChannelHandler.Sharable
public class HttpDispatcherHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private final Map<HttpExecutorMappingInfo, HttpExecutor> executorMap;

	private final Map<String, HttpSession> sessionMap;

	private final EventLoopGroup eventExecutors;

	public HttpDispatcherHandler(Map<HttpExecutorMappingInfo, HttpExecutor> executorMap, EventLoopGroup eventExecutors) {
		this.executorMap = executorMap;
		this.sessionMap = new ConcurrentHashMap<>();
		this.eventExecutors = eventExecutors;
		initSessionSchedule(this.eventExecutors);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext handlerContext, FullHttpRequest request) throws Exception {
		HttpRequestEntity requestEntity= resolveRequest(handlerContext.channel(), request);
		ConnectContext connectContext = new HttpConnectContext(requestEntity);
		dispatch(connectContext);
	}

	private HttpRequestEntity resolveRequest(Channel channel, FullHttpRequest request) {
		Charset charset = getCharset(request, Charset.defaultCharset());
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri(), charset);
		HttpMethod httpMethod = HttpMethod.valueOf(request.method().name());
		return new HttpRequestEntity
			.Builder()
			.method(httpMethod).path(queryStringDecoder.path())
			.queryParams(queryStringDecoder.parameters()).bodyBuf(request.content())
			.channel(channel).httpHeaders(request.headers())
			.build();
	}

	private void dispatch(ConnectContext connectContext) throws Exception {
		HttpRequestEntity requestEntity = connectContext.getRequest();
		HttpExecutorMappingInfo mappingInfo = new HttpExecutorMappingInfo(requestEntity.getPath(), requestEntity.getMethod());
		HttpExecutor executor = getExecutor(mappingInfo);
		Assert.executorExists(executor, mappingInfo.getPath());
		handleCookies(connectContext);
		handleSession(connectContext);
		executor.execute(connectContext);
	}

	private HttpExecutor getExecutor(HttpExecutorMappingInfo mappingInfo) {
		return this.executorMap.get(mappingInfo);
	}

	/**
	 * save Cookies in HttpHeaders into context
	 * @param connectContext
	 */
	private void handleCookies(ConnectContext connectContext) {
		HttpHeaders httpHeaders = connectContext.getRequest().getHttpHeaders();
		if (httpHeaders.contains(HttpHeaderNames.COOKIE)) {
			ServerCookieDecoder cookieDecoder = ServerCookieDecoder.STRICT;
			Set<Cookie> cookieSet = cookieDecoder.decode(httpHeaders.get(HttpHeaderNames.COOKIE));
			Cookies cookies = new Cookies(cookieSet);
			connectContext.addRequestCookies(cookies);
		}
	}

	/**
	 * check whether the related session exist, if not, then create new session and save into context
	 * refresh session lastAccessedTime
	 * @param connectContext
	 */
	private void handleSession(ConnectContext connectContext) {
		Cookies cookies = connectContext.getRequestCookies();
		String sessionId = cookies.getCookieValue(SESSION_ID);
		HttpSession session;
		if (sessionId != null) {
			session = this.sessionMap.get(sessionId);
			if (session == null) {
				session = createNewSession();
			}
		} else {
			session = createNewSession();
		}
		session.setLastAccessedTime(System.currentTimeMillis());
		connectContext.setSession(session);
	}

	private HttpSession createNewSession() {
		// todo, make session parameters configurable
		HttpSession session = new HttpSession();
		sessionMap.put(session.getId(), session);
		return session;
	}

	private void initSessionSchedule(EventLoopGroup eventExecutors) {
		eventExecutors.scheduleWithFixedDelay(
				new SessionRemoveTask(this.sessionMap),
				60, 30, TimeUnit.SECONDS
		);
	}

	public EventLoopGroup getEventExecutors() {
		return eventExecutors;
	}

	private static class SessionRemoveTask implements Runnable {

		private Map<String, HttpSession> sessionMap;

		SessionRemoveTask(Map<String, HttpSession> sessionMap) {
			this.sessionMap = sessionMap;
		}

		@Override
		public void run() {
			sessionMap.entrySet().removeIf(sessionEntry -> {
				HttpSession session = sessionEntry.getValue();
				Long timeGap = System.currentTimeMillis() - session.getLastAccessedTime();
				return (timeGap - session.getMaxInactiveInterval()) > 0;
			});
		}
	}

}
