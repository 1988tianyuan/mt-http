package com.liugeng.mthttp.pojo;

import java.util.List;

public class HttpResponse {

    private HttpMethod method;
    private String requestUri;
    private String protocolAndVersion;
    private List<String> httpHeaders;
    private byte[] body;
}
