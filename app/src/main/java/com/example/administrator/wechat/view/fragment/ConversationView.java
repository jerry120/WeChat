package com.example.administrator.wechat.view.fragment;

import com.hyphenate.chat.EMConversation;

import java.util.List;

/**
 * Created by Administrator on 2017/8/16.
 */

public interface ConversationView {
    void onInit(List<EMConversation> emConversationList);
}
