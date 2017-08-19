package com.example.administrator.wechat.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/8/9.
 */

public class ThreadFactory {

    /**
     * 默认情况下，如果使用空参的构造函数，那么在其构造函数内部会自动从当前线程中找Looper对象，然后绑定Looper对象
     * 如果在构造中传入了Looper对象，那么Handler就会绑定给Looper。
     *
     *  必须传入来自主线程的Looper对象
     *  获取主线程中的Looper
     */

    private static Handler sHandler = new Handler(Looper.getMainLooper());
    private static Executor sExecutor = Executors.newFixedThreadPool(3);
    public static void runOnSubThread(Runnable runnable){
        sExecutor.execute(runnable);
    }

    public static void runOnUIThread(Runnable runnable){
        sHandler.post(runnable);
    }
}
