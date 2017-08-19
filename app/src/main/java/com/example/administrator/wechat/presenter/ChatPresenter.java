package com.example.administrator.wechat.presenter;

import com.hyphenate.chat.EMMessage;

import java.io.File;

/**
 * Created by Administrator on 2017/8/13.
 */

public interface ChatPresenter {
    void initChat(String username);

    void loadMoreMsg(String username);

    void sendTextMsg(String username, String msg);

    void addMessage(EMMessage emMessage);


    void sendImageMag(File imageFile, String username);
}
