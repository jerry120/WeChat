package com.example.administrator.wechat.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/8/9.
 */

public class StringUtil {

    public static final String USERNAME_REGEX = "^[a-zA-Z]\\w{2,19}$";
    public static final String PSW_REGEX = "^[0-9]{3,20}$";
    public static boolean checkUsername(String username){
        if (TextUtils.isEmpty(username)) {
            return false;
        }
        return username.matches(USERNAME_REGEX);
    }

    public static boolean checkPsw(String psw){
        if (TextUtils.isEmpty(psw)) {
            return false;
        }
        return psw.matches(PSW_REGEX);
    }

    public static String getInitial(String contact){
        if (TextUtils.isEmpty(contact)) {
            return "搜";
        }
        return contact.substring(0, 1).toUpperCase();
    }

    //时间格式化
    public static String getDateString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

}
