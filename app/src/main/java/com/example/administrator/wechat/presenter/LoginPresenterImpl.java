package com.example.administrator.wechat.presenter;

import com.example.administrator.wechat.adapter.MyEMCallback;
import com.example.administrator.wechat.view.LoginView;
import com.hyphenate.chat.EMClient;

/**
 * Created by Administrator on 2017/8/10.
 */

public class LoginPresenterImpl implements LoginPresenter {
    private LoginView mLoginView;

    public LoginPresenterImpl(LoginView loginView) {
        mLoginView = loginView;
    }

    @Override
    public void login(final String username, final String psw) {
        /**
         * 1,登录到环信服务器
         */
        EMClient.getInstance().login(username, psw, new MyEMCallback() {
            @Override
            public void onMainSuccess() {
                mLoginView.onLogin(true,username,psw,"success");
            }

            @Override
            public void onMainError(int code,String message) {
                mLoginView.onLogin(false,username,psw,message);
            }
        });
    }
}
