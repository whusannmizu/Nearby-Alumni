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
        ziliao1=findViewById(R.id.ziliao1);
        ziliao2=findViewById(R.id.ziliao2);
        ziliao3=findViewById(R.id.ziliao3);
        ziliao4=findViewById(R.id.ziliao4);
        ziliao5=findViewById(R.id.ziliao5);
        ziliao6=findViewById(R.id.ziliao6);
        ziliao7=findViewById(R.id.ziliao7);
        spref=PreferenceManager.getDefaultSharedPreferences(this);
        String name=spref.getString("puserid","");
        ziliao1.initItemWidthEdit2(R.drawable.ic_xingming,"用户id","");
        ziliao1.setEditContent(name);
        String age=spref.getString("pnianling","");
        ziliao2.initItemWidthEdit2(R.drawable.ic_nianling,"年龄","");
        ziliao2.setEditContent(age);
        String sex=spref.getString("pxingbie","");
        ziliao3.initItemWidthEdit2(R.drawable.ic_xingbie,"性别","");
        ziliao3.setEditContent(sex);
        String nickname=spref.getString("pnicheng","");
        ziliao4.initItemWidthEdit2(R.drawable.ic_nicheng2,"昵称","");
        ziliao4.setEditContent(nickname);
        String collection=spref.getString("pxingzuo","");
        ziliao5.initItemWidthEdit2(R.drawable.ic__xingzuoyuncheng,"星座","");
        ziliao5.setEditContent(collection);
        String career=spref.getString("pzhiye","");
        ziliao6.initItemWidthEdit2(R.drawable.ic_zhiye,"职业","");
        ziliao6.setEditContent(career);
        String area=spref.getString("pdiqu","");
        ziliao7.initItemWidthEdit2(R.drawable.ic_diqu,"地区","");
        ziliao7.setEditContent(area);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
