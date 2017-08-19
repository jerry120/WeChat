package com.example.administrator.wechat.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.administrator.wechat.MainActivity;
import com.example.administrator.wechat.R;
import com.example.administrator.wechat.adapter.AnimatorAdapter;
import com.example.administrator.wechat.presenter.SplashPresenter;
import com.example.administrator.wechat.presenter.SplashPresenterImpl;

public class SplashActivity extends BaseActivity implements SplashView{

    public static final int DURATION = 2000;
    private SplashPresenter mSplashPresenter;
    private ImageView mIv_logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mIv_logo = (ImageView) findViewById(R.id.iv_logo);

        /**
         * 1. 先判断用户是否已经登录了，(P)
         2. 如果已经登录了则直接进入主界面(V)
         3. 如果没有登录，则闪屏2s，然后跳转到登录界面(V)
         */

        mSplashPresenter = new SplashPresenterImpl(this);
        mSplashPresenter.checkLogin();
    }

    @Override
    public void onCheckLogin(boolean isLogin) {

        /**
         * 2. 如果已经登录了则直接进入主界面(V)
         * 3. 如果没有登录，则闪屏2s，然后跳转到登录界面(V)
         */
        if(isLogin){
            startActivity(MainActivity.class,true);
        }else {
            //执行一个透明度渐变的效果
            ObjectAnimator oa = ObjectAnimator.ofFloat(mIv_logo,"alpha",0,1).setDuration(DURATION);
            oa.addListener(new AnimatorAdapter(){
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    startActivity(LoginActivity.class,true);
                }
            });

            oa.start();
        }
    }
}
