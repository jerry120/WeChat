package com.example.administrator.wechat.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.wechat.R;
import com.example.administrator.wechat.adapter.ConversationAdapter;
import com.example.administrator.wechat.presenter.ConversationPresenter;
import com.example.administrator.wechat.presenter.ConversationPresenterImpl;
import com.example.administrator.wechat.view.ChatActivity;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationFragment extends BaseFragment implements ConversationView {


    private RecyclerView mRecyclerView;
    private ConversationPresenter mConversationPresenter;
    private ConversationAdapter mConversationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_conversation);
        mConversationPresenter = new ConversationPresenterImpl(this);
        //获取最近所有的会话(P)
        //展示到recycleview上(V)
        mConversationPresenter.initConversation();
        //当前fragment注册为eventbus的订阅者
        EventBus.getDefault().register(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage emMessage) {
        //更新会话数据(P)界面(V)
        mConversationPresenter.updateConversation();
        if (mConversationAdapter != null) {

            mConversationAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mRecyclerView = null;
        mConversationAdapter = null;
    }

    @Override
    public void onInit(List<EMConversation> emConversationList) {
        //展示到recycleview上(V)
        mConversationAdapter = new ConversationAdapter(emConversationList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mConversationAdapter);

        mConversationAdapter.setOnConversationClickListener(new ConversationAdapter.OnConversationClickListener() {
            @Override
            public void onConversationClick(String username) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("contact", username);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        mConversationPresenter.updateConversation();
        if (mConversationAdapter != null) {
            mConversationAdapter.notifyDataSetChanged();
        }
    }
}
