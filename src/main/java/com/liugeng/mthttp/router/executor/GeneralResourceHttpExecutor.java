package com.liugeng.mthttp.router.executor;

import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;

import com.liugeng.mthttp.exception.HttpRequestException;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.router.resovler.ResourceResolver;
import com.liugeng.mthttp.utils.io.ClassPathResource;
import com.liugeng.mthttp.utils.io.Resource;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;

public class GeneralResourceHttpExecutor implements HttpExecutor {

	private final String rootPath;

	public GeneralResourceHttpExecutor(String rootPath) {
		this.rootPath = rootPath;
	}

	@Override
	public void execute(ConnectContext context) {
		String requestPath = context.getRequest().getPath();
		String requestUri = genRealUri(requestPath);
		Resource resource = null;
		InputStream inputStream = null;
		try {
			resource = new ClassPathResource(requestUri);
			inputStream = resource.getInputStream();
		} catch (FileNotFoundException e) {
			throw new HttpRequestException("can't find your request resource: " + requestPath, HttpResponseStatus.NOT_FOUND);
		}
		// todo
		context.getResponse().getResponseHeaders().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_OCTET_STREAM);
		new ResourceResolver().resolve(inputStream, context, HttpResponseStatus.OK);
	}

	private String genRealUri(String path) {
		if (StringUtils.startsWith(path, "/")) {
			path = RegExUtils.replaceFirst(path, "/", "");
		}
		if (StringUtils.isBlank(rootPath)) {
			return path;
		}
		if (!StringUtils.endsWith(rootPath, "/")) {
			path = "/" + path;
		}
		return rootPath + path;
	}

	@Override
	public void config(PropertiesConfiguration config) throws Exception {

	}
}
