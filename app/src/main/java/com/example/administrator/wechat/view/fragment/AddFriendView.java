package com.example.administrator.wechat.view.fragment;

import com.avos.avoscloud.AVUser;

import java.util.List;

/**
 * Created by Administrator on 2017/8/13.
 */

public interface AddFriendView {
    void onSearchResult(boolean isSuccess, List<AVUser> list, String message);

    void onAddFriend(boolean isSuccess, String username, String message);
}
