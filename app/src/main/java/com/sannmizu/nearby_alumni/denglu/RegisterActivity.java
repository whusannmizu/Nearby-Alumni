package com.sannmizu.nearby_alumni.denglu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.sannmizu.nearby_alumni.NetUtils.Net;
import com.sannmizu.nearby_alumni.NetUtils.RegisterResponse;
import com.sannmizu.nearby_alumni.R;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    public static List<String> logList = new CopyOnWriteArrayList<String>();
    private Button register_submit;
    private CheckBox checkBox;
    private EditText telEdit,nicknameEdit,pwdEdit;
    private static String appkey ="2c1f47c68d00d";
    private static String appsecret="2ebdbaedd7f9e080439fd461e7a18ffd";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EditText telEdit =findViewById(R.id.et_register_username);
        setContentView(R.layout.activity_main_register_step_one);
        findViewById(R.id.ib_navigation_back).setOnClickListener(this);
        pref= PreferenceManager.getDefaultSharedPreferences(this);
        initView();
    }
private void initView(){
        register_submit=findViewById(R.id.bt_register_submit);
        register_submit.setOnClickListener(this);
        nicknameEdit = findViewById(R.id.et_registerusername);
        pwdEdit =findViewById(R.id.et_register_pwd_input);
        telEdit =findViewById(R.id.et_register_username);
        telEdit.addTextChangedListener(this);
        register_submit.setClickable(false);
        checkBox=(CheckBox)findViewById(R.id.cb_protocol);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                register_submit.setClickable(b);
            }
        });
}
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.ib_navigation_back:
                finish();
                break;
            case R.id.bt_register_submit:
                //sendmessage();
                sendRequestWithOkHttp();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }
    public boolean isphone(String mobiles){
        Pattern p=Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m=p.matcher(mobiles);
        return m.matches();
    }
    public boolean isemail(String email){
        String str="^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p=Pattern.compile(str);
        Matcher m=p.matcher(email);
        return m.matches();
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String haoma=telEdit.getText().toString().trim();
        boolean isphone=isphone(haoma);
        boolean isemail=isemail(haoma);
        if (isphone==true||isemail==true)
           register_submit.setClickable(true);
    }
    private void sendRequestWithOkHttp(){
                    String timestamp = Long.toString(new Date().getTime() / 1000);
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
                                        pref=PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
                                        editor=pref.edit();
                                        editor.putString("password",pwd);
                                        editor.putString("account", String.valueOf(registerResponse.getData().getUser_id()));
                                        editor.putString("userid",String.valueOf(registerResponse.getData().getUser_id()));
                                        editor.apply();
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
    }
    public void refreshLogInfo() {
        String AllLog = "";
        for (String log : logList) {
            AllLog = AllLog + log + "\n\n";
            Log.d("reg",log);
        }
        startActivity(new Intent(RegisterActivity.this, LandingActivity.class));
    }
}
