package com.rubic.sso.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.SynchronousQueue;

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
    public static Timestamp newTimeStampFromNow(int interval) {
//        Timestamp createTime = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
//        cal.setTime(createTime);
        cal.add(Calendar.SECOND, interval);
        return new Timestamp(cal.getTimeInMillis());
    }

    /**
     * 将毫秒转换成时间
     *
     * @param millis 毫秒数
     * @return
     */
    public static String transMillisToTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        return calendar.getTime().toString();
    }

    public static long transTimeToMillis(String time){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        return calendar.getTimeInMillis();
    }

    public static void main(String[] args) {
        long millis = System.currentTimeMillis();
        System.out.println(millis);
        String time = transMillisToTime(millis);
        System.out.println(time);

        long m = transTimeToMillis(time);
        System.out.println(m);

    }

}
