package com.rubic.sso.util;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by Mian on 2016/5/20.
 */
public class TimeUtils {

    /**
     * 从当前时间，生成一个指定间隔时间的时间戳
     *
     * @param interval
     * @return
     */
    public static Timestamp newTimeStampFromNow(int interval){
        Timestamp createTime = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(createTime);
        cal.add(Calendar.SECOND, interval);
        return new Timestamp(cal.getTimeInMillis());
    }


}
