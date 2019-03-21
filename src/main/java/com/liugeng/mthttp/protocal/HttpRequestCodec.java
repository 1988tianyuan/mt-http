package com.liugeng.mthttp.protocal;

import com.liugeng.mthttp.pojo.HttpMethod;
import com.liugeng.mthttp.pojo.HttpRequest;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

public class HttpRequestCodec {

    public static HttpRequest decode(ByteBuf byteBuf) {
        HttpMethod method = HttpMethod.valueOf(decodeSingleBySpace(byteBuf));
        String requestUri = decodeSingleBySpace(byteBuf);
        String protocolAndVersion = decodeSingleByCR(byteBuf);
        List<String> httpHeaders = new LinkedList<>();
        while (byteBuf.getByte(byteBuf.readerIndex()) != 13) {
            httpHeaders.add(decodeSingleByCR(byteBuf));
        }
        byteBuf.skipBytes(2);
        byte[] body = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(body);
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setMethod(method);
        httpRequest.setProtocolAndVersion(protocolAndVersion);
        httpRequest.setRequestUri(requestUri);
        httpRequest.setHttpHeaders(httpHeaders);
        httpRequest.setBody(body);
        return httpRequest;
    }

    private static String decodeSingleBySpace(ByteBuf byteBuf) {
        return decodeSingle(byteBuf, 1, (byte) 32);
    }

    private static String decodeSingleByCR(ByteBuf byteBuf) {
        return decodeSingle(byteBuf, 2, (byte) 13);
    }

    private static String decodeSingle(ByteBuf byteBuf, int skip, byte slipToken) {
        byte[] bytes = new byte[byteBuf.bytesBefore(slipToken)];
        byteBuf.readBytes(bytes);
        byteBuf.skipBytes(skip);
        return new String(bytes, Charset.defaultCharset());
    }
}
