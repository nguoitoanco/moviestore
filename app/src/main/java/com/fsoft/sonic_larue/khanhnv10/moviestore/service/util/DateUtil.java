package com.fsoft.sonic_larue.khanhnv10.moviestore.service.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by KhanhNV10 on 2015/12/08.
 */
public class DateUtil {
    public static String FORMAT_YYYY_MM_DD = "YYYY/MM/DD";
    public static String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm";

    public static Date stringToDate(String strDate, String pattern) {
        Date date;
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            date = df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

    public static Date getDate(int year, int month, int day, String pattern) {
        String date = year + "/" + month + "/" + day;
        Date utilDate = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_YYYY_MM_DD);
            utilDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return utilDate;
    }

    public static Date getCurrentDate(String pattern) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);

        Date currentDate = getDate(year, month, day, pattern);
        return currentDate;
    }

    public static String dateToString(Date date, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        String strDate = StringUtil.EMPTY;
        try {
            strDate = df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strDate;
    }

    public static String getStrDate(int year, int month, int day) {
        String format = "%02d";
        return new StringBuilder()
                .append(String.format(format, year)).append("/")
                .append(String.format(format, month)).append("/")
                .append(String.format(format, day))
                .toString();
    }

    public static String timeStampToString(long timeStamp, String pattern) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timeStamp);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String strDate = simpleDateFormat.format(c.getTime());
        return strDate;
    }

    public static long getCurrentTimeStamp() {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }
}
