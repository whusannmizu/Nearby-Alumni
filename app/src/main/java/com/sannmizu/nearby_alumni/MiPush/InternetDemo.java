package com.sannmizu.nearby_alumni.MiPush;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.sannmizu.nearby_alumni.NetUtils.MyResponse;
import com.sannmizu.nearby_alumni.NetUtils.ConnectResponse;
import com.sannmizu.nearby_alumni.NetUtils.LoginResponse;
import com.sannmizu.nearby_alumni.NetUtils.RegisterResponse;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.Utils.AESUtils;
import com.sannmizu.nearby_alumni.Utils.JsonConverterFactory;
import com.sannmizu.nearby_alumni.Utils.MD5Utils;
import com.sannmizu.nearby_alumni.Utils.RSAUtils;
import com.sannmizu.nearby_alumni.Utils.Utils;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
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
                        String timestamp = Long.toString(new Date().getTime() / 1000);
                        String tel = telEdit.getText().toString();
                        String name = nicknameEdit.getText().toString();
                        String pwd = pwdEdit.getText().toString();

                        //创建json数据，建议封装成静态方法到对应的类中
                        JsonObject requestRoot = new JsonObject();
                        requestRoot.addProperty("type", "tel");
                        requestRoot.addProperty("timestamp", timestamp);
                        JsonObject requestData = new JsonObject();
                        requestData.addProperty("account", tel);
                        requestData.addProperty("nickname", name);
                        requestData.addProperty("pwd", pwd);
                        requestData.addProperty("sign", MD5Utils.md5(timestamp+tel+name+pwd));
                        requestRoot.add("data", requestData);

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(this.getString(R.string.ServerBaseUrl))
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        String encodeStr = RSAUtils.encrypt(requestRoot.toString());
                        RegisterResponse.RegisterService service = retrofit.create(RegisterResponse.RegisterService.class);
                        Call<RegisterResponse> call = service.register(encodeStr);
                        //异步请求
                        call.enqueue(new Callback<RegisterResponse>() {
                            @Override
                            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                                if(response.body().getCode() == 0) {
                                    logList.add("注册成功，id为" + response.body().getData().getUser_id());
                                } else {
                                    logList.add("注册失败，原因" + response.body().getReason());
                                }
                                refreshLogInfo();
                            }

                            @Override
                            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                                logList.add("异常错误");
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

                        Retrofit retrofit1 = new Retrofit.Builder()
                                .baseUrl(this.getString(R.string.ServerBaseUrl))
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        LoginResponse.LoginService service1 = retrofit1.create(LoginResponse.LoginService.class);
                        Call<LoginResponse> call1 = service1.login(RSAUtils.encrypt(requestRoot.toString()));
                        //同步请求，记得start()
                        new Thread(()->{
                            try {
                                LoginResponse response = call1.execute().body();
                                if(response.getCode() == 0) {
                                    String id = response.getData().getId();
                                    String logToken = response.getData().getLogToken();
                                    String expire_time = response.getData().getExpire_time();
                                    //store(id, logToken, expire_time);数据库中存下logToken和expire_time，
                                    // 之后在logToken没有过期，且没有切换账号时，就不需要再登录了
                                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("id",id);
                                    editor.putString("logToken",logToken);
                                    editor.putString("expire_time",expire_time);
                                    editor.putString("logToken", response.getData().getLogToken());
                                    editor.apply();
                                    InternetDemo.logList.add("登录成功：id："+id);

                                    //建议登陆之后马上与服务器建立私密链接
                                    String info = "随便写";
                                    //这个密钥用来解密服务器返回的数据
                                    String key = Utils.getRandomString(16);
                                    String iv = Utils.getRandomString(16);
                                    String requestStr2 = "{\"info\":\"" + info + "\", \"key\":\"" + key + "\", \"iv\":\"" + iv + "\", \"sign\":\"" + MD5Utils.md5(info + key + iv) + "\"}";
                                    //把key和iv存在SharedPreferences中，这样就可以使用自定义的转换器解密并反序列化了
                                    editor.putString(this.getString(R.string.connect_aes_key), key);
                                    editor.putString(this.getString(R.string.connect_aes_iv), iv);
                                    editor.apply();

                                    Retrofit retrofit2 = new Retrofit.Builder()
                                            .baseUrl(this.getString(R.string.ServerBaseUrl))
                                            .addConverterFactory(JsonConverterFactory.create(this)) //要传入一个Context
                                            .build();
                                    ConnectResponse.ConnectService service2 = retrofit2.create(ConnectResponse.ConnectService.class);
                                    Call<ConnectResponse> call2 = service2.connect(logToken, RSAUtils.encrypt(requestStr2));
                                    //同步请求
                                    ConnectResponse responseBody = call2.execute().body();

                                    if(responseBody.getCode() == 0) {
                                        InternetDemo.logList.add("私密连接成功");
                                        editor.putString(this.getString(R.string.connect_aes_key), responseBody.getData().getAes().getKey());
                                        editor.putString(this.getString(R.string.connect_aes_iv), responseBody.getData().getAes().getIv());
                                        editor.putString("connToken", responseBody.getData().getToken().getValue());
                                        editor.apply();
                                    } else {
                                        InternetDemo.logList.add("私密连接失败："+responseBody.getReason());
                                    }
                                } else {
                                    InternetDemo.logList.add("登录失败："+response.getReason());
                                }
                                NearbyApplication.getHandler().sendEmptyMessage(1);
                            } catch (IOException e) {
                                e.printStackTrace();;
                            }
                        }).start();
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
                            String id = sharedPreferences.getString("id","null");
                            String logToken = sharedPreferences.getString("logToken", "null");
                            String connToken = sharedPreferences.getString("connToken", "null");
                            if(id == "null" || logToken == "null") {    //其实还要判断logToken是否失效
                                runOnUiThread(()->{
                                    Toast.makeText(InternetDemo.this, "请先登录", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(this.getString(R.string.ServerBaseUrl))
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                ChatResponse.ChatService service = retrofit.create(ChatResponse.ChatService.class);
                                String encrypted = AESUtils.encryptFromLocal(jsonStr, InternetDemo.this);
                                if(encrypted == "" || connToken == "null") {  //其实还要判断connToken是否失效
                                    runOnUiThread(()->{
                                        Toast.makeText(InternetDemo.this, "请先建立私密链接", Toast.LENGTH_SHORT).show();
                                    });
                                } else {
                                    Call<ChatResponse> call = service.chat(id, encrypted, logToken, connToken);
                                    call.enqueue(new Callback<ChatResponse>() {
                                        @Override
                                        public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                                            if(response.body().getCode() == 0) {
                                                logList.add(response.body().getData().getState());
                                            } else {
                                                logList.add(response.body().getReason());
                                            }
                                            refreshLogInfo();
                                        }

                                        @Override
                                        public void onFailure(Call<ChatResponse> call, Throwable t) {
                                            logList.add("异常错误");
                                            refreshLogInfo();
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
