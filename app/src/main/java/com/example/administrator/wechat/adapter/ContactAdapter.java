package com.example.administrator.wechat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.administrator.wechat.R;
import com.example.administrator.wechat.utils.StringUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/8/12.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> implements IContactAdapter{

    private List<String> contactsList;

    public ContactAdapter(List<String> contactsList) {
        this.contactsList = contactsList;
    }

    @Override
    public int getItemCount() {
        return contactsList==null?0:contactsList.size();
    }

    /**
     * 该方法总共调用的个数为界面同时最多显示的条目个数
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创键一个条目的view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact, parent, false);
        ContactViewHolder contactViewHolder = new ContactViewHolder(view);
        return contactViewHolder;
    }

    /**
     * 根据position获取数据,然后将数据显示到Itemview上
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        final String contact = contactsList.get(position);
        holder.mTv_newuser.setText(contact);
        String initial = StringUtil.getInitial(contact);
        holder.mTv_section.setText(initial);

        /**
         * 如果当前联系人的首字母跟上一个联系人的首字母是一致的，那么当前的首字母得隐藏掉（Gone）
         */

        if (position ==0) {
            //代表当前的联系人就是通讯录的第一个,显示
            holder.mTv_section.setVisibility(View.VISIBLE);
        }else {
            String preContact = contactsList.get(position - 1);
            String preInitial = StringUtil.getInitial(preContact);
            if (preInitial.equalsIgnoreCase(initial)) {
                //隐藏当前的section
                holder.mTv_section.setVisibility(View.GONE);
            }else{
                //否则直接显示
                holder.mTv_section.setVisibility(View.VISIBLE);
            }

        }

        //给itemview绑定监听事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnContactClickListener.onClick(contact);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mOnContactClickListener.onLongClick(contact);
                return true;
            }
        });

    }

    @Override
    public List<String> getItems() {
        return contactsList;
    }


    class ContactViewHolder extends RecyclerView.ViewHolder{

        private final TextView mTv_section;
        private final TextView mTv_newuser;

        public ContactViewHolder(View itemView) {
            super(itemView);
            mTv_section = (TextView) itemView.findViewById(R.id.tv_section);
            mTv_newuser = (TextView) itemView.findViewById(R.id.tv_username);

        }
    }

    private OnContactClickListener mOnContactClickListener;

    public void setOnContactClickListener(OnContactClickListener onContactClickListener) {
        this.mOnContactClickListener = onContactClickListener;
    }

    public interface OnContactClickListener{
        void onClick(String contact);
        void onLongClick(String contact);
    }
}
