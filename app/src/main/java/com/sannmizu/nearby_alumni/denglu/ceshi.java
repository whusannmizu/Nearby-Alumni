package com.sannmizu.nearby_alumni.denglu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.sannmizu.nearby_alumni.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class ceshi extends AppCompatActivity {
    private SharedPreferences spref;
    private SharedPreferences.Editor seditor;
    private ImageView bingPicimg;
    private TextView textView;
    private ScrollView scrollView;
    private moban cone,ctwo,cthree;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ceshi);
        bingPicimg=(ImageView)findViewById(R.id.bing_pic_img);
        scrollView=(ScrollView)findViewById(R.id.beijing);
        cone=findViewById(R.id.cone);
        cone.initmine(R.drawable.ic_gerenziliao,"个人资料");
        ctwo=findViewById(R.id.ctwo);
        ctwo.initmine(R.drawable.ic_wodeguanzhu,"我的关注");
        cthree=findViewById(R.id.cthree);
        cthree.initmine(R.drawable.ic_liulanjilu,"浏览记录");
        spref= PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar=(Toolbar)findViewById(R.id.ctoolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        String bingpic=spref.getString("bing_pic",null);
        if (bingpic!=null){
            Glide.with(this).load(bingpic).into(bingPicimg);
        }
        else {
            loadBingPic();
        }
    }
    private void loadBingPic(){
        String requestBingpic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingpic, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingpic=response.body().string();
                seditor= PreferenceManager.getDefaultSharedPreferences(ceshi.this).edit();
                seditor.putString("bing_pic",bingpic);
                seditor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(ceshi.this).load(bingpic).into(bingPicimg);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}
