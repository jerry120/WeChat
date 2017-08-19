package com.example.administrator.wechat.view;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.wechat.R;
import com.example.administrator.wechat.presenter.RegistPresenter;
import com.example.administrator.wechat.presenter.RegistPresenterImpl;
import com.example.administrator.wechat.utils.StringUtil;
import com.example.administrator.wechat.utils.ToastUtil;

public class RegistActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener ,RegistView{

    private EditText mEtUsername;
    private EditText mEtPsw;
    private TextInputLayout mTilUsrname;
    private TextInputLayout mTilPsw;
    private RegistPresenter mRegistPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        mEtUsername = (EditText) findViewById(R.id.et_username);
        mEtPsw = (EditText) findViewById(R.id.et_psw);
        mTilUsrname = (TextInputLayout) findViewById(R.id.til_username);
        mTilPsw = (TextInputLayout) findViewById(R.id.til_psw);
        findViewById(R.id.btn_regist).setOnClickListener(this);

        //监听密码框的action键
        mEtPsw.setOnEditorActionListener(this);

        mRegistPresenter = new RegistPresenterImpl(this);
    }

    @Override
    public boolean isEnableTranslucentStatus() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_regist:
                regist();
                break;
        }
    }



    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        regist();
//        Toast.makeText(this, "Action被点击了", Toast.LENGTH_SHORT).show();
        return false; //返回false，代表自己没有消费，那么系统就会走他自己的逻辑（隐藏输入法）
    }


    /**
     * 1.获取用户名和密码（V）
     * 2. 校验数据（V）
     * 3. 哪个数据不合法，就把光标定位到哪，然后提示错误信息（V）
     * 4.显示进度条对话框
     * 5. 开始注册（P）
     * 6. 关闭对话框
     */
    private void regist() {
        //只能是字母开头后面可以是任意英文字符,总长度限制在[3,20]
        String username = mEtUsername.getText().toString().trim();
        //只能是数字
        String psw = mEtPsw.getText().toString().trim();

        if (!StringUtil.checkUsername(username)) {//用户名不合法
            mEtUsername.requestFocus();
            //将错误信息显示到TextInputLayout上
            mTilUsrname.setErrorEnabled(true);
            mTilUsrname.setError("用户名不合法");
        }else{//合法
            //将TextInputLayout上的错误信息隐藏
            mTilUsrname.setErrorEnabled(false);
        }

        if (!StringUtil.checkPsw(psw)){
            mEtPsw.requestFocus();
            //将错误信息显示到TextInputLayout上
            mTilPsw.setErrorEnabled(true);
            mTilPsw.setError("密码不合法");
            return;
        }else{
            mTilPsw.setErrorEnabled(false);
        }

        showDialog("正在注册中......");

        mRegistPresenter.regist(username,psw);

    }

    @Override
    public void onRegist(boolean isSuccess, String username, String psw, String msg) {

        /**
         * 1. 隐藏对话框
         * 2. 如果成功，跳转到登录界面
         * 3. 如果失败了，弹吐司，告诉用户什么原因
         */

        hideDialog();
        if (isSuccess) {
            //回显注册成功的用户名和密码(保存到sp中)
            //保证登录界面只有一个 singleTask
            saveUser(username,psw);
            startActivity(LoginActivity.class,true);
        }else{
            ToastUtil.showToast(msg);
        }
    }
}
