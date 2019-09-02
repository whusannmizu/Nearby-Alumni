package com.sannmizu.nearby_alumni.denglu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.sannmizu.nearby_alumni.R;

public class ForgetPwdActivity extends AppCompatActivity implements View.OnClickListener{
private Button retrieve_submit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_retrieve_pwd);
        findViewById(R.id.ib_navigation_back).setOnClickListener(this);
        initView();
    }

    public void initView(){
        retrieve_submit=findViewById(R.id.bt_retrieve_submit);
        retrieve_submit.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_navigation_back:
                finish();
                break;
            case R.id.bt_retrieve_submit:
                startActivity(new Intent(ForgetPwdActivity.this,ChangePwdActivity.class));
        }
    }
}
