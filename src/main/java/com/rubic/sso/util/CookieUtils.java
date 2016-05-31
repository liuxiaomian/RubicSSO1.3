package com.rubic.sso.util;

import com.rubic.sso.util.exception.CookieNotFoundException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class CookieUtils {

	public static void deleteCookie(HttpServletRequest request,
									HttpServletResponse response, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {

			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					System.out.println("SSOAuth: set cookie maxage = 0: "
							+ cookie.getName()+" - "+cookie.getValue());
					Cookie deleteCookie = new Cookie(cookie.getName(), "hello world");
					deleteCookie.setPath(cookie.getPath());
					deleteCookie.setMaxAge(0);
					response.addCookie(deleteCookie);
				}
			}
		}
	}

	/**
	 * 生成一组给定的path路径的cookie
	 * @param cookieName
	 * @param value
	 * @param response
	 * @param expiry 过期时间
	 * @param paths path列表
     * @param flag 为true时用户https
     */
	public static void generateCookies(String cookieName, String value,
									   HttpServletResponse response, int expiry, List<String> paths, boolean flag) {
		for(String path:paths){
			Cookie cookie = new Cookie(cookieName, value);
//			cookie.setDomain();
			cookie.setSecure(flag);
			cookie.setMaxAge(expiry);
			cookie.setPath(path);
			response.addCookie(cookie);
		}
		
	}

	/**
	 * 获取cookie
	 * 
	 * @param request
	 * @param cookieName
	 * @return
	 * @throws CookieNotFoundException
	 */
	public static Cookie getCookie(HttpServletRequest request, String cookieName) throws CookieNotFoundException {
		Cookie targetCookie = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(cookieName)) {
					targetCookie = cookie;
					break;
				}
			}
		}
		if (targetCookie != null) {
			return targetCookie;
		} else {
			throw new CookieNotFoundException();
		}
	}
	
}
