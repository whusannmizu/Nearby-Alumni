package com.sannmizu.nearby_alumni.denglu;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.locate;
import com.sannmizu.nearby_alumni.utils.locateback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class MyActivity extends AppCompatActivity implements MyOneLineView.OnArrowClickListener,MyOneLineView.OnRootClickListener{
 MyOneLineView oneitem,twoitem,threeitem,fouritem,soneitem;
 private SharedPreferences spref;
 private SharedPreferences.Editor seditor;
 TextView snichen,mwode,signtext;
 private ImageView picture,mpicture;
 private LinearLayout mbeijing;
 ImageView mbingmic;
 int imageSize, radius;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myactivity);
        spref= PreferenceManager.getDefaultSharedPreferences(this);
        mbeijing=(LinearLayout)findViewById(R.id.mbeijing);
        oneitem=(MyOneLineView)findViewById(R.id.one_item);
        twoitem=(MyOneLineView)findViewById(R.id.two_item);
        threeitem=(MyOneLineView)findViewById(R.id.three_item);
        fouritem=(MyOneLineView)findViewById(R.id.four_item);
        soneitem=(MyOneLineView)findViewById(R.id.sone_item);
        snichen=(TextView)findViewById(R.id.nichen);
        signtext=findViewById(R.id.signtext);
        mwode=(TextView)findViewById(R.id.mwode);
        mpicture=(ImageView)findViewById(R.id.mpicture);
        mbingmic=findViewById(R.id.mbing_pic_img);
       //mbingPicimg=(ImageView)findViewById(R.id.mbing_pic_img);
        //mbejing=(ScrollView)findViewById(R.id.mbeijing);
        Toolbar toolbar=(Toolbar)findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            //actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        String bingpic=spref.getString("bing_pic",null);
        if (bingpic!=null){
            Glide.with(this).load(bingpic).into(mpicture);
            Glide.with(this).load(bingpic).into(new ViewTarget<View, GlideDrawable>(mbingmic) {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    this.view.setBackground(resource.getCurrent());
                }
            });
        }
        else {
            loadBingPic();
        }
        oneitem.initMine(R.drawable.ic_liulanjilu,"浏览记录","",true)
                .setOnRootClickListener(this,11);
        twoitem.initMine(R.drawable.ic_shezhi,"设置","",true)
        .setOnArrowClickListener(this,2);
        threeitem.initMine(R.drawable.ic_wodeguanzhu,"我的关注","",true)
        .setOnArrowClickListener(this,3);
        fouritem.initMine(R.drawable.ic_gerenziliao,"个人资料","",true)
        .setOnArrowClickListener(this,1);
        picture=(ImageView)findViewById(R.id.picture);
        imageSize = getResources().getDimensionPixelSize(R.dimen.image_size);
        radius = getResources().getDimensionPixelSize(R.dimen.radius);
        String nichen=spref.getString("nicheng","");
        snichen.setText(nichen);
        String sign=spref.getString("qianming","");
        signtext.setText(sign);
        //new LoadTask1().execute();
        String uri=spref.getString("imagePath",null);
        if (uri!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(uri);
            picture.setImageBitmap(bitmap);
        }
        ActivityCollector.addActivity(this);
    }

    private void loadBingPic(){
        String requestBingpic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingpic, new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingpic=response.body().string();
                seditor= spref.edit();
                seditor.putString("bing_pic",bingpic);
                seditor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MyActivity.this).load(bingpic).into(mpicture);
                        Glide.with(MyActivity.this).load(bingpic).into(new ViewTarget<View, GlideDrawable>(mbingmic) {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                this.view.setBackground(resource.getCurrent());
                                mbingmic.getBackground().setAlpha(50);
                            }
                        });
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        spref=PreferenceManager.getDefaultSharedPreferences(this);
        String nichen=spref.getString("nicheng","");
        snichen.setText(nichen);
        String sign=spref.getString("qianming","");
        signtext.setText(sign);
        /*String imagepath=spref.getString("imagepath","");
        picture.setImageBitmap(imagepath);*/
        //String uri=getIntent().getType();
        String uri=spref.getString("imagepath",null);
        if (uri!=null){
        Bitmap bitmap=BitmapFactory.decodeFile(uri);
        picture.setImageBitmap(bitmap);}
        String uri1=spref.getString("background",null);
        if (uri1!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(uri1);
            mbingmic.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    public void onArrowClick(View view) {
        switch ((int)view.getTag()){
            case 1:
                startActivity(new Intent(MyActivity.this,PersonalActivity.class));
                ActivityCollector.addActivity(this);
                break;
            case 2:
                startActivity(new Intent(MyActivity.this,shezhiActivity.class));
                ActivityCollector.addActivity(this);
                break;
            case 3:
                startActivity(new Intent(MyActivity.this,guanzhu.class));
                ActivityCollector.addActivity(this);
                break;
        }
    }
    public void onRootClick(View view){
        switch ((int)view.getTag()){
            case 11:
                /*startActivity(new Intent(MyActivity.this,ceshi.class));
                ActivityCollector.addActivity(this);*/
                locate locate=new locate();
                Context context=getApplicationContext();
                locate.setApplicationContext(context);
                locate.requesLocation(new locateback() {
                    @Override
                    public void onReceiveLocation(String latitude, String longitude) {

                    }

                    @Override
                    public void onConnentHotSpotMessage() {

                    }
                });
                //locate.setApplicationContext(getApplicationContext());
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(MyActivity.this,shezhiActivity.class));
                ActivityCollector.addActivity(this);
                break;
                default:
                    break;
        }
        return true;
    }
}
