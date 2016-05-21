package com.rubic.sso.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mian on 2016/5/19.
 */
public interface AuthService {


    String handlePreLogin(HttpServletRequest request, HttpServletResponse response);

    String handleLogin(HttpServletRequest request, HttpServletResponse response);

    String handleLogout(HttpServletRequest request, HttpServletResponse response);

    String handleAuthCookie(HttpServletRequest request, HttpServletResponse response);

}
