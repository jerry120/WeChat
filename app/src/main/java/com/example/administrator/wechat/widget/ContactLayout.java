package com.example.administrator.wechat.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.wechat.R;

/**
 * Created by Administrator on 2017/8/11.
 */

public class ContactLayout extends RelativeLayout {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTv_float;
    private SlideBar mSlideBar;
    private RecyclerView mRecyclerView;

    public ContactLayout(Context context) {
        this(context,null);
    }

    public ContactLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //将contatct_layout的布局作为子空间添加到当前的ViewGroup
        LayoutInflater.from(context).inflate(R.layout.contact_layout,this,true);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mTv_float = (TextView) findViewById(R.id.tv_float);
        mSlideBar = (SlideBar) findViewById(R.id.slideBar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);

    }

    public ContactLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public ContactLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    public void setAdapter(RecyclerView.Adapter adapter){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);

    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener){
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
    }

    public void setRefreshing(boolean isRefreshing){
        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }
}
