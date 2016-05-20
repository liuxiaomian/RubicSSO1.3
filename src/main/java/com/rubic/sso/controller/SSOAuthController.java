package com.rubic.sso.controller;

import com.rubic.sso.service.LoginService;
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
    private LoginService loginService;

    @RequestMapping(params = "action=preLogin")
    public String handlePreLogin(@RequestParam("action") String action, HttpServletRequest request, HttpServletResponse response){

        String path = loginService.handlePreLogin(request,response);
        return path;

    }

    @RequestMapping(params = "action=login")
    @ResponseBody
    public String handleLogin(@Param("action") String action, HttpServletRequest request, HttpServletResponse response){
        String result = loginService.handleLogin(request,response);
        return result;

    }

    @RequestMapping(params = "action=logout")
    @ResponseBody
    public String handleLoginout(@Param("action") String action, HttpServletRequest request, HttpServletResponse response){
        String result = loginService.handleLogout(request,response);
        return result;

    }

    @RequestMapping(params = "action=authTicket")
    @ResponseBody
    public String handleAuthTicket(@Param("action") String action, HttpServletRequest request, HttpServletResponse response){
        String result = loginService.handleAuthCookie(request,response);
        return result;

    }

}
