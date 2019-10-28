package com.sannmizu.nearby_alumni.MiPush;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.sannmizu.nearby_alumni.Database.ChatRecord;
import com.sannmizu.nearby_alumni.MiPush.Bean.ChatBean;
import com.sannmizu.nearby_alumni.MiPush.Bean.FriendReBean;
import com.sannmizu.nearby_alumni.chat.NewMsgBean;
import com.sannmizu.nearby_alumni.chat.RecordObject;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 1、PushMessageReceiver 是个抽象类，该类继承了 BroadcastReceiver。<br/>
 * 2、需要将自定义的 MessageReceiver 注册在 AndroidManifest.xml 文件中：
 * <pre>
 * {@code
 *  <receiver
 *      android:name="MessageReceiver"
 *      android:exported="true">
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.ERROR" />
 *      </intent-filter>
 *  </receiver>
 *  }</pre>
 * 3、MessageReceiver 的 onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。<br/>
 * 4、MessageReceiver 的 onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发。<br/>
 * 5、MessageReceiver 的 onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。<br/>
 * 6、MessageReceiver 的 onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。<br/>
 * 7、MessageReceiver 的 onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。<br/>
 * 8、以上这些方法运行在非 UI 线程中。
 *
 * @author mayixiang
 */
public class MessageReceiver extends PushMessageReceiver {

    private String mRegId;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;

    private static final int NEW_MESSAGE = 1;
    private static final int NEW_FRIEND = 2;
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Log.v(NearbyApplication.TAG,
                "onReceivePassThroughMessage is called. " + message.toString());
        Gson gson = new Gson();
        String content = message.getContent();
        switch(message.getNotifyId()) {
            //好友申请通知
            case NEW_FRIEND:
                FriendReBean request = gson.fromJson(content, FriendReBean.class);
                //TODO:通知
                break;
        }
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.v(NearbyApplication.TAG,
                "onNotificationMessageClicked is called. " + message.toString());
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.v(NearbyApplication.TAG,
                "onNotificationMessageArrived is called. " + message.toString());
       //解析数据
        Gson gson = new Gson();
        String content = message.getContent();
        switch(message.getNotifyId()) {
            //新消息通知
            case NEW_MESSAGE:
                ChatBean information = gson.fromJson(content, ChatBean.class);
                //存进数据库
                SharedPreferences sp = context.getSharedPreferences("currentChatList", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                NewMsgBean bean = new NewMsgBean(information.getData().getTime(), information.getData().getContent());
                editor.putString(String.valueOf(information.getData().getFromId()), bean.toString());
                editor.apply();

                ChatRecord chatRecord = new ChatRecord(information.getData());
                LitePal.initialize(context);
                boolean b = chatRecord.save();
                //广播通知
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", chatRecord.getUser_id());
                bundle.putInt("friend_id", chatRecord.getFriend_id());
                bundle.putParcelable("message", new RecordObject(chatRecord));
                Intent intent = new Intent("sannmizu.chat.NEW_MESSAGE");
                intent.putExtras(bundle);
                context.sendBroadcast(intent);
                break;
        }
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.v(NearbyApplication.TAG,
                "onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                Log.v("regID", mRegId);
            } else {

            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            } else {

            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            } else {

            }
        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
            } else {

            }
        } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
            } else {

            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            } else {

            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            } else {

            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            } else {

            }
        } else {
            log = message.getReason();
        }

    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.v(NearbyApplication.TAG,
                "onReceiveRegisterResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
            } else {

            }
        } else {
            log = message.getReason();
        }

    }

    @Override
    public void onRequirePermissions(Context context, String[] permissions) {
        super.onRequirePermissions(context, permissions);
        Log.e(NearbyApplication.TAG,
                "onRequirePermissions is called. need permission" + arrayToString(permissions));

        if (Build.VERSION.SDK_INT >= 23 && context.getApplicationInfo().targetSdkVersion >= 23) {
            Intent intent = new Intent();
            intent.putExtra("permissions", permissions);
            intent.setComponent(new ComponentName(context.getPackageName(), PermissionActivity.class.getCanonicalName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }

    public String arrayToString(String[] strings) {
        String result = " ";
        for (String str : strings) {
            result = result + str + " ";
        }
        return result;
    }
}

