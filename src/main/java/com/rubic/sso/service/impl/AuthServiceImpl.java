package com.rubic.sso.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.rubic.sso.dao.UserDao;
import com.rubic.sso.po.Ticket;
import com.rubic.sso.po.User;
import com.rubic.sso.service.AuthService;
import com.rubic.sso.util.CookieUtils;
import com.rubic.sso.util.DESUtils;
import com.rubic.sso.util.TimeUtils;
import com.rubic.sso.util.exception.CookieNotFoundException;
import com.rubic.sso.util.exception.IllegalInfoException;
import com.rubic.sso.util.exception.IllegalPasswordException;
import com.rubic.sso.util.exception.NullValueException;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by Mian on 2016/5/19.
 */
public class AuthServiceImpl implements AuthService {

    private static Logger logger = Logger.getLogger(AuthServiceImpl.class);
    /**
     * 单点登录标记
     */
    private Map<String, Ticket> tickets;

    /**
     * cookie名称
     */
    private String cookieName;

    /**
     * 是否安全协议
     */
    private boolean secure;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * ticket有效时间
     */
    private int defaultTicketTimeout;

    /**
     * 需要设置cookie的项目路径
     */
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

    public void setDefaultTicketTimeout(int defaultTicketTimeout) {
        this.defaultTicketTimeout = defaultTicketTimeout;
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
                if (setCookieURL != null) {
                    StringBuilder sb = new StringBuilder(setCookieURL);
                    sb.append("?encodedticketKey=")
                            .append(encodedTicket)
                            .append("&expiry=")
                            .append(ticket.getMaxAge())
                            .append("&gotoURL=")
                            .append(gotoURL);
                    response.sendRedirect(sb.toString());
                } else {
                    return "404";
                }
            } else {
                logger.info("prelogin: tickets not contains this ticket");
                CookieUtils.deleteCookie(request, response, cookieName);
                return "login";
            }
        } catch (CookieNotFoundException e) {
            logger.info("Cookie in SSOAuth is not founded !");
            return "login";
        } catch (NullValueException e) {
            e.printStackTrace();
            return "500";
        } catch (IOException e) {
            e.printStackTrace();
            return "500";
        }
        return "404";
    }

    /**
     * 对用户登录进行处理 TODO 用户重复登陆问题
     *
     * @param request
     * @param response
     * @return
     */
    public String handleLogin(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        User user = null;
        try {
            user = checkUserInfo(email, password);
        } catch (IllegalInfoException e) {
            JSONObject result = packLoginMsg("邮箱错误", 1);
            return result.toJSONString();
        } catch (IllegalPasswordException e) {
            JSONObject result = packLoginMsg("密码错误", 1);
            return result.toJSONString();
        }
        String ticketKey = UUID.randomUUID().toString().replace("-", "");
        String encodedticketKey = DESUtils.encrypt(ticketKey, secretKey);

        String[] checks = request.getParameterValues("autoLoginInWeek");
        int expiry;
        if (checks != null && "1".equals(checks[0])) {
            expiry = 7 * 24 * 3600;
        } else {
            expiry = -1;

        }
        Ticket ticket = newTicketAndCookie(expiry, encodedticketKey, user, request,response);
        tickets.put(ticketKey, ticket);

        logger.info("用户登陆：" + user.toString() + "- ip：" + request.getRemoteHost());
        String gotoURL = request.getParameter("gotoURL");
        JSONObject result = new JSONObject();
        result.put("gotoURL", gotoURL);
        result.put("state", 0);
        result.put("protocol_id", "A-7-2-1-response");
        return result.toJSONString();
    }

    /**
     * 用户登出，删除ticket；在java代码里面发起请求
     *
     * @param request
     * @param response
     * @return
     */
    public String handleLogout(HttpServletRequest request, HttpServletResponse response) {
        String encodedTicket = request.getParameter("cookieName");

        String decodedTicket = null;
        try {
            decodedTicket = DESUtils.decrypt(encodedTicket, secretKey);
        } catch (NullValueException e) {
            JSONObject resultJSON = new JSONObject();
            resultJSON.put("error", true);
            resultJSON.put("msg", "Ticket can not be empty!");
            return resultJSON.toJSONString();
        }
        JSONObject resultJSON = new JSONObject();
        tickets.remove(decodedTicket);
        CookieUtils.deleteCookie(request, response, cookieName);
        resultJSON.put("error", false);
        resultJSON.put("msg", "登出成功");
        return resultJSON.toJSONString();
    }

    /**
     * 验证cookie是否合法
     *
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
            JSONObject resultJSON = packAuthCookieMsg(true, "Ticket is null or '' !");
            CookieUtils.deleteCookie(request, response, cookieName);
            return resultJSON.toJSONString();
        }

        if (tickets.containsKey(decodedTicket)) {
            JSONObject resultJSON = packAuthCookieMsg(false, "auth success");
            resultJSON.put("email", tickets.get(decodedTicket).getEmail());
            resultJSON.put("userId", tickets.get(decodedTicket).getUserId());
            return resultJSON.toJSONString();

        } else {
            JSONObject resultJSON = packAuthCookieMsg(true, "Ticket is not found!");
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
        if (user == null) {
            throw new IllegalInfoException("wrong username");
        } else if (!user.getPassword().equals(password)) {
            throw new IllegalPasswordException("wrong password");
        }
        return user;
    }

    /**
     * 打包用户登录结果
     *
     * @param msg   登陆结果信息
     * @param state 登陆结果状态
     * @return
     */
    private JSONObject packLoginMsg(String msg, int state) {
        JSONObject result = new JSONObject();
        result.put("state", state);
        result.put("msg", msg);
        result.put("protocol_id", "A-7-2-1-response");
        return result;
    }

    private JSONObject packAuthCookieMsg(boolean error, String msg) {
        JSONObject resultJSON = new JSONObject();
        resultJSON.put("error", error);
        resultJSON.put("msg", msg);
        return resultJSON;
    }

    /**
     * 生成ticket  TODO 用户选择一周免登录如何处理
     *
     * @param email    用户email
     * @param userId   用户id
     * @param deadTime 指定过期时间
     * @return ticket
     */
    private Ticket newTicket(String email, int userId, int deadTime) {
        return new Ticket(email, userId, TimeUtils.newTimeStampFromNow(deadTime));
    }

    /**
     * 生成ticket并把生成的cookie添加到response中
     *
     * @param expiry           cookie过期时间
     * @param encodedticketKey 加密的ticketKey
     * @param user             登陆的用户
     * @param response
     * @return ticket
     */
    private Ticket newTicketAndCookie(int expiry, String encodedticketKey, User user,HttpServletRequest request, HttpServletResponse response) {
        int timeout;
        if (expiry == -1) {
            timeout = defaultTicketTimeout;
        } else {
            timeout = expiry;
            logger.info("一周之内免登录~~~");
        }
        Map<String,String> pathAndDomain = new HashMap<String, String>();

        logger.info("remoteAddr:"+request.getRemoteAddr());
        logger.info("remoteUrl:"+request.getRequestURL());
        logger.info("remoteHost:"+request.getRemoteHost());
        logger.info("localAddr:"+request.getLocalAddr());
        logger.info("serverName:"+request.getServerName());

        for (String path:cookiePaths){
            if(path.equals(request.getContextPath())){
                pathAndDomain.put(path,request.getLocalAddr());
            }else {
                pathAndDomain.put(path,request.getRemoteAddr());
            }
        }
        CookieUtils.generateCookies(cookieName, encodedticketKey, response,
                expiry, cookiePaths, secure);
        return newTicket(user.getEmail(), user.getUser_id(), timeout);
    }

}
