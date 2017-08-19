package com.example.administrator.wechat.presenter;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.example.administrator.wechat.utils.ThreadFactory;
import com.example.administrator.wechat.view.fragment.AddFriendView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

/**
 * Created by Administrator on 2017/8/13.
 */

public class AddFriendPresenterImpl implements AddFriendPresenter {

    private AddFriendView mAddFriendView;

    public AddFriendPresenterImpl(AddFriendView addFriendView) {
        this.mAddFriendView = addFriendView;
    }

    @Override
    public void search(String keyword) {
        /**
         * 1,去云数据库上搜索username中包含keyword的用户列表
         *
         */
        AVQuery<AVUser> userQuery = new AVQuery<>("_User");
        //添加查询条件(1,用户名必须包含keyword;2,不要把自己查找出来)
        userQuery.whereContains("username", keyword);
        String currentUser = EMClient.getInstance().getCurrentUser();
        userQuery.whereNotEqualTo("username", currentUser);
        userQuery.findInBackground(new FindCallback<AVUser>() {//发起异步查询
            @Override
            public void done(List<AVUser> list, AVException e) {

                if (e != null) {
                    //遇到异常
                    mAddFriendView.onSearchResult(false, null, e.getMessage());
                } else {
                    //没有异常
                    mAddFriendView.onSearchResult(true, list, "success");
                }
            }

        });
    }

    @Override
    public void addFriend(final String username, final String reason) {
        ThreadFactory.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(username, reason);
                    //请求发送成功
                    ThreadFactory.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {

                            mAddFriendView.onAddFriend(true, username, "success");
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    //请求发送失败
                    ThreadFactory.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {

                            mAddFriendView.onAddFriend(false, username, e.getMessage());
                        }
                    });

                }
            }
        });
    }
}
