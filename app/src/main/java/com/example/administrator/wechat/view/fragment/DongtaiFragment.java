package com.example.administrator.wechat.view.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.administrator.wechat.R;
import com.example.administrator.wechat.presenter.DongtaiPresenter;
import com.example.administrator.wechat.presenter.DongtaiPresenterImpl;
import com.example.administrator.wechat.utils.ToastUtil;
import com.example.administrator.wechat.view.BaseActivity;
import com.example.administrator.wechat.view.LoginActivity;
import com.hyphenate.chat.EMClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class DongtaiFragment extends BaseFragment implements View.OnClickListener,DongtaiView {

    private Button mBtn_logout;
    private DongtaiPresenter mDongtaiPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dongtai, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtn_logout = (Button) view.findViewById(R.id.btn_logout);
        String currentUser = EMClient.getInstance().getCurrentUser();
        mBtn_logout.setText("退("+currentUser+")出");
        mBtn_logout.setOnClickListener(this);

        mDongtaiPresenter = new DongtaiPresenterImpl(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                logout();
                break;
        }
    }

    private void logout() {
     //显示进度条对话框
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) activity;
            baseActivity.showDialog(getResources().getString(R.string.is_logout));
        }

        mDongtaiPresenter.logout();
    }

    @Override
    public void onLogout(boolean isSuccess, String message) {
        /**
         * 1,隐藏对话框
         * 2,退出失败,谈吐司
         * 3,不管退出失败还是成功,都要跳转到登录界面
         */
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) activity;
            baseActivity.hideDialog();
        }

        if (!isSuccess) {
            ToastUtil.showToast(message);
        }

        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();//销毁MainActivity

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBtn_logout = null;
    }
}
