package com.example.administrator.wechat.presenter;

import com.example.administrator.wechat.view.SplashView;
import com.hyphenate.chat.EMClient;

/**
 * Created by Administrator on 2017/8/9.
 */

public class SplashPresenterImpl implements SplashPresenter {

    private SplashView mSplashView;

    public SplashPresenterImpl(SplashView splashView) {
        this.mSplashView = splashView;
    }

    @Override
    public void checkLogin() {
        //判断当前用户是否已经登录
        if (EMClient.getInstance().isConnected() && EMClient.getInstance().isLoggedInBefore()) {
            mSplashView.onCheckLogin(true);
        }else {
            mSplashView.onCheckLogin(false);
        }
    }
}
