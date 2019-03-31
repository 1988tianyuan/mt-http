package com.liugeng.mthttp.pojo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSession {

    private String id;
    private Long createTime;
    private Long lastAccessedTime;
    private Long maxInactiveInterval;
    private Map<String, Object> attributeMap = new ConcurrentHashMap<>();

    private static final Long DEFAULT_INACTIVE_INTERVAL= 1800000L;

    public HttpSession(String id, Long createTime, Long lastAccessedTime, Long maxInactiveInterval) {
        this.id = id;
        this.createTime = createTime;
        this.lastAccessedTime = lastAccessedTime;
        this.maxInactiveInterval = maxInactiveInterval;
    }

    public HttpSession() {
        this(UUID.randomUUID().toString(), System.currentTimeMillis(), System.currentTimeMillis(), DEFAULT_INACTIVE_INTERVAL);
    }

    public Object getAttribute(String name) {
        return attributeMap.get(name);
    }

    public void setAttribute(String name, Object value) {
        attributeMap.put(name, value);
    }

    public void removeAttribute(String name) {
        attributeMap.remove(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(Long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    public Long getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public void setMaxInactiveInterval(Long maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    @Override
    public String toString() {
        return "HttpSession{" +
                "id='" + id + '\'' +
                ", createTime=" + createTime +
                ", lastAccessedTime=" + lastAccessedTime +
                ", maxInactiveInterval=" + maxInactiveInterval +
                ", attributeMap=" + attributeMap +
                '}';
    }
}
