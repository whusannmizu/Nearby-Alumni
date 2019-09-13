package com.sannmizu.nearby_alumni.denglu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.sannmizu.nearby_alumni.NetUtils.MyResponse;
import com.sannmizu.nearby_alumni.NetUtils.Net;
import com.sannmizu.nearby_alumni.NetUtils.addresponse;
import com.sannmizu.nearby_alumni.NetUtils.friendResponse;
import com.sannmizu.nearby_alumni.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class guanzhu extends AppCompatActivity implements View.OnClickListener,MyOneLineView.OnRootClickListener{
    private MyOneLineView gone,gtwo;
    private SharedPreferences spref;
    private SharedPreferences.Editor seditor;
    private ImageView opicture,obackground;
    private ScrollView scrollView;
    private TextView gtext1,gtext2,gt1,gt2;
    private Button gbutton1,gbutton2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guanzhu);
        scrollView=(ScrollView)findViewById(R.id.gbeijing);
        opicture=findViewById(R.id.opicture);
        obackground=findViewById(R.id.obackground);
        gbutton1=findViewById(R.id.gbutton1);
        gbutton1.setOnClickListener(this);
        gbutton2=findViewById(R.id.gbutton2);
        gbutton2.setOnClickListener(this);
        gbutton1.setText("发消息");
        //gbutton2.setText("添加好友");
        juge();
        spref= PreferenceManager.getDefaultSharedPreferences(this);
        gone=findViewById(R.id.gone);
        gone.init("设置备注").setOnRootClickListener(this,1);
        gone.setTextContentSize(18);
        gone.setRootPaddingLeftRight(20,-60);
        gtwo=findViewById(R.id.gtwo);
        gtwo.init("详细资料").setOnRootClickListener(this,2);
        gtwo.setTextContentSize(18);
        gtwo.setRootPaddingLeftRight(20,-60);
        gtext1=findViewById(R.id.gtext1);
        gtext2=findViewById(R.id.gtext2);
        gt1=findViewById(R.id.gt1);
        gt1.setText(R.string.qianming);
        gt2=findViewById(R.id.gt2);
        String text3=spref.getString("qianming",null);
        if (text3!=null)
            gt2.setText(text3);
        String text1=spref.getString("note",null);
        if (text1!=null)
        {
            gtext1.setText(text1);
        }
        String text2=spref.getString("diqu",null);
        if (text2!=null)
        {
            gtext2.setText(text2);
        }
        Toolbar toolbar=(Toolbar)findViewById(R.id.gtoolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        String uri=spref.getString("imagepath",null);
        if (uri!=null)
        {
            Bitmap bitmap=BitmapFactory.decodeFile(uri);
            opicture.setImageBitmap(bitmap);
        }
        /*String uri1=spref.getString("bing_pic",null);
        if (uri1!=null)
        {
            Glide.with(this).load(uri1).into(obackground);
        }*/
    }

    @Override
    public void onRootClick(View view) {
        switch ((int)view.getTag()){
            case 1:
                startActivity(new Intent(guanzhu.this,beizhu.class));
                break;
            case 2:
                startActivity(new Intent(guanzhu.this,gerenziliao.class));
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.gbutton1:
                break;
            case R.id.gbutton2:
                String friend= String.valueOf(gbutton2.getText());
                if (friend=="添加好友") {
                    Toast.makeText(guanzhu.this, "添加好友", Toast.LENGTH_SHORT).show();
                    addfriend();
                }
                else
                    deletefriend();
                break;
        }
    }
    public void juge(){
        spref=PreferenceManager.getDefaultSharedPreferences(this);
        String logToken = spref.getString("logToken", null);
        //String userid=spref.getString("userId",null);
        String userid = "10007";
        if (logToken == "null") {    //其实还要判断logToken是否失效
            runOnUiThread(() -> {
                Toast.makeText(guanzhu.this, "请先登录", Toast.LENGTH_SHORT).show();
            });
        } else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Net.BaseHost)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            friendResponse.friendService service=retrofit.create(friendResponse.friendService.class);
            retrofit2.Call<friendResponse>call=service.friend(logToken);
            call.enqueue(new retrofit2.Callback<friendResponse>() {
                @Override
                public void onResponse(retrofit2.Call<friendResponse> call, retrofit2.Response<friendResponse> response) {
                    Log.d("gqw", String.valueOf(response.body().getCode()));
                    //gbutton2.setText("连接成功");
                    if (response.body().getCode()==0)
                    {
                        /*if (userid==response.body().getData().getFriends().get(1).getId())
                        {
                            gbutton2.setText("删除好友");
                        }
                        else
                        {
                            gbutton2.setText("添加好友");
                        }*/
                        gbutton2.setText("添加好友");
                    }
                    else
                        gbutton2.setText("连接失败");
                }

                @Override
                public void onFailure(retrofit2.Call<friendResponse> call, Throwable t) {

                }
            });
        }
    }
    public void addfriend(){
        spref=PreferenceManager.getDefaultSharedPreferences(this);
        String logToken = spref.getString("logToken", null);
        //String userid=spref.getString("userId",null);
        String userid="10002";
        if (logToken == "null") {    //其实还要判断logToken是否失效
            runOnUiThread(() -> {
                Toast.makeText(guanzhu.this, "请先登录", Toast.LENGTH_SHORT).show();
            });
        } else{
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Net.BaseHost)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            MyResponse.addService service=retrofit.create(MyResponse.addService.class);
            retrofit2.Call<MyResponse>call=service.add(Integer.parseInt(userid),logToken);
            call.enqueue(new Callback<MyResponse>() {
                @Override
                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                    if (response.body().getCode()==0)
                        Log.d("haoyou","添加成功");
                }

                @Override
                public void onFailure(Call<MyResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
    public void deletefriend(){
        spref=PreferenceManager.getDefaultSharedPreferences(this);
        String logToken = spref.getString("logToken", null);
        //String userid=spref.getString("userId",null);
        String userid="10006";
        if (logToken == "null") {    //其实还要判断logToken是否失效
            runOnUiThread(() -> {
                Toast.makeText(guanzhu.this, "请先登录", Toast.LENGTH_SHORT).show();
            });
        } else{
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Net.BaseHost)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MyResponse.deleteService service=retrofit.create(MyResponse.deleteService.class);
            retrofit2.Call<MyResponse>call=service.delete(Integer.parseInt(userid),logToken);
            call.enqueue(new Callback<MyResponse>() {
                @Override
                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                    if (response.body().getCode()==0)
                        Log.d("haoyou","添加成功");
                }

                @Override
                public void onFailure(Call<MyResponse> call, Throwable t) {

                }
            });
        }
    }

}
