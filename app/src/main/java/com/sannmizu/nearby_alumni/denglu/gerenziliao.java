package com.sannmizu.nearby_alumni.denglu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sannmizu.nearby_alumni.R;

import java.util.ArrayList;

public class gerenziliao extends AppCompatActivity {
    MyOneLineView ziliao1,ziliao2,ziliao3,ziliao4,ziliao5,ziliao6,ziliao7;
    private SharedPreferences spref;
    private SharedPreferences.Editor seditor;
    String me;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerenziliao);
        Toolbar toolbar=(Toolbar)findViewById(R.id.gtoolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ActivityCollector.addActivity(this);
        ziliao1=findViewById(R.id.ziliao1);
        ziliao2=findViewById(R.id.ziliao2);
        ziliao3=findViewById(R.id.ziliao3);
        ziliao4=findViewById(R.id.ziliao4);
        ziliao5=findViewById(R.id.ziliao5);
        ziliao6=findViewById(R.id.ziliao6);
        ziliao7=findViewById(R.id.ziliao7);
        spref=PreferenceManager.getDefaultSharedPreferences(this);
        String name=spref.getString("xingming","");
        ziliao1.initMine(R.drawable.ic_xingming,"姓名",name,false);
        String age=spref.getString("nianling","");
        ziliao2.initMine(R.drawable.ic_nianling,"年龄",age,false);
        String sex=spref.getString("xingbie","");
        ziliao3.initMine(R.drawable.ic_xingbie,"性别",sex,false);
        String nickname=spref.getString("nicheng","");
        ziliao4.initMine(R.drawable.ic_nicheng2,"昵称",nickname,false);
        String collection=spref.getString("xingzuo","");
        ziliao5.initMine(R.drawable.ic__xingzuoyuncheng,"星座",collection,false);
        String career=spref.getString("zhiye","");
        ziliao6.initMine(R.drawable.ic_zhiye,"职业",career,false);
        String area=spref.getString("diqu","");
        ziliao7.initMine(R.drawable.ic_diqu,"地区",area,false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
                break;
        }
        return true;
    }
}
