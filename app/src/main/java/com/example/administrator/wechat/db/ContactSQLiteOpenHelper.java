package com.example.administrator.wechat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.administrator.wechat.WeChatApplication;

/**
 * Created by Administrator on 2017/8/11.
 */

public class ContactSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "contact.db";
    private static final int DB_VERSION = 1;//>=1

    private ContactSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public ContactSQLiteOpenHelper(){
        this(WeChatApplication.getWeChatApplication(),DB_NAME,null,DB_VERSION);

    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table t_contact(_id integer primary key,contact varchar(20),username varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
