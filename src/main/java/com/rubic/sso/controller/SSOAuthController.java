package com.rubic.sso.controller;

import com.rubic.sso.service.AuthService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mian on 2016/5/19.
 */

@Controller
@RequestMapping(value = "SSOAuth")
public class SSOAuthController {

    @Autowired
    private AuthService authService;

    @RequestMapping(params = "action=preLogin",produces = "application/json;charset=UTF-8")
    public String handlePreLogin(@RequestParam("action") String action, HttpServletRequest request, HttpServletResponse response){

        String path = authService.handlePreLogin(request,response);
        return path;

    }

    @RequestMapping(params = "action=login",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String handleLogin(@Param("action") String action, HttpServletRequest request, HttpServletResponse response){
        String result = authService.handleLogin(request,response);
        return result;

    }

    @RequestMapping(params = "action=logout",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String handleLoginout(@Param("action") String action, HttpServletRequest request, HttpServletResponse response){
        String result = authService.handleLogout(request,response);
        return result;

    }

    @RequestMapping(params = "action=authTicket",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String handleAuthTicket(@Param("action") String action, HttpServletRequest request, HttpServletResponse response){
        String result = authService.handleAuthCookie(request,response);
        return result;

    }

}
