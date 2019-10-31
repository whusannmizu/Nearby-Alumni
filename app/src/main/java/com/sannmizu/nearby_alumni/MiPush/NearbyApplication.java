package com.sannmizu.nearby_alumni.MiPush;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.sannmizu.nearby_alumni.Locate;
import com.sannmizu.nearby_alumni.utils.SharedPreUtils;
import com.sannmizu.nearby_alumni.utils.Util;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.litepal.LitePal;

import java.util.List;

/**
 * 1、为了打开客户端的日志，便于在开发过程中调试，需要自定义一个 Application。
 * 并将自定义的 application 注册在 AndroidManifest.xml 文件中。<br/>
 * 2、为了提高 push 的注册率，您可以在 Application 的 onCreate 中初始化 push。你也可以根据需要，在其他地方初始化 push。
 *
 * @author wangkuiwei
 */
public class NearbyApplication extends Application {

    // user your appid the key.
    private static final String APP_ID = "2882303761518112808";
    // user your appid the key.
    private static final String APP_KEY = "5261811237808";

    // 此TAG在adb logcat中检索自己所需要的信息， 只需在命令行终端输入 adb logcat | grep
    // sannmizu.nearby_alumni
    public static final String TAG = "sannmizu.nearby_alumni";

    private static DemoHandler sHandler = null;
    private static InternetDemo sMainActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        //数据库
        LitePal.initialize(this);
        LitePal.getDatabase();
        //Utils
        Util.initialize(this);
        //Baidu
        SDKInitializer.initialize(this);
        Locate.initialize(this);
        // 注册push服务，注册成功后会向MessageReceiver发送广播
        // 可以从MessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }

        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
        if (sHandler == null) {
            sHandler = new DemoHandler(getApplicationContext());
        }
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public static void reInitPush(Context ctx) {
        MiPushClient.registerPush(ctx.getApplicationContext(), APP_ID, APP_KEY);
    }

    public static DemoHandler getHandler() {
        return sHandler;
    }

    public static void setInternetDemo(InternetDemo activity) {
        sMainActivity = activity;
    }

    public static class DemoHandler extends Handler {

        private Context context;

        public DemoHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            if(msg == null){
                sMainActivity.refreshLogInfo();
            } else {
                String s = (String) msg.obj;
                if (sMainActivity != null) {
                    sMainActivity.refreshLogInfo();
                }
                if (!TextUtils.isEmpty(s)) {
                    Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}