package com.rubic.sso.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rubic.sso.po.Ticket;
import com.rubic.sso.service.AdminService;
import com.rubic.sso.util.SSOFileVisitor;
import com.rubic.sso.util.TimeUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
    public JSONObject viewLogFileList(int index) {

        logger.info(webRootPath);

        Path filePath = Paths.get(webRootPath);
        Path parent1 = filePath.getParent();
        Path parent2 = parent1.getParent();
        logger.info("1:"+parent1+"2:"+parent2);

        Path logPath = Paths.get(parent2.toString(),"project_logs","RubicSSO");
        try {
            Path path = Files.walkFileTree(logPath,new SSOFileVisitor());
        JSONObject result = new JSONObject();
        result.put("paths",path);

        return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
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


//    private JSONObject transTime(Map<String,Ticket> tickets){
//
//        Set<Map.Entry<String,Ticket>> entries = tickets.entrySet();
//        Iterator<Map.Entry<String, Ticket>> iterator = entries.iterator();
//        JSONObject result = new JSONObject();
//        while (iterator.hasNext()){
//            Map.Entry<String,Ticket> entry = iterator.next();
//            TimeUtils.transMillisToTime(entry.getValue().getRecoverTime().getTime());
//        }
//
//    }
}
