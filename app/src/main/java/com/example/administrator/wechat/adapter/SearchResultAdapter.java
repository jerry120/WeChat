package com.example.administrator.wechat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.example.administrator.wechat.R;
import com.example.administrator.wechat.db.DBUtils;
import com.example.administrator.wechat.utils.StringUtil;
import com.hyphenate.chat.EMClient;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/8/13.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    private List<AVUser> mAVUsersList;
    private final List<String> mStringList;

    public SearchResultAdapter(List<AVUser> AVUsersList) {
        mAVUsersList = AVUsersList;
        //从本地数据库中获取当前用户的好友列表
        mStringList = DBUtils.getContact(EMClient.getInstance().getCurrentUser());
    }

    @Override
    public int getItemCount() {
        return mAVUsersList==null?0:mAVUsersList.size();
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search_result, parent, false);
        SearchResultViewHolder searchResultViewHolder = new SearchResultViewHolder(view);
        return searchResultViewHolder;
    }

    @Override
    public void onBindViewHolder(SearchResultViewHolder holder, int position) {
        AVUser avUser = mAVUsersList.get(position);
        holder.mTvUsername.setText(avUser.getUsername());

        Date createdAt = avUser.getCreatedAt();
        holder.mTvTime.setText(StringUtil.getDateString(createdAt));

        final String username = avUser.getUsername();
        if (mStringList.contains(username)) {
            //已经是好友
            holder.mBtnAdd.setText("已经添加");
            holder.mBtnAdd.setEnabled(false);
        }else{
            //添加好友
            holder.mBtnAdd.setText("添加");
            holder.mBtnAdd.setEnabled(true);
            holder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnAddFriendClickListener!=null) {
                        mOnAddFriendClickListener.onAddFriendClick(username);
                    }
                }
            });
        }

    }

    private OnAddFriendClickListener mOnAddFriendClickListener;
    public void setOnAddFriendClickListener(OnAddFriendClickListener onAddFriendClickListener){
        this.mOnAddFriendClickListener = onAddFriendClickListener;
    }

    public interface OnAddFriendClickListener{
        void onAddFriendClick(String username);
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder{

        private final TextView mTvUsername;
        private final TextView mTvTime;
        private final Button mBtnAdd;


        public SearchResultViewHolder(View itemView) {
            super(itemView);

            mTvUsername = (TextView) itemView.findViewById(R.id.tv_username);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_time);
            mBtnAdd = (Button) itemView.findViewById(R.id.btn_add);
        }
    }
}
