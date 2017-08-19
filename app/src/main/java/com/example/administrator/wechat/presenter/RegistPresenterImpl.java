package com.example.administrator.wechat.presenter;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.example.administrator.wechat.utils.ThreadFactory;
import com.example.administrator.wechat.view.RegistView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by Administrator on 2017/8/9.
 */

public class RegistPresenterImpl implements RegistPresenter {

    private RegistView mRegistView;

    public RegistPresenterImpl(RegistView registView) {
        this.mRegistView = registView;
    }

    @Override
    public void regist(final String username, final String psw) {
        /**
         * 1. 先注册云数据库平台
         * 2. 如果云数据库平台成功了
         * 3. 再注册环信平台
         * 4. 如果环信失败了，则把云数据库上对应的数据删除掉即可（保存数据的一致性）
         */

        final AVUser user = new AVUser();// 新建 AVUser 对象实例
        user.setUsername(username);// 设置用户名
        user.setPassword(psw);// 设置密码

        user.signUpInBackground(new SignUpCallback() {//异步
            @Override
            public void done(AVException e) {
                if (e == null) {
                    // 注册环信
                    //注册失败会抛出HyphenateException
//                    try {
//                        EMClient.getInstance().createAccount(username, psw);//同步方法
//                    } catch (HyphenateException e1) {
//                        e1.printStackTrace();
//                    }

                    ThreadFactory.runOnSubThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().createAccount(username, psw);

                                ThreadFactory.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {

                                //环信注册成功
                                        mRegistView.onRegist(true, username, psw, "success");
                                    }
                                });
                            } catch (HyphenateException e1) {
                                e1.printStackTrace();
                                //环信注册失败,将云数据库的用户删除
                                user.deleteInBackground();
                                ThreadFactory.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        mRegistView.onRegist(false, username, psw, "环信注册失败");
                                    }
                                });
                            }
                        }
                    });

                } else {
                    // 失败的原因可能有多种，常见的是用户名已经存在。
                    mRegistView.onRegist(false, username, psw, "云数据库注册失败");
                    e.printStackTrace();
                }
            }
        });
    }
}
