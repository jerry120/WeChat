package com.example.administrator.wechat.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public class DBUtils {
    public static List<String> getContact(String username){
        List<String> contactList = new ArrayList<>();
        ContactSQLiteOpenHelper contactSQLiteOpenHelper = new ContactSQLiteOpenHelper();
        SQLiteDatabase database = contactSQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query("t_contact", new String[]{"contact"}, "username = ?", new String[]{username}, null, null, "contact");
        while(cursor.moveToNext()){
            String string = cursor.getString(0);
            contactList.add(string);
        }
        cursor.close();
        database.close();
        contactSQLiteOpenHelper.close();//此行可以不写
        return contactList;
    }

    /**
     * 先删除username的所有的好友,然后重新插入
     * @param contactList
     * @param username
     */
    public static void updateContacts(List<String> contactList, String username) {
        ContactSQLiteOpenHelper contactSQLiteOpenHelper = new ContactSQLiteOpenHelper();
        SQLiteDatabase database = contactSQLiteOpenHelper.getReadableDatabase();

        database.beginTransaction();//开启事务

        //先删除
        database.delete("t_contact","username = ?",new String[]{username});

            ContentValues contentValues = new ContentValues();
            contentValues.put("username",username);
        //在添加
        for (String contact : contactList) {
            contentValues.put("contact",contact);
            database.insert("t_contact",null,contentValues);
        }

        database.setTransactionSuccessful();//设置事务成功,只有改行代码执行完,上面所有对数据库的操作才会一次性全部成功
        database.endTransaction();//结束事务
        database.close();
        contactSQLiteOpenHelper.close();
    }
}
