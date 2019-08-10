package com.sztvis.mnvrlibrary.util;

import android.text.TextUtils;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Administrator on 2019/8/1.
 */

public class TimeUtil {
    private static final int seconds_of_1minute = 60;
    private static final int seconds_of_1hour = 60 * 60;
    private static final int seconds_of_2hour = 2 * 60 * 60;
    private static final int seconds_of_3hour = 3 * 60 * 60;

    private static final String YMDHMS_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String search_DateFormat = "MM/dd/yyyy HH:mm:ss";
    private static final String TIME_ZERO = "00:00";
    private static final String TIME_MAX = "23:59:59";

    public static Date stringConvertDate(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        Date data = null;
        try {
            data = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String getUSDateTimeFormat(long timeStamp) {
        SimpleDateFormat usSdf = new SimpleDateFormat("HH:mm, MMMM dd, yyyy", Locale.US);
        return usSdf.format(new Date(timeStamp));
    }

    public static String getCurrentTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * local ---> UTC
     *
     * @return
     */
    public static String Local2UTC() {
        SimpleDateFormat sdf = new SimpleDateFormat(YMDHMS_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String gmtTime = sdf.format(new Date());
        return gmtTime;
    }

    /**
     * UTC --->local
     *
     * @param utcTime UTC
     * @return
     */
    public static String utc2Local(String utcTime) {
        //Log.i("utcTime",utcTime);
        try {
            if (TextUtils.isEmpty(utcTime)) {
                return "";
            }
            SimpleDateFormat utcFormater = new SimpleDateFormat(YMDHMS_FORMAT);
            utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date gpsUTCDate = null;
            try {
                gpsUTCDate = utcFormater.parse(utcTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat localFormater = new SimpleDateFormat(YMDHMS_FORMAT);
            localFormater.setTimeZone(TimeZone.getDefault());
            String localTime = localFormater.format(gpsUTCDate.getTime());
            return localTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 时间戳转换成日期格式字符串
     *
     * @return
     */
    public static String timeStamp2Date(long seconds, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(seconds));
    }

    public static String longToString(long longNum, String dateFormat) {
        if (TextUtils.isEmpty(dateFormat)) {
            dateFormat = YMDHMS_FORMAT;
        }
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        Date date = new Date(longNum);
        return format.format(date);
    }

    public static String secondsToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return TIME_ZERO;
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 23)
                    return TIME_MAX;
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        try {
            if (i >= 0 && i < 10)
                retStr = "0" + Integer.toString(i);
            else
                retStr = "" + i;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retStr;
    }

    public static long searchTimeToLong(String time) {
        if (TextUtils.isEmpty(time)) {
            return 0L;
        }
        try {
            String[] split = time.split(" ");
            String tempTime = split[0] + " " + split[1];
            int diff = 0;
            if ("pm".equals(split[2])) {
                diff = 1000 * 12 * 60 * 60;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(search_DateFormat);
            sdf.setTimeZone(TimeZone.getDefault());
            Date startTime = null;
            startTime = sdf.parse(tempTime);
            return (startTime.getTime() + diff);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static String searchTimeFormat(String time) {
        if (TextUtils.isEmpty(time)) {
            return "";
        }
        try {
            String date = (String) DateFormat.format("yyyy-MM-dd HH:mm:ss", searchTimeToLong(time));
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
