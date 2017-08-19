package com.example.administrator.wechat.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.administrator.wechat.WeChatApplication;
import com.example.administrator.wechat.utils.Constant;

/**
 * Created by Administrator on 2017/8/9.
 */

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private SharedPreferences mSp;
    private WeChatApplication mWeChatApplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWeChatApplication = (WeChatApplication) getApplication();
        mWeChatApplication.addActivity(this);


        if (isEnableTranslucentStatus()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4 全透明状态栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// 5.0 全透明实现
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);// calculateStatusColor(Color.WHITE, (int) alphaValue)
            }

            mSp = getSharedPreferences("config", MODE_PRIVATE);
        }
    }

    public boolean isEnableTranslucentStatus() {
        return false;
    }

    public void startActivity(Class<? extends BaseActivity> clazz, boolean isFinish) {

        startActivity(new Intent(this, clazz));
        if (isFinish) {
            finish();
        }
    }

    public void showDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            //设置进度圈不能被取消
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
    }

    public void hideDialog(){
        if (mProgressDialog!=null) {
            mProgressDialog.dismiss();
        }
    }

    public void saveUser(String username,String psw){
        mSp.edit()
                .putString(Constant.SP_USERNAME,username)
                .putString(Constant.SP_PSW,psw)
                .commit();

    }

    public String getUsername(){
        return mSp.getString(Constant.SP_USERNAME,"");
    }

    public String getPsw(){
        return  mSp.getString(Constant.SP_PSW,"");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWeChatApplication.removeActivity(this);
    }
}
