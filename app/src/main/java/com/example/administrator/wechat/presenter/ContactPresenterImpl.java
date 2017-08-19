package com.example.administrator.wechat.presenter;

import com.example.administrator.wechat.db.DBUtils;
import com.example.administrator.wechat.utils.ThreadFactory;
import com.example.administrator.wechat.view.fragment.ContactView;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public class ContactPresenterImpl implements ContatcPresenter {
    private List<String> mContactList = new ArrayList<>();
    private ContactView mContactView;

    public ContactPresenterImpl(ContactView contactView) {
        this.mContactView = contactView;
    }

    @Override
    public void initContact() {
        /**
         * 1,初始化通讯录时,现获取本地缓存在数据库中的通讯录(P)
         * 2,获取到本地通讯录后,直接显示到界面(V)
         * 3,访问环信网路,获取最新的通讯录(P)
         * 4,当获取到网络上返回的通讯录时,更新界面(V)
         * 5,缓存最新通讯录到本地(P)
         */


        final String currentUser = EMClient.getInstance().getCurrentUser();
        final List<String> contacts = DBUtils.getContact(currentUser);

        mContactList.clear();
        mContactList.addAll(contacts);
        mContactView.onInitContacts(mContactList);

        updateFromServer(currentUser);
    }

    private void updateFromServer(final String currentUser) {
        EMClient.getInstance().contactManager().aysncGetAllContactsFromServer(new EMValueCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> strings) {
                // 5,缓存最新通讯录到本地(P)
                // 4,当获取到网络上返回的通讯录时,更新界面(V)
                mContactList.clear();
                mContactList.addAll(strings);

                //对集合进行排序
                Collections.sort(mContactList, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareToIgnoreCase(o2);
                    }
                });

                //将最新的通讯录保存到本地数据库
                DBUtils.updateContacts(mContactList, currentUser);
                ThreadFactory.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mContactView.onUpdateContact(true,"success");
                    }
                });
            }

            @Override
            public void onError(int code, final String message) {
                ThreadFactory.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mContactView.onUpdateContact(false,message);
                    }
                });
            }
        });
    }

    @Override
    public void updateContact() {
        String currentUser = EMClient.getInstance().getCurrentUser();
        updateFromServer(currentUser);
    }

    @Override
    public void deleteContact(final String contact) {
        ThreadFactory.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(contact);
                    //删除成功
                    ThreadFactory.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mContactView.onDelete(true,contact,"success");
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    //删除失败
                    ThreadFactory.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mContactView.onDelete(false,contact,e.getMessage());
                        }
                    });
                }
            }
        });
    }
}
