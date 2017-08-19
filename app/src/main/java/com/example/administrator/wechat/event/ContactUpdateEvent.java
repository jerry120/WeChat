package com.example.administrator.wechat.event;

/**
 * Created by Administrator on 2017/8/12.
 */

public class ContactUpdateEvent {
    public String username;
    public boolean isAdded;

    public ContactUpdateEvent(String username, boolean isAdded) {
        this.username = username;
        this.isAdded = isAdded;
    }

    public ContactUpdateEvent() {
    }
}
