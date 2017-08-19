package com.example.administrator.wechat.view;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.example.administrator.wechat.R;
import com.example.administrator.wechat.adapter.SearchResultAdapter;
import com.example.administrator.wechat.presenter.AddFriendPresenter;
import com.example.administrator.wechat.presenter.AddFriendPresenterImpl;
import com.example.administrator.wechat.utils.ToastUtil;
import com.example.administrator.wechat.view.fragment.AddFriendView;

import java.util.List;

public class AddFriendActivity extends BaseActivity implements SearchView.OnQueryTextListener,AddFriendView {

    private ImageView mIv_nodata;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private TextView mTvTitle;

    private AddFriendPresenter mAddFriendPresenter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        mIv_nodata = (ImageView) findViewById(R.id.iv_nodata);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTvTitle = (TextView) findViewById(R.id.tv_title);

        initToolbar();
        mAddFriendPresenter = new AddFriendPresenterImpl(this);

    }

    private void initToolbar() {
        mTvTitle.setText(R.string.search);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        //给toolbar设置回退箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //给箭头添加点击事件
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.add_friend_menu,menu);
        //初始化searchview
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) menuItem.getActionView();

        mSearchView.setQueryHint("支持模糊匹配");

        //设置全键盘上到的search按键的点击监听
        mSearchView.setOnQueryTextListener(this);
        //设置searchview是否获取到焦点的监听
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mTvTitle.setVisibility(View.GONE);
                }else{
                    mTvTitle.setVisibility(View.VISIBLE);
                }
            }
        });
        return true;
    }

    /**
     * 当点击键盘的search按键的时候,回调该方法
     * @param keyword
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String keyword) {
        //搜索姓名中包含 keyword 的所有用户 （p），然后展示到RecyclerView上（V）
        showDialog("正在搜索......");
        mAddFriendPresenter.search(keyword);
        //返回false，才可以自动隐藏软键盘
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }


    @Override
    public void onSearchResult(boolean isSuccess, List<AVUser> list, String message) {
        /**
         * 1,隐藏进度条
         * 2,将searchview的焦点去掉,不然软键盘会自动弹出
         * 2,如果成功,将集合显示到recycleview上
         * 3,如果失败,弹吐司
         */

        hideDialog();
        mSearchView.clearFocus();
        mTvTitle.setVisibility(View.INVISIBLE);//当搜索结束时隐藏Title
        if (isSuccess) {

            if (list==null||list.size()==0) {
                ToastUtil.showToast("没有查询到符合条件的结果");
                mIv_nodata.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
            }else{
                mIv_nodata.setVisibility(View.INVISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);

                SearchResultAdapter searchResultAdapter = new SearchResultAdapter(list);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setAdapter(searchResultAdapter);

                searchResultAdapter.setOnAddFriendClickListener(new SearchResultAdapter.OnAddFriendClickListener(){
                    public void onAddFriendClick(String username){
                        //发出请求(P)
                        mAddFriendPresenter.addFriend(username,getString(R.string.reason));
                    }
                });
            }
        }else{
            ToastUtil.showToast("查询失败:"+message);
            mIv_nodata.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAddFriend(boolean isSuccess, String username, String message) {
        if (isSuccess){
            Snackbar.make(mRecyclerView,"添加"+username+"请求发送成功",Snackbar.LENGTH_SHORT).show();
        }else{
            Snackbar.make(mRecyclerView,"添加"+username+"请求发送失败",Snackbar.LENGTH_SHORT).show();
        }
    }
}
