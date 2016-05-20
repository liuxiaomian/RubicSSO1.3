package com.rubic.sso.job;

import com.rubic.sso.po.Ticket;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 回收ticket任务调度器
 * Created by Mian on 2016/5/19.
 */
public class RecoverTicketJob {

    private static Logger logger = Logger.getLogger(RecoverTicketJob.class);

    private Map<String, Ticket> tickets;

    public void setTickets(Map<String, Ticket> tickets) {
        this.tickets = tickets;
    }

    /**
     * 回收ticket实现
     */
    public void recover(){
//        System.out.println("目前共有："+tickets.size()+" ticket，现在对过期ticket进行清理!");
        logger.info("目前共有："+tickets.size()+" ticket，现在对过期ticket进行清理!");
        List<String> ticketKeys = new ArrayList<String>();
        for(Map.Entry<String, Ticket> entry : tickets.entrySet()) {
            long recoverTime = entry.getValue().getRecoverTime().getTime();
            long currentTime = System.currentTimeMillis();
            logger.info("recoverTime: "+recoverTime+" - currentTime: "+currentTime);
            if(recoverTime < currentTime)
                ticketKeys.add(entry.getKey());
        }
        for(String ticketKey : ticketKeys) {
            tickets.remove(ticketKey);
            logger.info("ticket[" + ticketKey + "]过期已删除！");
        }
    }

}
