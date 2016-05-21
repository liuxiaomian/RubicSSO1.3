package com.rubic.sso.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rubic.sso.po.Ticket;
import com.rubic.sso.service.AdminService;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * 管理实现类
 * Created by Mian on 2016/5/20.
 */
public class AdminServiceImpl implements AdminService {

    private static Logger logger = Logger.getLogger(AdminServiceImpl.class);

    private static String webRootPath = System.getProperty("web.root");

    private Map<String, Ticket> tickets;

    public void setTickets(Map<String, Ticket> tickets) {
        this.tickets = tickets;
    }

    /**
     * 查看所有ticket
     * @return
     */
    public JSONObject viewTickets(){

        JSONObject ticketsJson = new JSONObject();
        ticketsJson.put("tickets",tickets);
        ticketsJson.put("protocol_id","查看所有ticket，暂时还没有协议号");

        return ticketsJson;
    }

    public JSONObject deleteTicket(String ticketKey) {


        Ticket oldTicket = tickets.remove(ticketKey);

        JSONObject result = new JSONObject();
        result.put("protocol_id","删除指定ticket");
        result.put("state",1);
        result.put("ticket",oldTicket);
        return result;
    }

    /**
     * 查看文件列表
     * @param index 请求查看的页数
     * @return
     */
    public JSONObject viewLogFileList(String index) {



        return null;
    }

    /**
     * 查看日志文件详情
     * @param fileName 日志文件名
     * @return
     */
    public JSONObject viewLogFileDetails(String fileName) {



        return null;
    }
}
