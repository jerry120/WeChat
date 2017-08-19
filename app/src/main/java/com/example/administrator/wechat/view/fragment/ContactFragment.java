package com.example.administrator.wechat.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.wechat.adapter.ContactAdapter;
import com.example.administrator.wechat.R;
import com.example.administrator.wechat.event.ContactUpdateEvent;
import com.example.administrator.wechat.presenter.ContactPresenterImpl;
import com.example.administrator.wechat.presenter.ContatcPresenter;
import com.example.administrator.wechat.utils.ToastUtil;
import com.example.administrator.wechat.view.ChatActivity;
import com.example.administrator.wechat.widget.ContactLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends BaseFragment implements ContactView, SwipeRefreshLayout.OnRefreshListener {

    private ContatcPresenter mContatcPresenter;
    private ContactLayout mContactLayout;
    private ContactAdapter mContactAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContactLayout = (ContactLayout) view.findViewById(R.id.contactLayout);
        mContatcPresenter = new ContactPresenterImpl(this);

        //获取好友列表,然后显示到mContactLayout上
        mContatcPresenter.initContact();
        //将当前的fragment对象作为eventbus的订阅者
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ContactUpdateEvent event){
        ToastUtil.showToast("通讯录"+(event.isAdded?"添加了":"删除了")+event.username);
        mContatcPresenter.updateContact();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消注册
        EventBus.getDefault().unregister(this);
        mContactLayout = null;
        mContactAdapter = null;
    }

    @Override
    public void onInitContacts(List<String> contactList) {
        /**
         * 1,将集合中的通讯录显示到界面(mContactLayout对象中的Recycleview上)
         */
        mContactAdapter = new ContactAdapter(contactList);
        mContactLayout.setAdapter(mContactAdapter);
        //监听下拉刷新事件
        mContactLayout.setOnRefreshListener(this);
        //给adapter设置条目点击监听
        mContactAdapter.setOnContactClickListener(new ContactAdapter.OnContactClickListener(){
           public void onClick(String contact){
//                ToastUtil.showToast("单击了"+contact);
               Intent intent = new Intent(getActivity(), ChatActivity.class);
               intent.putExtra("contact",contact);
               startActivity(intent);
           }

           public void onLongClick(final String contact){
               ToastUtil.showToast("长按了"+contact);
               Snackbar.make(mContactLayout,"确定要删除 "+contact+"?",Snackbar.LENGTH_LONG)
                       .setAction("确定", new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               ToastUtil.showToast("删除了"+contact);
                               mContatcPresenter.deleteContact(contact);
                           }
                       })
                       .show();
           }
        });
    }

    @Override
    public void onUpdateContact(boolean isSuccess, String message) {
        mContactAdapter.notifyDataSetChanged();
        //更新结束后隐藏下拉刷新
        mContactLayout.setRefreshing(false);
    }

    @Override
    public void onDelete(boolean isSuccess, String contact, String message) {
        if (isSuccess) {
            Snackbar.make(mContactLayout,"删除"+contact+"成功",Snackbar.LENGTH_LONG).show();
        }else{
            Snackbar.make(mContactLayout,"删除"+contact+"失败 "+message,Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRefresh() {
        //更新通讯录(P)
        mContatcPresenter.updateContact();
    }
}
