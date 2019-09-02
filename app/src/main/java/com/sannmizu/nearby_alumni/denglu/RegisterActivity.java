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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.sannmizu.nearby_alumni.MainActivity;
import com.sannmizu.nearby_alumni.NetUtils.Net;
import com.sannmizu.nearby_alumni.NetUtils.RegisterResponse;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.utils.MD5Utils;
import com.sannmizu.nearby_alumni.utils.RSAUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
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
    //public static final MediaType JSON= MediaType.parse("application/json;charset=utf-8");

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    public static List<String> logList = new CopyOnWriteArrayList<String>();
    private Button register_submit,mimacall;
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
        mimacall=(Button)findViewById(R.id.tv_register_sms_call);
        mimacall.setOnClickListener(this);
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
            case R.id.tv_register_sms_call:
                /*try {
                    SendCode();
                    Log.d("RegisterActivity","mima");
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                //sendmessage();
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
            mimacall.setClickable(true);
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
        startActivity(new Intent(RegisterActivity.this, MainActivity2.class));
    }

     /*public void SendCode() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //发送验证码的请求路径URL
                    String SERVER_URL="https://api.netease.im/sms/sendcode.action";
                    //网易云信分配的账号，请替换你在管理后台应用下申请的Appkey
                    String APP_KEY="bc5fbcf9e011b09b43610385fa003882";
                    //网易云信分配的密钥，请替换你在管理后台应用下申请的appSecret
                    String APP_SECRET="f762fe8e956d";
                    //随机数
                    String NONCE="123456";
                    //短信模板ID
                    String TEMPLATEID="14813421";
                    //手机号，接收者号码列表，JSONArray格式，限制接收者号码个数最多为100个
                    String MOBILE="13098830176";
                    //验证码长度，范围4～10，默认为4
                    String CODELEN="6";
                    //短信参数列表，用于依次填充模板，JSONArray格式，每个变量长度不能超过30字,对于不包含变量的模板，不填此参数表示模板即短信全文内容
                    String PARAMS="['xxxx','xxxx']";

                    String curTime = String.valueOf((new Date()).getTime() / 1000L);
                     String checkSum = CheckSumBuilder.getCheckSum(APP_SECRET, NONCE, curTime);
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(SERVER_URL);

                    // 设置请求的header
                    httpPost.addHeader("AppKey", APP_KEY);
                    httpPost.addHeader("Nonce", NONCE);
                    httpPost.addHeader("CurTime", curTime);
                    httpPost.addHeader("CheckSum", checkSum);
                    httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

                    // 设置请求的的参数，requestBody参数
                    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                    /*
                     * 1.如果是模板短信，请注意参数mobile是有s的，详细参数配置请参考“发送模板短信文档”
                     * 2.参数格式是jsonArray的格式，例如 "['13888888888','13666666666']"
                     * 3.params是根据你模板里面有几个参数，那里面的参数也是jsonArray格式
                     */
                  /*  nvps.add(new BasicNameValuePair("templateid", TEMPLATEID));
                    nvps.add(new BasicNameValuePair("mobile", MOBILE));
                    nvps.add(new BasicNameValuePair("codeLen", CODELEN));

                    httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

                    // 执行请求
                    HttpResponse response = httpClient.execute(httpPost);
                    /*
                     * 1.打印执行结果，打印结果一般会200、315、403、404、413、414、500
                     * 2.具体的code有问题的可以参考官网的Code状态表
                     */
              /*      Log.d("registeractivity",EntityUtils.toString(response.getEntity(), "utf-8"));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
     }*/
    /*private void sendRequest(){
        String tel = telEdit.getText().toString();

        JsonObject code=new JsonObject();
        code.addProperty("mobile",tel);
        code.addProperty("tpl_id",181695);
        code.addProperty("tpl_vable","#code#=1234");
        code.addProperty("key","2f77a1efd798656ae49dfb88fe7a0d9d");
        HttpUtil.sendOkHttpResponse("http://v.juhe.cn/sms/send",code,new okhttp3.Callback(){
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Gson gson=new Gson();
                Data data=gson.fromJson(response.body().toString(),Data.class);
                if (data.getError_code()==0){

                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }
        });
    }
    /*private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Accentnew accentnew=new Accentnew("tel","123456789",new Data("account","nickname","pwd","md5"));
                    Type type=new TypeToken<Accentnew>(){}.getType();
                    String json=new Gson().toJson(accentnew,type);
                    RequestBody requestBody=RequestBody.create(JSON,json);
                    HttpUtil.sendOkHttpResponse("http://api.sannmizu.com/v1/",requestBody,new okhttp3.Callback(){
                        @Override
                        public void onResponse(Call call, Response response) throws IOException{

                        }
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }*/
    /*private void sendmessage(){
        HashMap<String,Object>result=null;
        CCPRestSmsSDK restApi=new CCPRestSmsSDK();
        restApi.init("https://app.cloopen.com","8883");
        restApi.setAccount("8a216da86c8a1a54016ca8e4d16e1327","ce49d48adafe43998234edbf75fd6f4a");
        restApi.setAppId("8a216da86c8a1a54016cb4c667421df8");
        Random ra=new Random();
        ra.nextInt((10000)+1);
        int rb=1;
        String ra1=String.valueOf(ra);
        String rb1=String.valueOf(rb);
        result=restApi.sendTemplateSMS("13098830176","1",new String[]{"ra1","rb1"});
        //System.out.println("SDKTestGetSubAccounts result=" + result);
        Log.d("result",result.toString());
        if ("000000".equals(result.get("statusCode"))){
            HashMap<String,Object>data=(HashMap<String, Object>)result.get("data");
            String mima=(String) data.get(0);
            Log.d("RegisterActivity","验证码为"+mima);
        }
        else
            Log.d("RegisterActivity","错误信息"+result.get("statusMsg"));
    }
    public void send(){
        String accountsid="8a216da86c8a1a54016ca8e4d16e1327";
        String accounttoken="ce49d48adafe43998234edbf75fd6f4a";
        String appid="8a216da86c8a1a54016cb4c667421df8";

        JsonObject request=new JsonObject();
        request.addProperty("to","13098830176");
        request.addProperty("appId",appid);
        request.addProperty("templateId","1");
        Random ra=new Random();
        ra.nextInt((10000)+1);
        int rb=1;
        String ra1=String.valueOf(ra);
        String rb1=String.valueOf(rb);
        JsonObject requestdata=new JsonObject();
        requestdata.addProperty("data",ra1);
        requestdata.addProperty("data",rb1);
        request.add("datas",requestdata);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.getString(R.string.baseurl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }**/

}
