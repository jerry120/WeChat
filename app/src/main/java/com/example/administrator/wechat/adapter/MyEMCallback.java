package com.example.administrator.wechat.adapter;

import com.example.administrator.wechat.utils.ThreadFactory;
import com.hyphenate.EMCallBack;

/**
 * Created by Administrator on 2017/8/10.
 */

public abstract class MyEMCallback implements EMCallBack {
    @Override
    public void onSuccess() {
        ThreadFactory.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                onMainSuccess();
            }


        });
    }

    public abstract void onMainSuccess();



    @Override
    public void onError(final int i, final String s) {
        ThreadFactory.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                onMainError(i,s);
            }
        });
    }

    public  abstract void onMainError(int code,String message);

    @Override
    public void onProgress(int i, String s) {

    }
}
