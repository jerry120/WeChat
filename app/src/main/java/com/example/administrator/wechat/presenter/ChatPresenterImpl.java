package com.example.administrator.wechat.presenter;

import com.example.administrator.wechat.adapter.MyEMCallback;
import com.example.administrator.wechat.view.fragment.ChatView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/13.
 */

public class ChatPresenterImpl implements ChatPresenter {

    private final int PAGE_SIZE = 20;
    private ChatView mChatView;
    private List<EMMessage> mEMMessageList = new ArrayList<>();
    private String username;

    public ChatPresenterImpl(ChatView chatView) {
        mChatView = chatView;
    }

    /**
     * 获取当前用户和username直接的历史聊天记录(最近的20条)
     *
     * @param username
     */
    @Override
    public void initChat(String username) {
        this.username = username;
        mEMMessageList.clear();
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
        if (conversation != null) {
            //先获取最近的一条消息
            EMMessage lastMessage = conversation.getLastMessage();
            //在火气msgId之前的PAGE_SIZE条
            List<EMMessage> emMessageList = conversation.loadMoreMsgFromDB(lastMessage.getMsgId(), PAGE_SIZE - 1);
            //将最近一条和加载的跟多的PAGE_SIZE - 1条都放到mEMMessageList集合中，然后返回给View
            mEMMessageList.addAll(emMessageList);
            mEMMessageList.add(lastMessage);


            //将该会话的所有消息标记为已读
            conversation.markAllMessagesAsRead();
        }
        mChatView.onInitChat(mEMMessageList);

    }

    @Override
    public void loadMoreMsg(String username) {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
        if (conversation != null) {
            //获取到当前集合中最老的一条消息\
            EMMessage emMessage = mEMMessageList.get(0);
            //再获取上一页的PAGE_SIZE条消息
            List<EMMessage> moreMsgFromDB = conversation.loadMoreMsgFromDB(emMessage.getMsgId(), PAGE_SIZE);//注意此处不需要减1
            //将新加载的数据添加到mEMMessageList集合中
            //注意必须将新获取的消息添加到大集合的最前面
            mEMMessageList.addAll(0, moreMsgFromDB);
            mChatView.onLoadMore(true, moreMsgFromDB.size());
        } else {
            //conversation为null表示从来就没聊过天,没有历史记录
            mChatView.onLoadMore(false, 0);
        }
    }

    @Override
    public void sendTextMsg(String username, String msg) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(msg, username);
        //将新创建消息立即显示到界面上(消息加入集合,然后让adapter notify)
        mEMMessageList.add(message);
        mChatView.onChatUpdate();
        //监听消息发送的状态,因为状态改变时我们需要更新界面(注意设置监听写在发送之前)
        message.setMessageStatusCallback(new MyEMCallback() {
            @Override
            public void onMainSuccess() {
                //更新界面
                mChatView.onChatUpdate();
            }

            @Override
            public void onMainError(int code, String message) {
                //更新界面
                mChatView.onChatUpdate();
            }
        });
        //发送消息(该放那个发内部是一个异步的发送)
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    @Override
    public void addMessage(EMMessage emMessage) {
        mEMMessageList.add(emMessage);
        //将新接受到的消息标记为已读
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
        if (conversation != null) {
        //把一条消息标记为已读
            conversation.markMessageAsRead(emMessage.getMsgId());

        }
    }

    @Override
    public void sendImageMag(File imageFile, String username) {
        /**
         * 1,创建一个图片消息
         * 2,将新创建的消息显示到界面
         * 3,将message添加到mEMMessageList集合
         * 4,发送图片消息
         */

        //imagePath为图片本地路径，false为不发送原图（默认超过100k的图片会压缩后发给对方），需要发送原图传true
        EMMessage message = EMMessage.createImageSendMessage(imageFile.getAbsolutePath(), false, username);
        mEMMessageList.add(message);
        mChatView.onChatUpdate();
        EMClient.getInstance().chatManager().sendMessage(message);
    }
}
