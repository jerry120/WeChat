package com.example.administrator.wechat.view.fragment;

import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public interface ContactView {
    void onInitContacts(List<String> contactList);

    void onUpdateContact(boolean isSuccess, String message);

    void onDelete(boolean isSuccess, String contact, String message);
}
