package com.example.administrator.wechat.view.fragment;

import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by Administrator on 2017/8/13.
 */

public interface ChatView {
    void onInitChat(List<EMMessage> emMessageList);

    void onLoadMore(boolean isSuccess, int moreSize);

    void onChatUpdate();
}
