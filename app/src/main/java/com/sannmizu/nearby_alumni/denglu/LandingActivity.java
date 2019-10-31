package com.sannmizu.nearby_alumni.denglu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.sannmizu.nearby_alumni.MiPush.NearbyApplication;
import com.sannmizu.nearby_alumni.NetUtils.ConnectResponse;
import com.sannmizu.nearby_alumni.NetUtils.LoginResponse;
import com.sannmizu.nearby_alumni.NetUtils.Net;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.bottomNavigation.MineFragment;
import com.sannmizu.nearby_alumni.utils.JsonConverterFactory;
import com.sannmizu.nearby_alumni.utils.MD5Utils;
import com.sannmizu.nearby_alumni.utils.RSAUtils;
import com.sannmizu.nearby_alumni.utils.Utils;
import com.sannmizu.nearby_alumni.utils.encoder.BASE64Decoder;
import com.sannmizu.nearby_alumni.utils.encoder.BASE64Encoder;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class LandingActivity extends AppCompatActivity implements View.OnClickListener,View.OnFocusChangeListener,TextWatcher {
    private ImageButton back;
    private LinearLayout loginpull;
    private View loginlayer;
    private LinearLayout loginoptions;
    private LinearLayout Mloginusername;
    private LinearLayout MloginPwd;
    private EditText mrloginusername;
    private EditText mrloginPwd;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private CheckBox remeberpass;
    private ImageButton mloginusername;
    private ImageButton mloginPwd;
    private LinearLayout backbar;
    private Button mSubmit;
    private Button mRegister;
    private TextView mForgetPwd;
    private Toast mToast;
    public static List<String> logList = new CopyOnWriteArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landingactivity);
        initView();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        remeberpass = (CheckBox) findViewById(R.id.cb_remember_login);
        boolean isRemember = pref.getBoolean("remember_password", false);
        if (isRemember) {
            String account = pref.getString("account", "");
            String password = pref.getString("password", "");
            mrloginusername.setText(account);
            mrloginPwd.setText(password);
            remeberpass.setChecked(true);
        }
    }

    //初始化视图
    private void initView() {
        //导航栏和返回键
        backbar = findViewById(R.id.ly_retrieve_bar);
        back = findViewById(R.id.ib_navigation_back);
        //username
        Mloginusername = findViewById(R.id.ll_login_username);
        mrloginusername = findViewById(R.id.et_login_username);
        mloginusername = findViewById(R.id.iv_login_username_del);
        //password
        MloginPwd = findViewById(R.id.ll_login_pwd);
        mrloginPwd = findViewById(R.id.et_login_pwd);
        mloginPwd = findViewById(R.id.iv_login_pwd_del);
        //提交、注册
        mSubmit = findViewById(R.id.bt_login_submit);
        mRegister = findViewById(R.id.bt_login_register);
        //忘记密码
        mForgetPwd = findViewById(R.id.tv_login_forget_pwd);
        //注册点击事件
        mForgetPwd.setOnClickListener(this);
        back.setOnClickListener(this);
        mrloginusername.setOnClickListener(this);
        mloginusername.setOnClickListener(this);
        mSubmit.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mrloginPwd.setOnClickListener(this);
        mloginPwd.setOnClickListener(this);

        mrloginusername.addTextChangedListener(this);
        mrloginPwd.addTextChangedListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String account = pref.getString("account", "");
        if (account != null) {
            mrloginusername.setText(account);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_navigation_back:
                //返回
                finish();
                break;
            case R.id.et_login_username:
                mrloginPwd.clearFocus();
                mrloginusername.setFocusableInTouchMode(true);
                mrloginusername.requestFocus();
                break;
            case R.id.et_login_pwd:
                mrloginusername.clearFocus();
                mrloginPwd.setFocusableInTouchMode(true);
                mrloginPwd.requestFocus();
                break;
            case R.id.iv_login_username_del:
                //清空用户名
                mrloginusername.setText(null);
                break;
            case R.id.iv_login_pwd_del:
                //清空密码
                mrloginPwd.setText(null);
                break;
            case R.id.bt_login_submit:
                //登录
                loginRequest();
                String account = mrloginusername.getText().toString();
                String password = mrloginPwd.getText().toString();
                editor = pref.edit();
                if (remeberpass.isChecked()) {
                    editor.putBoolean("remember_password", true);
                    editor.putString("account", account);
                    editor.putString("password", password);
                } else {
                    editor.clear();
                }
                editor.apply();
                break;
            case R.id.bt_login_register:
                //注册
                // startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                Intent intent = new Intent(LandingActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_login_forget_pwd:
                //忘记密码
                startActivity(new Intent(LandingActivity.this, ForgetPwdActivity.class));
                break;
            default:
                break;
        }
    }

    //用户名密码焦点改变
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();

        if (id == R.id.et_login_username) {
            if (hasFocus) {
                Mloginusername.setActivated(true);
                MloginPwd.setActivated(false);
            }
        } else {
            if (hasFocus) {
                MloginPwd.setActivated(true);
                Mloginusername.setActivated(false);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String username = mrloginusername.getText().toString().trim();
        String pwd = mrloginPwd.getText().toString().trim();
        //是否显示清除按钮
        if (username.length() > 0) {
            mloginusername.setVisibility(View.VISIBLE);
        } else {
            mloginusername.setVisibility(View.INVISIBLE);
        }
        if (pwd.length() > 0) {
            mloginPwd.setVisibility(View.VISIBLE);
        } else {
            mloginPwd.setVisibility(View.INVISIBLE);
        }

        //登录按钮是否可用
        if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(username)) {
            mSubmit.setClickable(true);
        } else {
            mSubmit.setClickable(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    //登录
    private void loginRequest() {
        String timestamp = Long.toString(new Date().getTime() / 1000);
        String tel = mrloginusername.getText().toString();
        String pwd = mrloginPwd.getText().toString();
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LandingActivity.this);
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
                        LandingActivity.logList.add("登录成功：id："+id);
                        //建议登陆之后马上与服务器建立私密链接
                        String info = "随便写";
                        //这个密钥用来解密服务器返回的数据
                        String key = Utils.getRandomString(16);
                        String iv = Utils.getRandomString(16);
                        //马上存入数据库
                        editor.putString(LandingActivity.this.getString(R.string.connect_aes_key), key);
                        editor.putString(LandingActivity.this.getString(R.string.connect_aes_iv), iv);
                        editor.apply();
                        String requestStr = "{\"info\":\"" + info + "\", \"key\":\"" + key + "\", \"iv\":\"" + iv + "\", \"sign\":\"" + MD5Utils.md5(info + key + iv) + "\"}";

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(Net.BaseHost)
                                .addConverterFactory(JsonConverterFactory.create(LandingActivity.this)) //要传入一个Context
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
                            LandingActivity.logList.add("私密连接成功");
                            Log.d("MainActivity","私密连接成功");
                            editor.putString(LandingActivity.this.getString(R.string.connect_aes_key), connectResponse.getData().getAes().getKey());
                            editor.putString(LandingActivity.this.getString(R.string.connect_aes_iv), connectResponse.getData().getAes().getIv());
                            editor.putString("connToken", connectResponse.getData().getToken().getValue());
                            editor.apply();
                            finish();
                        } else {
                            LandingActivity.logList.add("私密连接失败："+connectResponse.getReason());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LandingActivity.logList.add("登录失败："+e.getMessage());

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static String getImageStr(String imgFile)throws IOException{
        InputStream inputStream=null;
        byte[]data=null;
        inputStream=new FileInputStream(imgFile);
        data=new byte[inputStream.available()];
        inputStream.read(data);
        inputStream.close();
        BASE64Encoder encoder=new BASE64Encoder();
        return encoder.encode(data);
    }
    public static boolean generateImage(String imgStr,String path)throws IOException{
        if (imgStr==null){
            return false;
        }
        BASE64Decoder decoder=new BASE64Decoder();
        byte[] b=decoder.decodeBuffer(imgStr);

        for (int i=0;i<b.length;i++){
            if (b[i]<0){
                b[i]+=256;
            }
        }
        OutputStream out=new FileOutputStream(path);
        out.write(b);
        out.flush();
        out.close();
        return true;
    }
}