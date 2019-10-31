package com.sannmizu.nearby_alumni.MiPush;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.sannmizu.nearby_alumni.NetUtils.ChatResponse;
import com.sannmizu.nearby_alumni.NetUtils.ConnectResponse;
import com.sannmizu.nearby_alumni.NetUtils.LoginResponse;
import com.sannmizu.nearby_alumni.NetUtils.Net;
import com.sannmizu.nearby_alumni.NetUtils.RegisterResponse;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.bottomNavigation.MineFragment;
import com.sannmizu.nearby_alumni.utils.AESUtils;
import com.sannmizu.nearby_alumni.utils.JsonConverterFactory;
import com.sannmizu.nearby_alumni.utils.MD5Utils;
import com.sannmizu.nearby_alumni.utils.RSAUtils;
import com.sannmizu.nearby_alumni.utils.Utils;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class InternetDemo extends AppCompatActivity {
    public static List<String> logList = new CopyOnWriteArrayList<String>();

    private TextView mLogView = null;
    private ProgressBar progressBar = null;
    private Button button1,button2,button3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_demo);
        NearbyApplication.setInternetDemo(this);
        mLogView = findViewById(R.id.log);
        button1 = findViewById(R.id.registerDemo);
        button2 = findViewById(R.id.loginDemo);
        button3 = findViewById(R.id.chatDemo);
        progressBar = findViewById(R.id.progress_bar);

       button1.setOnClickListener(v->{
            LayoutInflater inflater = getLayoutInflater();
            View view= inflater.inflate(R.layout.register_demo, null);
            new AlertDialog.Builder(InternetDemo.this)
                    .setTitle("注册")
                    .setView(view)
                    .setPositiveButton("确认",(dialog, which)->{
                        EditText telEdit = view.findViewById(R.id.demo_tel);
                        EditText nicknameEdit = view.findViewById(R.id.demo_nickname);
                        EditText pwdEdit = view.findViewById(R.id.demo_pwd);

                        String tel = telEdit.getText().toString();
                        String name = nicknameEdit.getText().toString();
                        String pwd = pwdEdit.getText().toString();

                        //创建json数据，建议封装成静态方法到对应的类中
                        String requestStr = RegisterResponse.getRequestStr("tel", tel, pwd, name);

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(Net.BaseHost)
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .build();
                        RegisterResponse.RegisterService rxService = retrofit.create(RegisterResponse.RegisterService.class);

                        rxService.register(requestStr)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<RegisterResponse>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(RegisterResponse registerResponse) {
                                        if(registerResponse.getCode() == 0) {
                                            logList.add("注册成功，id为" + registerResponse.getData().getUser_id());
                                        } else {
                                            logList.add("注册失败，原因：" + registerResponse.getReason());
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        logList.add("失败，原因：" + e.getMessage());
                                        refreshLogInfo();
                                    }

                                    @Override
                                    public void onComplete() {
                                        refreshLogInfo();
                                    }
                                });
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
        button2.setOnClickListener(v->{
            LayoutInflater inflater = getLayoutInflater();
            View view= inflater.inflate(R.layout.login_demo, null);
            new AlertDialog.Builder(InternetDemo.this)
                    .setTitle("登陆")
                    .setView(view)
                    .setPositiveButton("确认",(dialog, which)->{
                        progressBar.setVisibility(View.VISIBLE);

                        EditText telEdit = view.findViewById(R.id.demo_tel);
                        EditText pwdEdit = view.findViewById(R.id.demo_pwd);
                        String timestamp = Long.toString(new Date().getTime() / 1000);
                        String tel = telEdit.getText().toString();
                        String pwd = pwdEdit.getText().toString();
                        String regid = MiPushClient.getRegId(this);

                        //创建json数据，建议封装成静态方法到对应的类中
                        JsonObject requestRoot = new JsonObject();
                        requestRoot.addProperty("type", "tel");
                        requestRoot.addProperty("timestamp", timestamp);
                        JsonObject requestData = new JsonObject();
                        requestData.addProperty("account", tel);
                        requestData.addProperty("pwd",pwd);
                        requestData.addProperty("sign", MD5Utils.md5(timestamp+tel+pwd));
                        requestData.addProperty("regid",regid);
                        requestRoot.add("data", requestData);

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(Net.BaseHost)
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .build();
                        LoginResponse.LoginService service1 = retrofit.create(LoginResponse.LoginService.class);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InternetDemo.this);
                        final SharedPreferences.Editor editor = sharedPreferences.edit();
                        service1.login(RSAUtils.encrypt(requestRoot.toString()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())
                                .flatMap(new Function<LoginResponse, ObservableSource<ConnectResponse>>() {
                                    @Override
                                    public ObservableSource<ConnectResponse> apply(LoginResponse loginResponse) throws Exception {
                                        int id = loginResponse.getData().getId();
                                        String logToken = loginResponse.getData().getLogToken();
                                        String expire_time = loginResponse.getData().getExpire_time();
                                        //store(id, logToken, expire_time);数据库中存下logToken和expire_time，
                                        // 之后在logToken没有过期，且没有切换账号时，就不需要再登录了
                                        editor.putInt("currentUser",id);
                                        editor.putString("logToken",logToken);
                                        editor.putString("expire_time",expire_time);
                                        editor.putString("logToken", loginResponse.getData().getLogToken());
                                        editor.apply();
                                        InternetDemo.logList.add("登录成功：id："+id);
                                        //建议登陆之后马上与服务器建立私密链接
                                        String info = "随便写";
                                        //这个密钥用来解密服务器返回的数据
                                        String key = Utils.getRandomString(16);
                                        String iv = Utils.getRandomString(16);
                                        //马上存入数据库
                                        editor.putString(InternetDemo.this.getString(R.string.connect_aes_key), key);
                                        editor.putString(InternetDemo.this.getString(R.string.connect_aes_iv), iv);
                                        editor.apply();
                                        String requestStr = "{\"info\":\"" + info + "\", \"key\":\"" + key + "\", \"iv\":\"" + iv + "\", \"sign\":\"" + MD5Utils.md5(info + key + iv) + "\"}";

                                        Retrofit retrofit = new Retrofit.Builder()
                                                .baseUrl(Net.BaseHost)
                                                .addConverterFactory(JsonConverterFactory.create(InternetDemo.this)) //要传入一个Context
                                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                                .build();
                                        ConnectResponse.ConnectService service = retrofit.create(ConnectResponse.ConnectService.class);
                                        return service.connect(logToken, RSAUtils.encrypt(requestStr));
                                    }
                                })
                                .subscribe(new Observer<ConnectResponse>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(ConnectResponse connectResponse) {
                                        if(connectResponse.getCode() == 0) {
                                            InternetDemo.logList.add("私密连接成功");
                                            editor.putString(InternetDemo.this.getString(R.string.connect_aes_key), connectResponse.getData().getAes().getKey());
                                            editor.putString(InternetDemo.this.getString(R.string.connect_aes_iv), connectResponse.getData().getAes().getIv());
                                            editor.putString("connToken", connectResponse.getData().getToken().getValue());
                                            editor.apply();
                                            startActivity(new Intent(InternetDemo.this, MineFragment.class));
                                        } else {
                                            InternetDemo.logList.add("私密连接失败："+connectResponse.getReason());
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        InternetDemo.logList.add("登录失败："+e.getMessage());
                                        NearbyApplication.getHandler().sendEmptyMessage(1);
                                    }

                                    @Override
                                    public void onComplete() {
                                        NearbyApplication.getHandler().sendEmptyMessage(1);
                                    }
                                });

                        progressBar.setVisibility(View.GONE);
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        button3.setOnClickListener(v->{
            final EditText editText = new EditText(InternetDemo.this);
            new AlertDialog.Builder(InternetDemo.this)
                    .setTitle("自发自接测试")
                    .setView(editText)
                    .setPositiveButton("确定", (dialog, which)->{
                        String text = editText.getText().toString();
                        String jsonStr = "{\"content\":\"测试数据:"+ text +"\"}";
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        int currentUser = sharedPreferences.getInt("currentUser",0);
                        String logToken = sharedPreferences.getString("logToken", "null");
                        String connToken = sharedPreferences.getString("connToken", "null");
                        if(currentUser == 0 || logToken == "null") {    //其实还要判断logToken是否失效
                            runOnUiThread(()->{
                                Toast.makeText(InternetDemo.this, "请先登录", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(Net.BaseHost)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                    .build();
                            ChatResponse.ChatService service = retrofit.create(ChatResponse.ChatService.class);
                            String encrypted = AESUtils.encryptFromLocal(jsonStr);
                            if(encrypted == "" || connToken == "null") {  //其实还要判断connToken是否失效
                                runOnUiThread(()->{
                                    Toast.makeText(InternetDemo.this, "请先建立私密链接", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                service.chat(currentUser, encrypted, logToken, connToken)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<ChatResponse>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onNext(ChatResponse chatResponse) {
                                                if(chatResponse.getCode() == 0) {
                                                    logList.add(chatResponse.getData().getState());
                                                } else {
                                                    logList.add(chatResponse.getReason());
                                                }
                                                refreshLogInfo();
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                logList.add("异常错误");
                                                refreshLogInfo();
                                            }

                                            @Override
                                            public void onComplete() {

                                            }
                                        });
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
    }

    public void refreshLogInfo() {
        String AllLog = "";
        for (String log : logList) {
            AllLog = AllLog + log + "\n\n";
        }
        mLogView.setText(AllLog);
    }
}
