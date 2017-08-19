package com.example.administrator.wechat.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/8/16.
 */

public class KeybordListenerLinearlayout extends LinearLayout implements View.OnLayoutChangeListener {

    private static int  THRESHOLD = 200;

    public KeybordListenerLinearlayout(Context context) {
        this(context,null);
    }


    public KeybordListenerLinearlayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public KeybordListenerLinearlayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    public KeybordListenerLinearlayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Activity activity = (Activity) context;
        WindowManager windowManager = activity.getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        THRESHOLD = (int) (point.y*0.3f);
        addOnLayoutChangeListener(this);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if(oldBottom-bottom> THRESHOLD){//软键盘打开了
            if (mOnKeyboardChageListener!=null) {
                mOnKeyboardChageListener.onKeyboardOpen();
            }
        }else if(bottom-oldBottom> THRESHOLD){//软键盘关闭了
            if (mOnKeyboardChageListener!=null) {
                mOnKeyboardChageListener.onKeyboardClose();
            }
        }
    }

    //1,定义一个接口
    public interface OnKeyboardChageListener{
        void onKeyboardOpen();
        void onKeyboardClose();
    }

    private  OnKeyboardChageListener mOnKeyboardChageListener;

    public void setOnKeyboardChageListener(OnKeyboardChageListener onKeyboardChageListener) {
        mOnKeyboardChageListener = onKeyboardChageListener;
    }
}
