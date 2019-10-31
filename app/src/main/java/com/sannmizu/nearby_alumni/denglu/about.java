package com.sannmizu.nearby_alumni.denglu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.sannmizu.nearby_alumni.R;

public class about extends AppCompatActivity {
    private ImageView imageView;
    private SharedPreferences spref;
    private SharedPreferences.Editor seditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        spref= PreferenceManager.getDefaultSharedPreferences(this);
        imageView=findViewById(R.id.abing_pic_img);
        Toolbar toolbar=findViewById(R.id.atoolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        String bingpic=spref.getString("bing_pic",null);
        if (bingpic!=null) {
            Glide.with(this).load(bingpic).into(imageView);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
