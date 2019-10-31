package com.sannmizu.nearby_alumni.bottomNavigation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.denglu.Databack;
import com.sannmizu.nearby_alumni.denglu.HttpUtil;
import com.sannmizu.nearby_alumni.denglu.LandingActivity;
import com.sannmizu.nearby_alumni.denglu.MyOneLineView;
import com.sannmizu.nearby_alumni.denglu.PersonalActivity;
import com.sannmizu.nearby_alumni.denglu.dataget;
import com.sannmizu.nearby_alumni.denglu.guanzhu;
import com.sannmizu.nearby_alumni.denglu.shezhiActivity;
import com.sannmizu.nearby_alumni.utils.AccountUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class MineFragment extends Fragment implements MyOneLineView.OnArrowClickListener,MyOneLineView.OnRootClickListener{
 MyOneLineView oneitem,twoitem,threeitem,fouritem,soneitem;
 private SharedPreferences spref;
 private SharedPreferences.Editor seditor;
 TextView snichen,mwode,signtext;
 private ImageView picture,mpicture;
 private LinearLayout mbeijing;
 private String nichen,sign;
 ImageView mbingmic;
 int imageSize, radius;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myactivity, container, false);
        //检查是否登陆
        if(AccountUtils.getLocked()) {
            AccountUtils.requestLogin(getContext());
        }
        spref= PreferenceManager.getDefaultSharedPreferences(getContext());
        mbeijing=view.findViewById(R.id.mbeijing);
        oneitem=view.findViewById(R.id.one_item);
        twoitem=view.findViewById(R.id.two_item);
        threeitem=view.findViewById(R.id.three_item);
        fouritem=view.findViewById(R.id.four_item);
        soneitem=view.findViewById(R.id.sone_item);
        snichen=view.findViewById(R.id.nichen);
        signtext=view.findViewById(R.id.signtext);
        mwode=view.findViewById(R.id.mwode);
        mpicture=view.findViewById(R.id.mpicture);
        mbingmic=view.findViewById(R.id.mbing_pic_img);
       //mbingPicimg=(ImageView)findViewById(R.id.mbing_pic_img);
        //mbejing=(ScrollView)findViewById(R.id.mbeijing);
        Toolbar toolbar=view.findViewById(R.id.mtoolbar);

        String bingpic=spref.getString("bing_pic",null);
        if (bingpic!=null){
            Glide.with(getContext()).load(bingpic).into(mpicture);
            Glide.with(getContext()).load(bingpic).into(new ViewTarget<View, GlideDrawable>(mbingmic) {
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
        picture=view.findViewById(R.id.picture);
        imageSize = getResources().getDimensionPixelSize(R.dimen.image_size);
        radius = getResources().getDimensionPixelSize(R.dimen.radius);
        nichen=spref.getString("nicheng","");
        sign=spref.getString("qianming","");
        int useid=AccountUtils.getCurrentUserId();
        if (nichen.length()==0||sign.length()==0) {
            dataget.getdata(useid, new Databack() {
                @Override
                public void ongetdata(String id, String name, String age, String sign, String sex, String constellaiton, String career, String areaId, String icon) {
                    MineFragment.this.nichen = name;
                    MineFragment.this.sign = sign;
                    snichen.setText(nichen);
                    signtext.setText(sign);
                }
            });
        }
        snichen.setText(nichen);
        signtext.setText(sign);
        //new LoadTask1().execute();
        String uri=spref.getString("imagePath",null);
        if (uri!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(uri);
            picture.setImageBitmap(bitmap);
        }
        return view;
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(getContext()).load(bingpic).into(mpicture);
                        Glide.with(getContext()).load(bingpic).into(new ViewTarget<View, GlideDrawable>(mbingmic) {
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
    public void onStart() {
        super.onStart();
        spref=PreferenceManager.getDefaultSharedPreferences(getContext());
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
    public void onArrowClick(View view) {
        if(AccountUtils.getLocked()) {
            AccountUtils.requestLogin(getContext());
            return;
        }
        switch ((int)view.getTag()){
            case 1:
                startActivity(new Intent(getContext(), PersonalActivity.class));
                break;
            case 2:
                startActivity(new Intent(getContext(), shezhiActivity.class));
                break;
            case 3:
                startActivity(new Intent(getContext(), guanzhu.class));
                break;
        }
    }
    public void onRootClick(View view){
        if(AccountUtils.getLocked()) {
            AccountUtils.requestLogin(getContext());
            return;
        }
        switch ((int)view.getTag()){
            case 11:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(AccountUtils.getLocked()) {
            AccountUtils.requestLogin(getContext());
            return true;
        }
        switch (item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(getContext(),shezhiActivity.class));
                break;
                default:
                    break;
        }
        return true;
    }

}
