package com.liugeng.mthttp.router.executor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import com.liugeng.mthttp.router.resovler.HttpResponseResolver;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
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

public class GeneralResourceHttpExecutor extends AbstractHttpExecutor {

	private final String rootPath;

	public GeneralResourceHttpExecutor(String rootPath) {
		this.rootPath = rootPath;
	}

	@Override
	public void execute(ConnectContext context) {
		String requestPath = context.getRequest().getPath();
		String requestUri = genRealUri(requestPath);
		HttpResponseResolver resolver;
		Resource resource = new ClassPathResource(requestUri);
		resolver = selectResolver(resource);
		// todo
		context.getResponse().getResponseHeaders().set(HttpHeaderNames.CONTENT_TYPE, selectRespMediaType(resource.getDescription()));
		resolver.resolve(resource, context, HttpResponseStatus.OK);
	}

	private String selectRespMediaType(String description) {
		// todo
		if (StringUtils.endsWith(description, ".html")) {
			return "text/html";
		} else if (StringUtils.endsWith(description, ".ico")) {
			return "image/x-icon";
		} else {
			return HttpHeaderValues.APPLICATION_OCTET_STREAM.toString();
		}
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
}
