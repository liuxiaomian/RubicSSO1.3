package com.rubic.sso.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Mian on 2016/5/20.
 */
public interface AdminService {

    JSONObject viewTickets();

    JSONObject deleteTicket(String ticketKey);

    JSONObject viewLogFileDetails(String fileName);

    JSONObject viewLogFileList(int index);


}
