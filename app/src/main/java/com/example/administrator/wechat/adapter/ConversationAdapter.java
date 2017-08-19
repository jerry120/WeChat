package com.example.administrator.wechat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.wechat.R;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/8/16.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private List<EMConversation> mEMConversationList;

    public ConversationAdapter(List<EMConversation> emConversationList) {
        this.mEMConversationList = emConversationList;
    }

    @Override
    public int getItemCount() {
        return mEMConversationList==null?0:mEMConversationList.size();
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_conversation, parent, false);
        ConversationViewHolder conversationViewHolder = new ConversationViewHolder(view);
        return conversationViewHolder;
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {

        EMConversation emConversation = mEMConversationList.get(position);
        EMMessage lastMessage = emConversation.getLastMessage();
        int unreadMsgCount = emConversation.getUnreadMsgCount();
        final String userName = lastMessage.getUserName();//获取会话对象的名称
        long msgTime = lastMessage.getMsgTime();
        String  msg = "";
        if (lastMessage.getType()== EMMessage.Type.TXT) {
            EMTextMessageBody textMessageBody = (EMTextMessageBody) lastMessage.getBody();
            msg = textMessageBody.getMessage();
        }else if(lastMessage.getType()==EMMessage.Type.IMAGE){
            msg = "[图片]";
        }

        holder.mTvUsername.setText(userName);
        holder.mTvTime.setText(DateUtils.getTimestampString(new Date(msgTime)));
        holder.mTvMsg.setText(msg);

        if (unreadMsgCount>99){
            holder.mTvUnread.setVisibility(View.VISIBLE);
            holder.mTvUnread.setText("99+");
        }else if (unreadMsgCount>0){
            holder.mTvUnread.setVisibility(View.VISIBLE);
            holder.mTvUnread.setText(unreadMsgCount+"");
        }else{
            holder.mTvUnread.setVisibility(View.GONE);
            holder.mTvUnread.setText("0");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnConversationClickListener!=null) {
                    mOnConversationClickListener.onConversationClick(userName);
                }
            }
        });
    }



    class ConversationViewHolder extends RecyclerView.ViewHolder{

        private final TextView mTvUsername;
        private final TextView mTvMsg;
        private final TextView mTvTime;
        private final TextView mTvUnread;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            mTvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            mTvMsg = (TextView) itemView.findViewById(R.id.tv_msg);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mTvUnread = (TextView) itemView.findViewById(R.id.tv_unread);
        }
    }

    //定义会话点击监听
    public interface OnConversationClickListener{
        void onConversationClick(String username);
    }

    private OnConversationClickListener mOnConversationClickListener;

    public void setOnConversationClickListener(OnConversationClickListener onConversationClickListener) {
        mOnConversationClickListener = onConversationClickListener;
    }
}
