package com.example.administrator.wechat.presenter;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.example.administrator.wechat.adapter.MyEMCallback;
import com.example.administrator.wechat.view.fragment.DongtaiView;
import com.hyphenate.chat.EMClient;

/**
 * Created by Administrator on 2017/8/10.
 */

public class DongtaiPresenterImpl implements DongtaiPresenter{

    private DongtaiView mDongtaiView;

    public DongtaiPresenterImpl(DongtaiView dongtaiView) {
        mDongtaiView = dongtaiView;
    }


    @Override
    public void logout() {
        /**
         * 参数1:退出时是否解绑设备,true代表解绑,这样就接受不到推送消息了
         */
        EMClient.getInstance().logout(true, new MyEMCallback() {
            @Override
            public void onMainSuccess() {
                mDongtaiView.onLogout(true,"success");
            }

            @Override
            public void onMainError(int code, String message) {
                mDongtaiView.onLogout(false,message);
            }
        });
    }
}
