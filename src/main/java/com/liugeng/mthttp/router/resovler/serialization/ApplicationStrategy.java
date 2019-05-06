package com.liugeng.mthttp.router.resovler.serialization;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.liugeng.mthttp.exception.HttpRequestException;
import com.liugeng.mthttp.utils.io.Resource;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationStrategy implements SerializationStrategy {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationStrategy.class);

	@Override
	public ByteBuf serialize(Object response, Charset charset) throws Exception {
		Resource resource = (Resource) response;
		try(InputStream inputStream = resource.getInputStream()) {
			byte[] bytes = new byte[inputStream.available()];
			IOUtils.readFully(inputStream, bytes);
			return Unpooled.buffer(bytes.length).writeBytes(bytes);
		} catch (FileNotFoundException e) {
			String msg = String.format("can't find file: {%s}", resource.getDescription());
			logger.warn(msg, e);
			throw new HttpRequestException(msg, HttpResponseStatus.NOT_FOUND);
		}
	}
}
