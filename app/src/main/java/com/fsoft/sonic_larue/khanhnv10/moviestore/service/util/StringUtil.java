package com.fsoft.sonic_larue.khanhnv10.moviestore.service.util;

/**
 * Created by KhanhNV10 on 2015/12/15.
 */
public class StringUtil {
    public static final String EMPTY = "";
    public static final String DEFAULT = "0";

    public static boolean isEmpty(String str) {
        return (str == null || str.equals(EMPTY));
    }

    public static boolean isNumeric(String str) {
        return str.matches("^[0-9]+$");
    }
}
