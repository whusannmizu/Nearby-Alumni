package com.sannmizu.nearby_alumni.denglu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sannmizu.nearby_alumni.NetUtils.ForgetResponse;
import com.sannmizu.nearby_alumni.NetUtils.Net;
import com.sannmizu.nearby_alumni.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgetPwdActivity extends AppCompatActivity implements View.OnClickListener{
private Button reset_submit;
private EditText telEdit,pwdEdit,pwdEdit1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_retrieve_pwd);
        findViewById(R.id.ib_navigation_back).setOnClickListener(this);
        initView();
    }

    public void initView(){
        reset_submit=findViewById(R.id.bt_retrieve_submit);
        reset_submit.setOnClickListener(this);
        telEdit=findViewById(R.id.tel_reset_tel);
        pwdEdit=findViewById(R.id.pwd_reset_pwd);
        pwdEdit1=findViewById(R.id.reset_pwd1);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_navigation_back:
                finish();
                break;
            case R.id.bt_retrieve_submit:
                forgetpwd();
                finish();
                break;
        }
    }
    private void forgetpwd() {
        String tel = telEdit.getText().toString();
        String type = "tel";
        String pwd = pwdEdit.getText().toString();
        String pwd1 = pwdEdit1.getText().toString();
        if (pwd.equals(pwd1)) {
            String requestStr = ForgetResponse.RequestStr(type, tel, pwd);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Net.BaseHost)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ForgetResponse.ForgetService service = retrofit.create(ForgetResponse.ForgetService.class);
            Call<ForgetResponse> call = service.forget(requestStr);
            call.enqueue(new Callback<ForgetResponse>() {
                @Override
                public void onResponse(Call<ForgetResponse> call, Response<ForgetResponse> response) {
                    if (response.code() == 0)
                        Log.d("密码重置", "成功 ");
                }

                @Override
                public void onFailure(Call<ForgetResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
        else {
            Toast.makeText(ForgetPwdActivity.this, "密码输入错误", Toast.LENGTH_SHORT).show();
        }
    }
}
