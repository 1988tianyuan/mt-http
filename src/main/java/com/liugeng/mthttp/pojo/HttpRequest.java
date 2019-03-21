package com.liugeng.mthttp.pojo;

import com.google.common.base.Objects;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private HttpMethod method;
    private String requestUri;
    private String protocolAndVersion;
    private List<String> httpHeaders;
    private byte[] body;

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getProtocolAndVersion() {
        return protocolAndVersion;
    }

    public void setProtocolAndVersion(String protocolAndVersion) {
        this.protocolAndVersion = protocolAndVersion;
    }

    public List<String> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(List<String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpRequest that = (HttpRequest) o;
        return method == that.method &&
                Objects.equal(requestUri, that.requestUri) &&
                Objects.equal(protocolAndVersion, that.protocolAndVersion) &&
                Objects.equal(httpHeaders, that.httpHeaders) &&
                Objects.equal(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(method, requestUri, protocolAndVersion, httpHeaders, body);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + method +
                ", requestUri='" + requestUri + '\'' +
                ", protocolAndVersion='" + protocolAndVersion + '\'' +
                ", httpHeaders=" + httpHeaders +
                ", body=" + Arrays.toString(body) +
                '}';
    }
}
