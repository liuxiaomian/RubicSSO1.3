package com.rubic.sso.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rubic.sso.dao.UserDao;
import com.rubic.sso.po.Ticket;
import com.rubic.sso.po.User;
import com.rubic.sso.service.LoginService;
import com.rubic.sso.util.CookieUtils;
import com.rubic.sso.util.DESUtils;
import com.rubic.sso.util.exception.CookieNotFoundException;
import com.rubic.sso.util.exception.IllegalInfoException;
import com.rubic.sso.util.exception.IllegalPasswordException;
import com.rubic.sso.util.exception.NullValueException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by Mian on 2016/5/19.
 */
public class LoginServiceImpl implements LoginService {

    /** 单点登录标记 */
    private Map<String, Ticket> tickets;

    /** cookie名称 */
    private String cookieName;

    /** 是否安全协议 */
    private boolean secure;

    /** 密钥 */
    private String secretKey;

    /** ticket有效时间 */
    private int ticketTimeout;

    /** 需要设置cookie的项目路径*/
    private List<String> cookiePaths;

    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    public void setTickets(Map<String, Ticket> tickets) {
        this.tickets = tickets;
    }
    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }
    public void setSecure(boolean secure) {
        this.secure = secure;
    }
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
    public void setTicketTimeout(int ticketTimeout) {
        this.ticketTimeout = ticketTimeout;
    }
    public void setCookiePaths(List<String> cookiePaths) {
        this.cookiePaths = cookiePaths;

    }

    public String handlePreLogin(HttpServletRequest request, HttpServletResponse response) {
        try {
            Cookie ticket = CookieUtils.getCookie(request, cookieName);
            String encodedTicket = ticket.getValue();
            String decodedTicket = DESUtils.decrypt(encodedTicket, secretKey);
            if (tickets.containsKey(decodedTicket)) {
                String setCookieURL = request.getParameter("setCookieURL");
                String gotoURL = request.getParameter("gotoURL");
                if (setCookieURL != null){
                    response.sendRedirect(setCookieURL + "?encodedticketKey="
                            + encodedTicket + "&expiry=" + ticket.getMaxAge()
                            + "&gotoURL=" + gotoURL);
                }
            } else {
                System.out.println("prelogin: tickets not contains this ticket");
                CookieUtils.deleteCookie(request, response, cookieName);
                return "login";
            }
        } catch (CookieNotFoundException e) {
            System.out.println("Cookie in SSOAuth is not founded !");
            return "login";

        } catch (NullValueException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return "login";
    }

    /**
     * 对用户登录进行处理 TODO 用户重复登陆问题
     * @param request
     * @param response
     * @return
     */
    public String handleLogin(HttpServletRequest request,HttpServletResponse response) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User user = null;
        try {
            user = checkUserInfo(email, password);
        } catch (IllegalInfoException e) {
            JSONObject result = packLoginMsg("邮箱错误",1);

            return result.toJSONString();
        } catch (IllegalPasswordException e) {
            JSONObject result = packLoginMsg("密码错误",1);
            return result.toJSONString();
        }

        String ticketKey = UUID.randomUUID().toString().replace("-", "");
        String encodedticketKey = DESUtils.encrypt(ticketKey, secretKey);

        Ticket ticket = generateTicket(email,user.getUser_id());
        tickets.put(ticketKey, ticket);

        String[] checks = request.getParameterValues("autoLoginInWeek");
        if (checks != null && "1".equals(checks[0])) {
            int expiry = 7 * 24 * 3600;
            System.out.println("一周之内免登录~~~");
            CookieUtils.generateCookeie(cookieName, encodedticketKey, response,
                    expiry, cookiePaths, secure);
        }else {
            int expiry = -1;
            CookieUtils.generateCookeie(cookieName, encodedticketKey, response,
                    expiry, cookiePaths, secure);
        }

        String gotoURL = request.getParameter("gotoURL");
        JSONObject result = new JSONObject();
        result.put("gotoURL", gotoURL);
        result.put("state", 0);
        result.put("protocol_id", "A-7-2-1-response");
        return result.toJSONString();
    }

    /**
     * 用户登出，删除ticket；在java代码里面发起请求
     * @param request
     * @param response
     * @return
     */
    public String handleLogout(HttpServletRequest request, HttpServletResponse response) {
        String encodedTicket = request.getParameter("cookieName");
        JSONObject resultJSON = new JSONObject();
        if (encodedTicket == null||encodedTicket.equals("")) {
            resultJSON.put("error", true);
            resultJSON.put("msg", "Ticket can not be empty!");
        } else {
            String decodedTicket = null;
            try {
                decodedTicket = DESUtils.decrypt(encodedTicket, secretKey);
            } catch (NullValueException e) {
                e.printStackTrace();
            }
            tickets.remove(decodedTicket);
            CookieUtils.deleteCookie(request, response, cookieName);
            resultJSON.put("error", false);
        }
        return resultJSON.toJSONString();
    }

    /**
     * 验证cookie是否合法
     * @param request
     * @param response
     * @return
     */
    public String handleAuthCookie(HttpServletRequest request, HttpServletResponse response) {

        String encodedTicket = request.getParameter("cookieName");

        String decodedTicket = null;
        try {
            decodedTicket = DESUtils.decrypt(encodedTicket, secretKey);
        } catch (NullValueException e) {
            JSONObject resultJSON = packAuthCookieMsg(true,"Ticket is null or '' !");
            CookieUtils.deleteCookie(request, response, cookieName);
            return resultJSON.toJSONString();
        }

        if (tickets.containsKey(decodedTicket)) {
            JSONObject resultJSON = packAuthCookieMsg(false,"auth success");
            resultJSON.put("email", tickets.get(decodedTicket).getEmail());
            resultJSON.put("userId", tickets.get(decodedTicket).getUserId());

            return resultJSON.toJSONString();

        } else {
            JSONObject resultJSON = packAuthCookieMsg(true,"Ticket is not found!");
            CookieUtils.deleteCookie(request, response, cookieName);
            return resultJSON.toJSONString();

        }
    }

    /**
     * 检查用户登录信息是否正确
     *
     * @param email
     * @param password
     * @return
     * @throws IllegalInfoException
     */
    private User checkUserInfo(String email, String password)
            throws IllegalInfoException, IllegalPasswordException {
        User user = userDao.findUserByEmail(email);
        if (user == null ) {
            throw new IllegalInfoException("wrong username");
        }else if(!user.getPassword().equals(password)){
            throw new IllegalPasswordException("wrong password");
        }
        return user;
    }

    /**
     * 打包用户登录结果
     * @param msg 登陆结果信息
     * @param state 登陆结果状态
     * @return
     */
    private JSONObject packLoginMsg(String msg,int state){
        JSONObject result = new JSONObject();
        result.put("state", state);
        result.put("msg", msg);
        result.put("protocol_id", "A-7-2-1-response");
        return result;
    }

    private JSONObject packAuthCookieMsg(boolean error,String msg){
        JSONObject resultJSON = new JSONObject();
        resultJSON.put("error", true);
        resultJSON.put("msg", "Ticket is null or '' !");
        return resultJSON;
    }

    /**
     * 生成Ticket
     * @param email 用户email
     * @param userId 用户id
     * @return
     */
    private Ticket generateTicket(String email,int userId){
        Timestamp createTime = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(createTime);
        cal.add(Calendar.SECOND, ticketTimeout);
        Timestamp recoverTime = new Timestamp(cal.getTimeInMillis());
        return new Ticket(email, userId, createTime,
                recoverTime);

    }

}
