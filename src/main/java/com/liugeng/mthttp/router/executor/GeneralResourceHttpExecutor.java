package com.liugeng.mthttp.router.executor;

import java.io.InputStream;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

import com.liugeng.mthttp.constant.HttpMethod;
import com.liugeng.mthttp.router.ConnectContext;
import com.liugeng.mthttp.utils.io.ClassPathResource;
import com.liugeng.mthttp.utils.io.Resource;

public class GeneralResourceHttpExecutor implements HttpExecutor {

	private final String rootPath;

	public GeneralResourceHttpExecutor(String rootPath) {
		this.rootPath = rootPath;
	}

	@Override
	public void execute(ConnectContext context) throws Exception {
		HttpMethod requestMethod = context.getHttpMethod();
		String requestUri = genRealUri(context.getRequest().getPath());
		Resource resource = new ClassPathResource(requestUri);
		InputStream inputStream = resource.getInputStream();

	}

	private String genRealUri(String path) {
		if (StringUtils.isBlank(rootPath)) {
			return path;
		}
		if (StringUtils.endsWith(path, "/")) {
			return StringUtils.substring(path, 0, path.length() - 1) + path;
		} else {
			return rootPath + path;
		}
	}

	@Override
	public void config(PropertiesConfiguration config) throws Exception {

	}
}
