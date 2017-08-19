package com.example.administrator.wechat;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.example.administrator.wechat.utils.ToastUtil;
import com.example.administrator.wechat.view.AddFriendActivity;
import com.example.administrator.wechat.view.BaseActivity;
import com.example.administrator.wechat.view.fragment.BaseFragment;
import com.example.administrator.wechat.view.fragment.FragmentFactory;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends BaseActivity implements BottomNavigationBar.OnTabSelectedListener {

    private Toolbar mToolBar;
    private BottomNavigationBar mBottomNavigationBar;
    private TextView mTv_title;

    private String[] titles = {"消息", "通讯录", "动态"};
    private TextBadgeItem mTextBadgeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mBottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        mTv_title = (TextView) findViewById(R.id.tv_title);

        initToolBar();
        initFragment();
        initBottomNavigationbar();

        //当进入主界面的时候,将所有的历史会话加载到内存中,方便以后获取会话
        //保证进入主页面后本地会话 load 完毕。
        EMClient.getInstance().chatManager().loadAllConversations();

        //将当前activity注册为eventbus的订阅者
        EventBus.getDefault().register(this);


    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage emMessage){
        //更新未读消息角标
        if (mTextBadgeItem!=null) {
            updateUnreadMsg();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //更新未读消息角标
        if (mTextBadgeItem!=null) {
            updateUnreadMsg();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuBuilder menuBuilder = (MenuBuilder) menu;
        menuBuilder.setOptionalIconsVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_friend:
//                ToastUtil.showToast("添加好友");
                startActivity(AddFriendActivity.class, false);
                break;
            case R.id.menu_share_friend:
                ToastUtil.showToast("分享好友");
                break;
            case R.id.menu_about:
                ToastUtil.showToast("关于我们");
                break;
        }
        return true;
    }

    private void initFragment() {
        //解决fragment重影问题
        /**
         * 获取所有老的Fragment,然后移除掉
         * findFragmentByTag(position+"")
         */

        for (int i = 0; i < titles.length; i++) {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(i + "");
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();
            }

        }

        //先添加一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fl_content, FragmentFactory.getFragment(0), 0 + "")
                .commit();

        mTv_title.setText(titles[0]);
    }

    private void initBottomNavigationbar() {
        BottomNavigationItem conversationItem = new BottomNavigationItem(R.mipmap.conversation_selected_2,titles[0]);
        mTextBadgeItem = new TextBadgeItem();
        updateUnreadMsg();
        mTextBadgeItem.setTextColor(Color.WHITE);
        mTextBadgeItem.setBackgroundColor(Color.RED);
        mTextBadgeItem.setGravity(Gravity.RIGHT);

        conversationItem.setBadgeItem(mTextBadgeItem);

        BottomNavigationItem contactItem = new BottomNavigationItem(R.mipmap.contact_selected_2,titles[1]);
        BottomNavigationItem dongtaiItem = new BottomNavigationItem(R.mipmap.plugin_selected_2,titles[2]);
        mBottomNavigationBar.addItem(conversationItem);
        mBottomNavigationBar.addItem(contactItem);
        mBottomNavigationBar.addItem(dongtaiItem);

        mBottomNavigationBar.setActiveColor(R.color.colorPrimary);//选中后的颜色
        mBottomNavigationBar.setInActiveColor(R.color.inActive);//没选中后的颜色
        mBottomNavigationBar.setTabSelectedListener(this);

        mBottomNavigationBar.initialise();

    }

    @NonNull
    private TextBadgeItem updateUnreadMsg() {
        int unreadMessageCount = EMClient.getInstance().chatManager().getUnreadMessageCount();

        if (unreadMessageCount > 99) {
            mTextBadgeItem.setText("99+");
            mTextBadgeItem.show();
        } else if (unreadMessageCount > 0) {
            mTextBadgeItem.setText(unreadMessageCount + "");
            mTextBadgeItem.show();
        }else {
            mTextBadgeItem.hide();
        }
        return mTextBadgeItem;
    }

    private void initToolBar() {
        mToolBar.setTitle("");//将标题设置为空
        setSupportActionBar(mToolBar);//toolbar替换actionbar
    }

    @Override
    public void onTabSelected(int position) {
        /**
         * 1,获取对应的Fragment
         * 2,判断当前的Fragment是否已经被添加到界面,如果没有,则添加
         * 3,显示当前的Fragment
         * 4,修改标题
         */

        BaseFragment fragment = FragmentFactory.getFragment(position);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (!fragment.isAdded()) {
            transaction.add(R.id.fl_content, fragment, position + "");
        }
        transaction.show(fragment);
        transaction.commit();

        mTv_title.setText(titles[position]);
    }

    @Override
    public void onTabUnselected(int position) {
        //获取当前Fragment并隐藏
        BaseFragment fragment = FragmentFactory.getFragment(position);
        getSupportFragmentManager().beginTransaction()
                .hide(fragment)
                .commit();
    }

    @Override
    public void onTabReselected(int position) {

    }
}
