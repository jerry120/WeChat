package com.example.administrator.wechat.presenter;

import com.example.administrator.wechat.view.fragment.ConversationView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/16.
 */

public class ConversationPresenterImpl implements ConversationPresenter {
    private List<EMConversation> mEMConversationList  = new ArrayList<>();
    private ConversationView mConversationView;
    public ConversationPresenterImpl(ConversationView conversationView) {
        this.mConversationView = conversationView;
    }

    @Override
    public void initConversation() {

        updateConversationData();

        mConversationView.onInit(mEMConversationList);
    }

    private void updateConversationData() {
        mEMConversationList.clear();

        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();

        mEMConversationList.addAll(conversations.values());
        //排序,最近会话消息在最上面
        Collections.sort(mEMConversationList, new Comparator<EMConversation>() {
            @Override
            public int compare(EMConversation o1, EMConversation o2) {
                int comp = (int) (o2.getLastMessage().getMsgTime()-o1.getLastMessage().getMsgTime());
                return comp;
            }
        });
    }

    @Override
    public void updateConversation() {
        updateConversationData();
    }
}
