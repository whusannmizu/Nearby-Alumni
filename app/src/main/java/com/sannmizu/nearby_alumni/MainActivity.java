package com.sannmizu.nearby_alumni;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.sannmizu.nearby_alumni.MiPush.InternetDemo;
import com.xiaomi.mipush.sdk.MiPushClient;


public class MainActivity extends AppCompatActivity {
    private Button button1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = findViewById(R.id.button1);
        button1.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, InternetDemo.class);
            startActivity(intent);
        });
    }

}
