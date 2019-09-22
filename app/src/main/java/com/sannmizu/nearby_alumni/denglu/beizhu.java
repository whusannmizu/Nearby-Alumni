package com.sannmizu.nearby_alumni.denglu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sannmizu.nearby_alumni.R;

public class beizhu extends AppCompatActivity {
    private SharedPreferences spref;
    private SharedPreferences.Editor seditor;
    private EditText bet1,bet2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beizhu);
        spref= PreferenceManager.getDefaultSharedPreferences(this);
        bet1=findViewById(R.id.bedit1);
        bet2=findViewById(R.id.bedit2);
        Toolbar toolbar=findViewById(R.id.btoolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.baocun,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       switch (item.getItemId()){
           case android.R.id.home:
               finish();
               ActivityCollector.removeActivity(this);
               break;
           case R.id.save:
               String note=bet1.getText().toString();
               seditor=spref.edit();
                seditor.putString("note",note);
                String describe=bet2.getText().toString();
                seditor.putString("describe",describe);
                seditor.apply();
                finish();
               ActivityCollector.removeActivity(this);
               break;
       }
       return true;
    }
}
