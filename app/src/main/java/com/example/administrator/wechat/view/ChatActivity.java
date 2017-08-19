package com.example.administrator.wechat.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.wechat.R;
import com.example.administrator.wechat.adapter.ChatAdapter;
import com.example.administrator.wechat.model.ImagePath;
import com.example.administrator.wechat.presenter.ChatPresenter;
import com.example.administrator.wechat.presenter.ChatPresenterImpl;
import com.example.administrator.wechat.utils.ToastUtil;
import com.example.administrator.wechat.view.fragment.ChatView;
import com.example.administrator.wechat.view.fragment.PhotoActivity;
import com.example.administrator.wechat.widget.KeybordListenerLinearlayout;
import com.hyphenate.chat.EMMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends BaseActivity implements TextWatcher, View.OnClickListener, ChatView, KeybordListenerLinearlayout.OnKeyboardChageListener, ChatAdapter.OnImageListener {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private static final int REQUEST_CAMERA = 1;
    private static final int PICK_IMAGE = 2;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private EditText mEtMsg;
    private TextView mTvTitle;
    private Button mBtnSend;
    private ImageView mIvCamera;
    private ImageView mIvPic;
    private String mUsername;

    private ChatPresenter mChatPresenter;
    private ChatAdapter mChatAdapter;
    private File mFile;
    private KeybordListenerLinearlayout mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_chat);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_chat);
        mEtMsg = (EditText) findViewById(R.id.et_msg);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mIvCamera = (ImageView) findViewById(R.id.iv_camera);
        mIvPic = (ImageView) findViewById(R.id.iv_pic);

        mRoot = (KeybordListenerLinearlayout) findViewById(R.id.ll_root);

        Intent intent = getIntent();
        mUsername = intent.getStringExtra("contact");
        if (TextUtils.isEmpty(mUsername)) {
            ToastUtil.showToast("没有找到聊天对象");
            finish();
            return;
        }

        initToobar();
        String msg = mEtMsg.getText().toString();
        if (TextUtils.isEmpty(msg)) {
            mBtnSend.setEnabled(false);
        } else {
            mBtnSend.setEnabled(true);
        }

        //监听EditText中的文本变化
        mEtMsg.addTextChangedListener(this);
        mBtnSend.setOnClickListener(this);
        mIvCamera.setOnClickListener(this);
        mIvPic.setOnClickListener(this);

        mChatPresenter = new ChatPresenterImpl(this);

        //初始化历史聊天消息
        initChatHistory();
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //获取下一页历史数据,然后更新界面
                mChatPresenter.loadMoreMsg(mUsername);
            }
        });

        //将当前activity注册为eventBus的订阅者
        EventBus.getDefault().register(this);
        mRoot.setOnKeyboardChageListener(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EMMessage emMessage) {
        /**
         * 1,判断当前收到的消息是否是当前正在聊天的对象发送的消息
         * 2,将消息添加到大集合中(P)
         * 3,更新界面(V)
         */

        if (emMessage.getFrom().equals(mUsername)) {
            mChatPresenter.addMessage(emMessage);
            if (mChatAdapter != null) {
                mChatAdapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
            }
        }

    }

    private void initChatHistory() {
        mChatPresenter.initChat(mUsername);
    }

    private void initToobar() {
        mToolbar.setTitle("");
        mTvTitle.setText("与" + mUsername + "聊天中");
        setSupportActionBar(mToolbar);
        //显示返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //给返回键添加点击监听
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String msg = s.toString();
        if (TextUtils.isEmpty(msg)) {
            mBtnSend.setEnabled(false);
        } else {
            mBtnSend.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                sendTextMsg();
                break;
            case R.id.iv_camera:
                takePhoto();
                break;
            case R.id.iv_pic:
                choosePic();
                break;
        }
    }

    private void choosePic() {
        //调用相册
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void takePhoto() {
        //动态判断当前用户是否已经授予了拍照权限,如果没有则动态申请
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED) {
            //还没被授权,则动态申请
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mFile = new File(Environment.getExternalStorageDirectory() , "WeChat_" + new Date().getTime() + ".jpg");
//        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),"WeChat_"+new Date().getTime()+".jpg" );

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile)); // set the image file name

        //请求码必须>=0才能收到结果
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                //被授权了
                ToastUtil.showToast("您获得了拍照权限");
                takePhoto();
            } else {
                //被拒绝了
                ToastUtil.showToast("您拒绝了拍照权限");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //照相完成
                ToastUtil.showToast("照相了");
                //发送图片消息(P)
                System.out.println(mFile);
                if (mFile != null) {
                    ToastUtil.showToast("11111");
                    mChatPresenter.sendImageMag(mFile, mUsername);

                }

            } else {
                //没有照相
                ToastUtil.showToast("没有照相");
            }
        }else if(requestCode == PICK_IMAGE){
            if(resultCode==RESULT_OK){
                if(data!=null){
                    //系统图库返回的是图片的url地址
                    Uri uri = data.getData();
                    //获取到的uri是系统图库内容提供者提供的地址，不是图片的真正的绝对地址，
                    // 因此需要根据内容提供者去获取图片的真正地址
                    Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    if (cursor.moveToNext()) {
                        String imagePath = cursor.getString(0);
                        File file = new File(imagePath);
                        mChatPresenter.sendImageMag(file,mUsername);
                    }
                    cursor.close();
                }
            }
        }
    }

    private void sendTextMsg() {
        /**
         * 1,获取输入框中的文本,显示到聊天
         * 2,然后清除输入框的文本
         * 3,将消息提交给P层发送(P)
         */

        String msg = mEtMsg.getText().toString();
        mEtMsg.getText().clear();
        mChatPresenter.sendTextMsg(mUsername, msg);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onInitChat(List<EMMessage> emMessageList) {
        mChatAdapter = new ChatAdapter(emMessageList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mChatAdapter);
        mRecyclerView.scrollToPosition(emMessageList.size() - 1);
        //给chatAdapter绑定图片点击监听器
        mChatAdapter.setOnImageListener(this);
    }

    @Override
    public void onLoadMore(boolean isSuccess, int moreSize) {
        /**
         * 1,隐藏下拉刷新进度条
         * 2,如果成功,并且moreSize>0,让adapter更新界面
         * 3,如果失败,或者moreSize=0,弹吐司显示没有更多消息
         */
        mSwipeRefreshLayout.setRefreshing(false);
        if (isSuccess && moreSize > 0) {
            mChatAdapter.notifyDataSetChanged();
            ToastUtil.showToast("更新了" + moreSize + "条聊天记录");
        } else {
            ToastUtil.showToast("没有更多消息了");
        }
    }

    @Override
    public void onChatUpdate() {
        if (mChatAdapter != null) {
            mChatAdapter.notifyDataSetChanged();
            mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void onKeyboardOpen() {
        if (mRecyclerView != null) {
            mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount());
        }
    }

    @Override
    public void onKeyboardClose() {
    }

    @Override
    public void onImageClick(ArrayList<ImagePath> imagePathList, int imagePosition) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("imagePosition",imagePosition);
        intent.putParcelableArrayListExtra("imagePathList",imagePathList);
        startActivity(intent);
    }
}
