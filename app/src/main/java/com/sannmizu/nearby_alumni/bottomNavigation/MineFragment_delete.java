package com.sannmizu.nearby_alumni.bottomNavigation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.sannmizu.nearby_alumni.MiPush.InternetDemo;
import com.sannmizu.nearby_alumni.MiPush.NearbyApplication;
import com.sannmizu.nearby_alumni.NetUtils.ConnectResponse;
import com.sannmizu.nearby_alumni.NetUtils.LoginResponse;
import com.sannmizu.nearby_alumni.NetUtils.Net;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.utils.JsonConverterFactory;
import com.sannmizu.nearby_alumni.utils.MD5Utils;
import com.sannmizu.nearby_alumni.utils.RSAUtils;
import com.sannmizu.nearby_alumni.utils.Utils;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.Date;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MineFragment_delete extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.activity_internet_demo, container, false);
        Button btn = view1.findViewById(R.id.loginDemo);
        ProgressBar progressBar = view1.findViewById(R.id.progress_bar);
        btn.setOnClickListener(v->{
            View view= inflater.inflate(R.layout.login_demo, null);
            new AlertDialog.Builder(getContext())
                    .setTitle("登陆")
                    .setView(view)
                    .setPositiveButton("确认",(dialog, which)->{
                        progressBar.setVisibility(View.VISIBLE);

                        EditText telEdit = view.findViewById(R.id.demo_tel);
                        EditText pwdEdit = view.findViewById(R.id.demo_pwd);
                        String timestamp = Long.toString(new Date().getTime() / 1000);
                        String tel = telEdit.getText().toString();
                        String pwd = pwdEdit.getText().toString();
                        String regid = MiPushClient.getRegId(getContext());

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
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
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
                                        editor.putString(getContext().getString(R.string.connect_aes_key), key);
                                        editor.putString(getContext().getString(R.string.connect_aes_iv), iv);
                                        editor.apply();
                                        String requestStr = "{\"info\":\"" + info + "\", \"key\":\"" + key + "\", \"iv\":\"" + iv + "\", \"sign\":\"" + MD5Utils.md5(info + key + iv) + "\"}";

                                        Retrofit retrofit = new Retrofit.Builder()
                                                .baseUrl(Net.BaseHost)
                                                .addConverterFactory(JsonConverterFactory.create(getContext())) //要传入一个Context
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
                                            editor.putString(getContext().getString(R.string.connect_aes_key), connectResponse.getData().getAes().getKey());
                                            editor.putString(getContext().getString(R.string.connect_aes_iv), connectResponse.getData().getAes().getIv());
                                            editor.putString("connToken", connectResponse.getData().getToken().getValue());
                                            editor.apply();
                                            startActivity(new Intent(getContext(), MineFragment.class));
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
        return view1;
    }
}
