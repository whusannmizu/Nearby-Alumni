package com.sannmizu.nearby_alumni.denglu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.sannmizu.nearby_alumni.NetUtils.MyCallback;
import com.sannmizu.nearby_alumni.NetUtils.MyResponse;
import com.sannmizu.nearby_alumni.NetUtils.Net;
import com.sannmizu.nearby_alumni.NetUtils.addresponse;
import com.sannmizu.nearby_alumni.NetUtils.friendResponse;
import com.sannmizu.nearby_alumni.NetUtils.infoResponse;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.utils.AccountUtils;
import com.sannmizu.nearby_alumni.utils.encoder.BASE64Decoder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.sannmizu.nearby_alumni.NetUtils.FriendsResponse.addFriend;
import static com.sannmizu.nearby_alumni.NetUtils.FriendsResponse.deleteFriend;

public class guanzhu extends AppCompatActivity implements View.OnClickListener,MyOneLineView.OnRootClickListener{
    private MyOneLineView gone,gtwo;
    private SharedPreferences spref;
    private SharedPreferences.Editor seditor;
    private ImageView opicture,obackground;
    private ScrollView scrollView;
    private TextView gtext1,gtext2,gt1,gt2;
    private Button gbutton1,gbutton2;
    String name,age,sign,sex,constellation,career,areaId,icon1,icon,id;
    public static void actionStart(Context context, int id) {
        Intent intent= new Intent(context, guanzhu.class);
        intent.putExtra("userid", id);
        context.startActivity(intent);
    }

    int userid = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        userid = intent.getIntExtra("userid", 0);
        if(userid == 0) {
            Toast.makeText(this, "无效操作", Toast.LENGTH_SHORT).show();
            finish();
        }
        setContentView(R.layout.guanzhu);
        scrollView=(ScrollView)findViewById(R.id.gbeijing);
        opicture=findViewById(R.id.opicture);
        obackground=findViewById(R.id.obackground);
        gbutton1=findViewById(R.id.gbutton1);
        gbutton1.setOnClickListener(this);
        gbutton2=findViewById(R.id.gbutton2);
        gbutton2.setOnClickListener(this);
        gbutton1.setText("发消息");

        //getdata();
        dataget.getdata(userid, new Databack() {
            @Override
            public void ongetdata(String id, String name, String age, String sign, String sex, String constellaiton, String career, String areaId, String icon) {
                guanzhu.this.id=id;
                guanzhu.this.name=name;
                guanzhu.this.age=age;
                guanzhu.this.sign=sign;
                guanzhu.this.sex=sex;
                guanzhu.this.constellation=constellation;
                guanzhu.this.career=career;
                guanzhu.this.areaId=areaId;
                guanzhu.this.icon=icon;
            }
        });
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

        if (sign!=null)
            gt2.setText(sign);
        String text1=spref.getString("note",null);
        if (text1!=null)
        {
            gtext1.setText(text1);
        }
        if (areaId!=null)
        {
            gtext2.setText(areaId);
        }
        Toolbar toolbar=(Toolbar)findViewById(R.id.gtoolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onRootClick(View view) {
        switch ((int)view.getTag()){
            case 1:
                startActivity(new Intent(guanzhu.this,beizhu.class));
                break;
            case 2:
                seditor=spref.edit();
                seditor.putString("puserid",id);
                seditor.putString("pnianling",age);
                seditor.putString("pxingbie",sex);
                seditor.putString("pnicheng",name);
                seditor.putString("pxingzuo",constellation);
                seditor.putString("pzhiye",career);
                seditor.putString("pdiqu",areaId);
                seditor.apply();
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
                    addFriend(userid, new MyCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(String reason) {

                        }

                        @Override
                        public void onError(Throwable t) {

                        }
                    });
                }
                else
                    deleteFriend(userid, new MyCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(String reason) {

                        }

                        @Override
                        public void onError(Throwable t) {

                        }
                    });
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void juge(){
        spref=PreferenceManager.getDefaultSharedPreferences(this);
        String logToken = AccountUtils.getLogToken();
        //String userid=spref.getString("userId",null);
        int userid = AccountUtils.getCurrentUserId();
        if (logToken.equals("")) {    //其实还要判断logToken是否失效
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
                        for(int i=0;i<response.body().getData().getFriends().size();i++) {
                            int m = response.body().getData().getFriends().get(i).getId();
                            if (userid != m)
                                gbutton2.setText("添加好友");
                        }
                        gbutton2.setText("添加好友");
                    }
                    else
                        gbutton2.setText("连接失败");
                }

                @Override
                public void onFailure(retrofit2.Call<friendResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
    public static Bitmap stringToBitmap(String string,ImageView imageView){
        Bitmap bitmap=null;
        try
        {
            byte[] bitmapArray= Base64.decode(string.split(",")[1],Base64.DEFAULT);
            bitmap=BitmapFactory.decodeByteArray(bitmapArray,0,bitmapArray.length);
        }catch (Exception e){
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);
        return bitmap;
    }
}
