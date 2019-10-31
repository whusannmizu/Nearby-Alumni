package com.sannmizu.nearby_alumni.denglu;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sannmizu.nearby_alumni.R;

public class shezhiActivity extends AppCompatActivity implements View.OnClickListener,MyOneLineView.OnArrowClickListener{
    MyOneLineView soneitem,stwoitem,sthreeitem,sfouritem,sfiveitem;
    private SharedPreferences spref;
    private SharedPreferences.Editor seditor;
    private Button tuichu;
    String background=null;
    public static final int CHOOSE_PHOTO= 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shezhi);
        spref=PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar=(Toolbar)findViewById(R.id.stoolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        soneitem=(MyOneLineView)findViewById(R.id.sone_item);
        stwoitem=(MyOneLineView)findViewById(R.id.stwo_item);
        sthreeitem=(MyOneLineView)findViewById(R.id.sthree_item);
        sfouritem=(MyOneLineView)findViewById(R.id.sfour_item);
        sfiveitem=(MyOneLineView)findViewById(R.id.sfive_item);
        tuichu=(Button)findViewById(R.id.stuichu);
        soneitem.initItemWidthEdit(R.drawable.ic_shezhi,"昵称","请输入你的昵称");
        String snicheng=spref.getString("nicheng","");
        soneitem.setEditContent(snicheng);
        stwoitem.initItemWidthEdit(R.drawable.ic_tuxiang,"背景","请选择你的背景")
        .setOnArrowClickListener(this,1);
        stwoitem.showDivider(true,true);
        sthreeitem.initMine(R.drawable.ic_guanyu,"关于","",true).setOnArrowClickListener(this,2);
        sfouritem.initMine(R.drawable.ic_bangzhuyufankui,"帮助","",true).setOnArrowClickListener(this,3);
        sfiveitem.init(R.drawable.ic_zhanghaoyuanquan,"账号与安全");
        sfiveitem.showDivider(true,true);
        stwoitem.setEditable(false);
        stwoitem.showArrow(true);
        stwoitem.setRightIconSize(30,30);
        tuichu.setOnClickListener(this);
        ActivityCollector.addActivity(this);
}

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.stuichu:
                ActivityCollector.finishAll();
                break;
        }
    }

    @Override
    public void onArrowClick(View view) {
        switch ((int)view.getTag()){
            case 1:
                if(ContextCompat.checkSelfPermission(shezhiActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(shezhiActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else
                    openAlbum();
                break;
            case 2:
                startActivity(new Intent(shezhiActivity.this,about.class));
                break;
            case 3:
                startActivity(new Intent(shezhiActivity.this,Help.class));
                break;
        }
    }
    public void openAlbum(){
        Intent intent=new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }
                else
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                break;
                default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHOOSE_PHOTO:
                handleImageOnKitKat(data);
                break;
                default:
                    break;
        }
    }
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        Uri uri=data.getData();
        if (DocumentsContract.isDocumentUri(this,uri))
        {
            String docId=DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];
                String seletion= MediaStore.Images.Media._ID+"="+id;
                background=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,seletion);
            }
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                background=getImagePath(contentUri,null);
            }
        }
        else if("content".equalsIgnoreCase(uri.getScheme())){
                background=getImagePath(uri,null);
            }
            else if ("file".equalsIgnoreCase(uri.getScheme())){
                background=uri.getPath();
            }
            /*seditor.putString("imagepath",imagePath);
            seditor.apply();*/
            //displayImage(imagePath);
    }
    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                spref=PreferenceManager.getDefaultSharedPreferences(this);
                String snicheng=soneitem.getEditContent();
                seditor=spref.edit();
                seditor.putString("nicheng",snicheng);
                seditor.putString("background",background);
                seditor.apply();
                /*Intent intent=new Intent(this,MainActivity.class);
                intent.setType(imagePath);
                startActivity(intent);*/
                finish();
                ActivityCollector.removeActivity(this);
                break;
                default:
                    break;
        }
        return true;
    }
    /*private void displayImage(String imagepath){
        if (imagepath!=null){
            Bitmap bitmap= BitmapFactory.decodeFile(imagepath);
            spicture.setImageBitmap(bitmap);
        }
        else {
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }*/
}

