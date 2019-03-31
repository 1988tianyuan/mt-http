package com.liugeng.mthttp.pojo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.liugeng.mthttp.utils.Assert;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;

public class Cookies {

	private Map<String, Cookie> cookieMap = new HashMap<>();

	public Cookies(Set<Cookie> cookieSet) {
		if (cookieSet != null) {
			for (Cookie cookie : cookieSet) {
				cookieMap.put(cookie.name(), cookie);
			}
		}
	}

	public String getCookieValue(String cookieName) {
		Assert.notNull(cookieName, "cookie name should not be null!");
		Cookie cookie = cookieMap.get(cookieName);
		if (cookie != null) {
			return cookie.value();
		}
		return null;
	}

	public Cookie getCookie(String cookieName) {
		Assert.notNull(cookieName, "cookie name should not be null!");
		return cookieMap.get(cookieName);
	}

	public void addCookie(Cookie cookie) {
		cookieMap.put(cookie.name(), cookie);
	}

	public void addCookie(String key, String value) {
		Cookie cookie = new DefaultCookie(key, value);
		cookie.setMaxAge(30000);
		cookie.setPath("/");
		cookie.setDomain("local.com");
		cookieMap.put(key, cookie);
	}

	public void addCookies(Cookies cookies) {
		cookieMap.putAll(cookies.cookieMap);
	}

	public Map<String, Cookie> getCookieMap() {
		return cookieMap;
	}

	public void setCookieMap(Map<String, Cookie> cookieMap) {
		this.cookieMap = cookieMap;
	}
}
