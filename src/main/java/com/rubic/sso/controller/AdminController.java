package com.rubic.sso.controller;

import com.rubic.sso.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 系统管理
 * Created by Mian on 2016/5/20.
 */

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @RequestMapping(value = "ticket/all",produces = "application/json;charset=UTF-8",method = RequestMethod.GET)
    @ResponseBody
    public String viewTickets(){
        return adminService.viewTickets().toJSONString();
    }

    @RequestMapping(value = "ticket/delete/{ticketKey}")
    @ResponseBody
    public String deleteTicket(@PathVariable(value = "ticketKey") String ticketKey){
        return adminService.deleteTicket(ticketKey).toJSONString();
    }

    @RequestMapping(value = "log/list",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String viewLogFileList(@RequestParam("pageIndex") int index){
        return adminService.viewLogFileList(index).toJSONString();
    }

    @RequestMapping(value = "log/{logFileName}",produces = "application/json;charset=UTF-8",method = RequestMethod.GET)
    public String viewLogDetails(@PathVariable(value = "logFileName") String logFileName){
        return null;
    }

}
