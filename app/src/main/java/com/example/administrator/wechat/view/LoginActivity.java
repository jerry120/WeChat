package com.example.administrator.wechat.view;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.wechat.MainActivity;
import com.example.administrator.wechat.R;
import com.example.administrator.wechat.presenter.LoginPresenter;
import com.example.administrator.wechat.presenter.LoginPresenterImpl;
import com.example.administrator.wechat.utils.StringUtil;
import com.example.administrator.wechat.utils.ToastUtil;

public class LoginActivity extends BaseActivity implements TextView.OnEditorActionListener, View.OnClickListener, LoginView {

    private static final int REQUEST_STORAGE = 1;
    private EditText mEt_username;
    private EditText mEt_psw;
    private TextInputLayout mTil_username;
    private TextInputLayout mTil_psw;

    private LoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEt_username = (EditText) findViewById(R.id.et_username);
        mEt_psw = (EditText) findViewById(R.id.et_psw);
        mTil_username = (TextInputLayout) findViewById(R.id.til_username);
        mTil_psw = (TextInputLayout) findViewById(R.id.til_psw);

        //监听密码框的action键
        mEt_psw.setOnEditorActionListener(this);
        findViewById(R.id.tv_newuser).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);

        mLoginPresenter = new LoginPresenterImpl(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //回显
        mEt_username.setText(getUsername());
        mEt_psw.setText(getPsw());
    }

    @Override
    public boolean isEnableTranslucentStatus() {
        return true;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        Toast.makeText(this, "Action被点击了", Toast.LENGTH_SHORT).show();
        login();
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_newuser:
                //跳转到注册界面
                startActivity(RegistActivity.class, false);
                break;

            case R.id.btn_login:
                login();
                break;
        }
    }

    private void login() {
        /**
         * 1.获取用户名和密码（V）
         * 2. 校验数据（V）
         * 3. 哪个数据不合法，就把光标定位到哪，然后提示错误信息（V）
         * 4.显示进度条对话框
         * 5. 开始登录（P）
         * 6. 关闭对话框
         */

        //只能是字母开头后面可以是任意英文字符,总长度限制在[3,20]
        String username = mEt_username.getText().toString().trim();
        //只能是数字
        String psw = mEt_psw.getText().toString().trim();

        if (!StringUtil.checkUsername(username)) {//用户名不合法
            mEt_username.requestFocus();
            //将错误信息显示到TextInputLayout上
            mTil_username.setErrorEnabled(true);
            mTil_username.setError("用户名不合法");
        } else {//合法
            //将TextInputLayout上的错误信息隐藏
            mTil_username.setErrorEnabled(false);
        }

        if (!StringUtil.checkPsw(psw)) {
            mEt_psw.requestFocus();
            //将错误信息显示到TextInputLayout上
            mTil_psw.setErrorEnabled(true);
            mTil_psw.setError("密码不合法");
            return;
        } else {
            mTil_psw.setErrorEnabled(false);
        }

        //动态申请SDCard权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            return;
        }


        showDialog("正在登录中......");
        mLoginPresenter.login(username, psw);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_STORAGE) {
            if (grantResults[0]== PermissionChecker.PERMISSION_GRANTED) {
                login();
            }
        }
    }

    @Override
    public void onLogin(boolean isSuccess, String username, String psw, String message) {
        /**
         * 1,隐藏对话框
         * 2,如果成功,跳转到主界面
         * 3,失败,谈吐司
         */

        hideDialog();
        if (isSuccess) {
            saveUser(username, psw);
            startActivity(MainActivity.class, true);
        } else {
            ToastUtil.showToast(message);
        }
    }
}
