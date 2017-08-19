package com.example.administrator.wechat.utils;

import android.widget.Toast;

import com.example.administrator.wechat.WeChatApplication;

/**
 * Created by Administrator on 2017/8/10.
 */

public class ToastUtil {
    private static Toast sToast;
    public static void showToast(String  msg){
        if (sToast==null) {
            sToast = Toast.makeText(WeChatApplication.getWeChatApplication(),msg,Toast.LENGTH_SHORT);
        }

        sToast.setText(msg);
        sToast.show();
    }
}
