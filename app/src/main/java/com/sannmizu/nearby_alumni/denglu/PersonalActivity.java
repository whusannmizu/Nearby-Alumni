package com.sannmizu.nearby_alumni.denglu;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sannmizu.nearby_alumni.R;

import java.util.ArrayList;

public class PersonalActivity extends AppCompatActivity implements MyOneLineView.OnArrowClickListener{
    MyOneLineView poneitem,ptwoitem,pthreeitem,pfouritem,pfiveitem,psixitem,psevenitem,peightitem,pnightitem;
    private SharedPreferences spref;
    private SharedPreferences.Editor seditor;
    String imagePath=null;
    public static final int CHOOSE_PHOTO= 2;
    private EditText tvconstellation;
    private ArrayList<String>constelltionlist=new ArrayList<>();
    private String selectText="";
    String me;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal);
        Toolbar toolbar=(Toolbar)findViewById(R.id.ptoolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        poneitem=(MyOneLineView)findViewById(R.id.pone_item);
        ptwoitem=(MyOneLineView)findViewById(R.id.ptwo_item);
        pthreeitem=(MyOneLineView)findViewById(R.id.pthree_item);
        pfouritem=(MyOneLineView)findViewById(R.id.pfour_item);
        findViewById(R.id.pfour_item).setOnClickListener(view -> showDialog(constelltionlist,12));
        pfiveitem=(MyOneLineView)findViewById(R.id.pfive_item);
        psixitem=(MyOneLineView)findViewById(R.id.psix_item);
        psevenitem=(MyOneLineView)findViewById(R.id.pseven_item);
        peightitem=(MyOneLineView)findViewById(R.id.peight_item);
        pnightitem=findViewById(R.id.pnight_item);
        spref= PreferenceManager.getDefaultSharedPreferences(this);
        poneitem.initItemWidthEdit(R.drawable.ic_xingming,"姓名","请输入你的名字");
        String mingzi=spref.getString("mingzi","");
        poneitem.setEditContent(mingzi);
        ptwoitem.initItemWidthEdit(R.drawable.ic_nianling,"年龄","请设置你的年龄");
        String nianling=spref.getString("nianling","");
        ptwoitem.setEditContent(nianling);
        pthreeitem.initItemWidthEdit(R.drawable.ic_xingbie,"性别","请判断你的性别");
        String xingbie=spref.getString("xingbie","");
        pthreeitem.setEditContent(xingbie);
        initData();
        pfouritem.initItemWidthEdit(R.drawable.ic__xingzuoyuncheng,"星座","");
        String xingzuo=spref.getString("xingzuo",null);
        if (xingzuo==null)
        {pfouritem.setEditHint("请选择你的星座");}
        else
        {pfouritem.setRightText(xingzuo);}
        pfiveitem.initItemWidthEdit(R.drawable.ic_zhiye,"职业","请选择你的职业");
        String zhiye=spref.getString("zhiye","");
        pfiveitem.setEditContent(zhiye);
        psixitem.initItemWidthEdit(R.drawable.ic_diqu,"地区","请输入你所在的地区");
        String diqu=spref.getString("diqu","");
        psixitem.setEditContent(diqu);
        psevenitem.initItemWidthEdit(R.drawable.ic_youxiang,"邮箱","请输入你的邮箱");
        String youxiang=spref.getString("youxiang","");
        psevenitem.setEditContent(youxiang);
        peightitem.initItemWidthEdit(R.drawable.ic_qianming,"签名","请输入个性签名");
        String qianming=spref.getString("qianming","");
        peightitem.setEditContent(qianming);
        pnightitem.initItemWidthEdit(R.drawable.ic_tuxiang,"头像","请选择你的头像")
        .setOnArrowClickListener(this,1);
        pnightitem.showArrow(true);
        pnightitem.setEditable(false);
        pnightitem.setRightIconSize(30,30);
        //findViewById(R.id.ib_navigation_back).setOnClickListener(this);
        ActivityCollector.addActivity(this);
    }
    private void initData(){
        constelltionlist.clear();
        constelltionlist.add("白羊座");
        constelltionlist.add("金牛座");
        constelltionlist.add("双子座");
        constelltionlist.add("巨蟹座");
        constelltionlist.add("狮子座");
        constelltionlist.add("处女座");
        constelltionlist.add("天枰座");
        constelltionlist.add("天蝎座");
        constelltionlist.add("射手座");
        constelltionlist.add("魔蝎座");
        constelltionlist.add("水瓶座");
        constelltionlist.add("双鱼座");
    }
    private void showDialog(ArrayList<String> list, int selected){
        showChoiceDialog(list, selected,
                new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                selectText = item;
            }
        });
    }
    private void showChoiceDialog(ArrayList<String> dataList,int selected, WheelView.OnWheelViewListener listener){
        selectText = "";
        View outerView = LayoutInflater.from(this).inflate(R.layout.dialog_wheelview,null);
        final WheelView wheelView = outerView.findViewById(R.id.wheel_view);
        wheelView.setOffset(2);// 对话框中当前项上面和下面的项数
        wheelView.setItems(dataList);// 设置数据源
        wheelView.setSeletion(selected);// 默认选中第三项
        wheelView.setOnWheelViewListener(listener);         // 显示对话框，点击确认后将所选项的值显示到Button上
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(outerView)
                .setPositiveButton("确认",
                        (dialogInterface, i) -> {
                    pfouritem.setRightText(selectText);
                    pfouritem.setRightTextColor(this.getResources().getColor(R.color.green));
                })
                .setNegativeButton("取消",null).create();
        alertDialog.show();
        int green = this.getResources().getColor(R.color.green);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(green);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(green);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                spref=PreferenceManager.getDefaultSharedPreferences(this);
                String mingzi=poneitem.getEditContent();
                String nianling=ptwoitem.getEditContent();
                String xingbie=pthreeitem.getEditContent();
                String xingzuo=pfouritem.getEditContent();
                String zhiye=pfiveitem.getEditContent();
                String diqu=psixitem.getEditContent();
                String youxiang=psevenitem.getEditContent();
                String qianming=peightitem.getEditContent();
                seditor=spref.edit();
                seditor.putString("mingzi",mingzi);
                seditor.putString("nianling",nianling);
                seditor.putString("xingbie",xingbie);
                seditor.putString("xingzuo",xingzuo);
                seditor.putString("zhiye",zhiye);
                seditor.putString("diqu",diqu);
                seditor.putString("youxiang",youxiang);
                seditor.putString("qianming",qianming);
                seditor.putString("imagepath",imagePath);
                seditor.apply();
                finish();
                break;
                default:
                    break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }


    @Override
    public void onArrowClick(View view) {
        switch ((int)view.getTag()){
            case 1:
                if(ContextCompat.checkSelfPermission(PersonalActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PersonalActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else
                    openAlbum();
                break;

                default:
                    break;
        }
    }

    public void openAlbum(){
        Intent intent=new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
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
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,seletion);
            }
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }
        else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
            Log.d("imagepath",imagePath);
            seditor=spref.edit();
            seditor.putString("imagepath",imagePath);
            seditor.apply();
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
}
