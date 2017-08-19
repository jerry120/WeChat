package com.example.administrator.wechat;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.example.administrator.wechat.adapter.MyEMCallback;
import com.example.administrator.wechat.event.ContactUpdateEvent;
import com.example.administrator.wechat.utils.ThreadFactory;
import com.example.administrator.wechat.utils.ToastUtil;
import com.example.administrator.wechat.view.BaseActivity;
import com.example.administrator.wechat.view.ChatActivity;
import com.example.administrator.wechat.view.LoginActivity;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/8/9.
 */

public class WeChatApplication extends Application {

    public static final String TAG = WeChatApplication.class.getSimpleName();
    private ActivityManager mActivityManager;
    private SoundPool mSoundPool;
    private int mDuan;
    private int mYulu;
    private NotificationManager mNotificationManager;
    private Bitmap mLogo;
    private List<BaseActivity> mBaseActivityList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();


        sContext = this;
        //初始化
        initHuanXin();
        initLeanCloud();
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);
        mSoundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        //返回加载的音效的id
        mDuan = mSoundPool.load(this, R.raw.duan, 1);
        mYulu = mSoundPool.load(this, R.raw.yulu, 1);

        mLogo = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
    }

    public void addActivity(BaseActivity activity){
        if (!mBaseActivityList.contains(activity)) {
           mBaseActivityList.add(activity);
        }
    }

    public void removeActivity(BaseActivity activity){
        mBaseActivityList.remove(activity);
    }

    private void initLeanCloud() {
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this, "qneskAIIhiHUpEda88TROEPR-gzGzoHsz", "U8lzxKBvRDtuiPSNADWy7MTU");
        AVOSCloud.setDebugLogEnabled(true);
    }

    private void initHuanXin() {

        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
// 如果APP启用了远程的service，此application:onCreate会被调用2次
// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
// 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null || !processAppName.equalsIgnoreCase(getPackageName())) {
            Log.e(TAG, "enter the service process!");

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
        //监听好友状态事件
        initialContactListener();
        //监听接受消息
        initMessageListener();
        initConnectionListener();

    }

    private void initConnectionListener() {
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected(int i) {
                if (i == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    //帐号异地登录
                    start2LoginActivity();
                    ThreadFactory.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {

                            ToastUtil.showToast("您的账号异地登录");
                        }
                    });
                } else if (i == EMError.USER_REMOVED) {
                    //帐号被服务端删除
                    start2LoginActivity();
                    ThreadFactory.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {

                            ToastUtil.showToast("您的账号被服务端删除");

                        }
                    });
                }
            }
        });
    }

    private void start2LoginActivity() {

        //跳转前销毁当前任务栈中的所有其他activity
        for (BaseActivity activity : mBaseActivityList) {
            activity.finish();
        }

        mBaseActivityList.clear();

        Intent intent = new Intent(WeChatApplication.this, LoginActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void initMessageListener() {
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息
                final EMMessage emMessage = messages.get(0);
                if (emMessage.getType() == EMMessage.Type.IMAGE) {

                    final EMImageMessageBody imageMessageBody = (EMImageMessageBody) emMessage.getBody();
                    //监听缩略图是否已经下载完成了,如果下载完成,再发送消息
                    emMessage.setMessageStatusCallback(new MyEMCallback() {
                        @Override
                        public void onMainSuccess() {
                            if (imageMessageBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.SUCCESSED) {
                                //使用EventBus将该消息发送出去
                                EventBus.getDefault().post(emMessage);
                                playSoundAndNotification(emMessage);
                            }
                        }

                        @Override
                        public void onMainError(int code, String message) {
                            Log.d(TAG, "onMainError: 缩略图下载失败：" + message);
                        }
                    });
                } else if (emMessage.getType() == EMMessage.Type.TXT) {
                    //使用EventBus将该消息发送出去
                    EventBus.getDefault().post(emMessage);
                    playSoundAndNotification(emMessage);
                }


            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageRecalled(List<EMMessage> messages) {
                //消息被撤回
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        });


        //记得在不需要的时候移除listener，如在activity的onDestroy()时
        //EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

    //显示通知栏和播放声音
    private void playSoundAndNotification(EMMessage emMessage) {
        if (isRunningBackground()) {

            String msg = "未知";
            if (emMessage.getType() == EMMessage.Type.TXT) {
                EMTextMessageBody textMessageBody = (EMTextMessageBody) emMessage.getBody();
                msg = textMessageBody.getMessage();
            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                msg = "图片";
            }
            //播放长音效
            //弹通知栏
            mSoundPool.play(mYulu, 1, 1, 0, 0, 1);

            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent chatIntent = new Intent(this, ChatActivity.class);
            chatIntent.putExtra("contact", emMessage.getUserName());
            Intent[] intents = {mainIntent, chatIntent};

            PendingIntent pendingIntent = PendingIntent.getActivities(this, 1, intents, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.new_msg))//通知的标题
                    .setContentText(msg)//通知的正文
                    .setContentInfo(emMessage.getFrom())//通知的右下角的额外信息(联系人)
                    .setSmallIcon(R.mipmap.message)//小图标必须写
                    .setLargeIcon(mLogo)//大图标
                    .setPriority(Notification.PRIORITY_MAX)
                    .setAutoCancel(true)//当点击跳转是自动销毁
                    .setContentIntent(pendingIntent)
                    .build();

            mNotificationManager.notify(1, notification);
        } else {
            //播放短音效
            mSoundPool.play(mDuan, 1, 1, 0, 0, 1);
        }
    }

    private boolean isRunningBackground() {
        //获取前100个正在运行的任务栈
        List<ActivityManager.RunningTaskInfo> runningTasks = mActivityManager.getRunningTasks(100);
        //获取最上面的任务栈
        ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
        //获取这个任务栈中最顶层的activity
        ComponentName topActivity = runningTaskInfo.topActivity;
        //获取最顶端activity的包名
        String packageName = topActivity.getPackageName();

        return !packageName.equals(getPackageName());
    }

    private void initialContactListener() {
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
            @Override
            public void onContactAdded(String contact) {
                //增加了联系人时回调此方法
                EventBus.getDefault().post(new ContactUpdateEvent(contact, true));

            }

            @Override
            public void onContactDeleted(String contact) {
                //被删除时回调此方法
                EventBus.getDefault().post(new ContactUpdateEvent(contact, false));
            }

            @Override
            public void onContactInvited(final String username, String reason) {
                //收到好友邀请
                //直接同意
                ThreadFactory.runOnSubThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            EMClient.getInstance().contactManager().acceptInvitation(username);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFriendRequestAccepted(String contact) {
                //好友请求被同意
            }

            @Override
            public void onFriendRequestDeclined(String contact) {
                //好友请求被拒绝
            }
        });
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    private static Context sContext;

    public static Context getWeChatApplication() {
        return sContext;
    }
}
