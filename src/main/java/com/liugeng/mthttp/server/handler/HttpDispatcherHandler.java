package com.liugeng.mthttp.server.handler;

import static io.netty.handler.codec.http.HttpUtil.*;
import static com.liugeng.mthttp.constant.StringConstants.*;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration2.PropertiesConfiguration;

import com.liugeng.mthttp.config.Configurable;
import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.constant.StringConstants;
import com.liugeng.mthttp.exception.HttpRequestException;
import com.liugeng.mthttp.pojo.Cookies;
import com.liugeng.mthttp.pojo.HttpRequestEntity;
import com.liugeng.mthttp.pojo.HttpSession;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.HttpConnectContext;
import com.liugeng.mthttp.router.executor.HttpExecutor;
import com.liugeng.mthttp.router.HttpExecutorMappingInfo;
import com.liugeng.mthttp.router.mapping.HttpExecutorMapping;
import com.liugeng.mthttp.router.mapping.MethodExecutorMappingInitializer;
import com.liugeng.mthttp.router.mapping.ResourceExecutorMappingInitializer;
import com.liugeng.mthttp.utils.Assert;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

@ChannelHandler.Sharable
public class HttpDispatcherHandler extends SimpleChannelInboundHandler<FullHttpRequest> implements Configurable {

	private final List<HttpExecutorMapping> mappingList;

	private final Map<String, HttpSession> sessionMap;

	private final EventLoopGroup eventExecutors;

	private PropertiesConfiguration config;

	public HttpDispatcherHandler(EventLoopGroup eventExecutors) {
		this.sessionMap = new ConcurrentHashMap<>();
		this.eventExecutors = eventExecutors;
		this.mappingList = new LinkedList<>();
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
		HttpExecutor executor = getExecutor(requestEntity);
		Assert.executorExists(executor, requestEntity.getPath());
		handleCookies(connectContext);
		handleSession(connectContext);
		executor.execute(connectContext);
	}

	private HttpExecutor getExecutor(HttpRequestEntity requestEntity) {
		for (HttpExecutorMapping mapping : mappingList) {
			HttpExecutor executor = mapping.getExecutor(requestEntity);
			if (executor != null) {
				return executor;
			}
		}
		return null;
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
		// todo, make session with multi kind of storage(like redis...)
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
		HttpSession session = new HttpSession();
		session.setMaxInactiveInterval(config.getLong(SESSION_EXPIRE_TIME, 300000));
		sessionMap.put(session.getId(), session);
		return session;
	}

	@Override
	public void config(PropertiesConfiguration config) throws Exception {
		this.config = config;
		initRequestMapping();
		initSessionSchedule(this.eventExecutors);
	}

	private void initRequestMapping() throws Exception {
		MethodExecutorMappingInitializer memInitializer = new MethodExecutorMappingInitializer(config);
		ResourceExecutorMappingInitializer remInitializer = new ResourceExecutorMappingInitializer(config);
		mappingList.add(memInitializer.initMapping());
//		mappingList.add(remInitializer.initMapping());
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
