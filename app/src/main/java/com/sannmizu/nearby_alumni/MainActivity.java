package com.sannmizu.nearby_alumni;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;

import com.sannmizu.nearby_alumni.database.LoadChinaArea;
import com.sannmizu.nearby_alumni.denglu.LandingActivity;
import com.sannmizu.nearby_alumni.denglu.MyActivity;


public class MainActivity extends AppCompatActivity {
    private Button button1, button2, button3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        //button3 = findViewById(R.id.button3);
        button1.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, MyActivity.class);
            startActivity(intent);
        });
        button2.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
            startActivity(intent);
        });
       /* button3.setOnClickListener(v->{
            //Intent intent = new Intent(MainActivity.this, LruTestActivity.class);
            //startActivity(intent);
        });*/

    }

    public void init() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean("isFirst", true)) {
            new Thread(()->{
                LoadChinaArea.load(this);
            }).start();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirst", false);
        editor.apply();
    }
}
