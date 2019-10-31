package com.sannmizu.nearby_alumni.denglu;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

import com.google.gson.JsonObject;
import com.sannmizu.nearby_alumni.database.Area;
import com.sannmizu.nearby_alumni.NetUtils.MyResponse;
import com.sannmizu.nearby_alumni.NetUtils.Net;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.utils.AESUtils;
import com.sannmizu.nearby_alumni.utils.AccountUtils;
import com.sannmizu.nearby_alumni.utils.encoder.BASE64Encoder;

import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PersonalActivity extends AppCompatActivity implements MyOneLineView.OnArrowClickListener{
    MyOneLineView poneitem,ptwoitem,pthreeitem,pfouritem,pfiveitem,psixitem,psevenitem,peightitem,pnightitem;
    private SharedPreferences spref;
    private SharedPreferences.Editor seditor;
    String imagePath=null;
    public static final int CHOOSE_PHOTO= 2;
    private EditText tvconstellation;
    private ArrayList<String>constelltionlist=new ArrayList<>();
    private ArrayList<String>agelist=new ArrayList<>();
    private ArrayList<String>sexlist=new ArrayList<>();
    private ArrayList<String>careerlist=new ArrayList<>();
    private ArrayList<String>provincelist=new ArrayList<>();
    private ArrayList<String>citylist=new ArrayList<>();
    private ArrayList<String>Arealist=new ArrayList<>();
    private String selectText="";
    private String mingzi,nianling,xingbie,xingzuo,zhiye,diqu,youxiang,qianming;
    int areaId,age;
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
        pfiveitem=(MyOneLineView)findViewById(R.id.pfive_item);
        psixitem=(MyOneLineView)findViewById(R.id.psix_item);
        psevenitem=(MyOneLineView)findViewById(R.id.pseven_item);
        peightitem=(MyOneLineView)findViewById(R.id.peight_item);
        pnightitem=findViewById(R.id.pnight_item);
        spref= PreferenceManager.getDefaultSharedPreferences(this);
        int userid=AccountUtils.getCurrentUserId();
        dataget.getdata(userid, new Databack() {
            @Override
            public void ongetdata(String id, String name, String age, String sign, String sex, String constellaiton, String career, String areaId, String icon) {
                PersonalActivity.this.mingzi=name;
                PersonalActivity.this.nianling=age;
                PersonalActivity.this.xingbie=sex;
                PersonalActivity.this.xingzuo=constellaiton;
                PersonalActivity.this.zhiye=career;
                PersonalActivity.this.diqu=areaId;
                PersonalActivity.this.qianming=sign;
                poneitem.initItemWidthEdit(R.drawable.ic_xingming,"姓名","请输入你的名字");
                poneitem.setEditContent(mingzi);
                ptwoitem.initItemWidthEdit1(R.drawable.ic_nianling,"年龄","请设置你的年龄").setOnArrowClickListener(PersonalActivity.this,3);
                ptwoitem.setEditContent(nianling);
                pthreeitem.initItemWidthEdit1(R.drawable.ic_xingbie,"性别","请判断你的性别").setOnArrowClickListener(PersonalActivity.this,4);
                pthreeitem.setEditContent(xingbie);
                pfouritem.initItemWidthEdit1(R.drawable.ic__xingzuoyuncheng,"星座","请输入你的星座").setOnArrowClickListener(PersonalActivity.this,2);
                pfouritem.setEditContent(xingzuo);
                pfiveitem.initItemWidthEdit1(R.drawable.ic_zhiye,"职业","请选择你的职业").setOnArrowClickListener(PersonalActivity.this,5);
                pfiveitem.setEditContent(zhiye);
                psixitem.initItemWidthEdit1(R.drawable.ic_diqu,"地区","请输入你的地区ID（6位）").setOnArrowClickListener(PersonalActivity.this,6);
                psixitem.setEditContent(diqu);
                psevenitem.initItemWidthEdit(R.drawable.ic_youxiang,"邮箱","请输入你的邮箱");
                psevenitem.setEditContent(youxiang);
                peightitem.initItemWidthEdit(R.drawable.ic_qianming,"签名","请输入个性签名");
                peightitem.setEditContent(qianming);
            }
        });
        youxiang=spref.getString("youxiang","");
        pnightitem.initItemWidthEdit(R.drawable.ic_tuxiang,"头像","请选择你的头像")
        .setOnArrowClickListener(this,1);
        pnightitem.showArrow(true);
        pnightitem.setEditable(false);
        pnightitem.setRightIconSize(30,30);
        //findViewById(R.id.ib_navigation_back).setOnClickListener(this);
        initData();
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

        agelist.clear();
        for(int i=1;i<=80;i++){
            agelist.add(String.format("%d",i));
        }

        sexlist.clear();
        sexlist.add("男");
        sexlist.add("女");
        sexlist.add("不明");

        careerlist.clear();
        careerlist.add("工人");
        careerlist.add("学生");
        careerlist.add("教师");
        careerlist.add("经理");

        List<Area>areas=LitePal.where("pid=0").find(Area.class);
        if (areas.size()>0){
            provincelist.clear();
            for (Area area:areas){
                provincelist.add(area.getName());
            }
        }

    }
    private void showDialog(ArrayList<String> list, int selected,MyOneLineView myOneLineView){
        showChoiceDialog(list, selected,myOneLineView,
                new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                selectText = item;
            }
        });
    }
    private void showChoiceDialog(ArrayList<String> dataList,int selected, MyOneLineView myOneLineView,WheelView.OnWheelViewListener listener){
        selectText = "";
        LayoutInflater inflater = getLayoutInflater();
        View outerView = inflater.inflate(R.layout.dialog_wheelview,null);
        final WheelView wheelView = outerView.findViewById(R.id.wheel_view);
        wheelView.setOffset(2);// 对话框中当前项上面和下面的项数
        wheelView.setItems(dataList);// 设置数据源
        wheelView.setSeletion(selected);// 默认选中第三项
        wheelView.setOnWheelViewListener(listener);         // 显示对话框，点击确认后将所选项的值显示到Button上
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(outerView)
                .setPositiveButton("确认",
                        (dialogInterface, i) -> {
                    myOneLineView.setEditContent(selectText);
                    myOneLineView.setEditSize(18);
                    myOneLineView.setEditColor(R.color.blue);
                    //myOneLineView.setRightTextColor(this.getResources().getColor(R.color.green));
                })
                .setNegativeButton("取消",null).create();
        alertDialog.show();
        int green = this.getResources().getColor(R.color.green);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(green);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(green);
    }

    private void selectprovince(ArrayList<String> dataList1,int selected,MyOneLineView myOneLineView,WheelView.OnWheelViewListener listener){
        selectText = "";
        LayoutInflater inflater = getLayoutInflater();
        View outerView = inflater.inflate(R.layout.dialog_wheelview,null);
        final WheelView wheelView = outerView.findViewById(R.id.wheel_view);
        wheelView.setOffset(2);// 对话框中当前项上面和下面的项数
        wheelView.setItems(dataList1);// 设置数据源
        wheelView.setSeletion(selected);// 默认选中第三项
        wheelView.setOnWheelViewListener(listener);         // 显示对话框，点击确认后将所选项的值显示到Button上
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(outerView)
                .setPositiveButton("确认",
                        (dialogInterface, i) -> {
                            int n=0;
                            List<Area> areas = LitePal.where("name=?",selectText).find(Area.class);
                            if (areas.size() > 0) {
                                n=areas.get(0).getArea_id();
                            }
                            if (n!=0)
                            {
                                List<Area> areas1= LitePal.where("pid=?",String.valueOf(n)).find(Area.class);
                                if (areas1.size()>0){
                                    citylist.clear();
                                    for (Area area:areas1){
                                        citylist.add(area.getName());
                                    }
                                }
                            }
                            selectcity(citylist,3,myOneLineView,new WheelView.OnWheelViewListener(){
                                @Override
                                public void onSelected(int selectedIndex, String item) {
                                    selectText = item;
                                }
                            });
                            //myOneLineView.setRightTextColor(this.getResources().getColor(R.color.green));
                        })
                .setNegativeButton("取消",null).create();
        alertDialog.show();
        int green = this.getResources().getColor(R.color.green);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(green);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(green);
    }
    private void selectcity(ArrayList<String> dataList1,int selected,MyOneLineView myOneLineView,WheelView.OnWheelViewListener listener){
        selectText = "";
        LayoutInflater inflater = getLayoutInflater();
        View outerView = inflater.inflate(R.layout.dialog_wheelview,null);
        final WheelView wheelView = outerView.findViewById(R.id.wheel_view);
        wheelView.setOffset(2);// 对话框中当前项上面和下面的项数
        wheelView.setItems(dataList1);// 设置数据源
        wheelView.setSeletion(selected);// 默认选中第三项
        wheelView.setOnWheelViewListener(listener);         // 显示对话框，点击确认后将所选项的值显示到Button上
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(outerView)
                .setPositiveButton("确认",
                        (dialogInterface, i) -> {
                            int n=0;
                            List<Area> areas = LitePal.where("name=?",selectText).find(Area.class);
                            if (areas.size() > 0) {
                                n=areas.get(0).getArea_id();
                            }
                            if (n!=0) {
                                List<Area> areas1 = LitePal.where("pid=?", String.valueOf(n)).find(Area.class);
                                if (areas1.size() > 0) {
                                    Arealist.clear();
                                    for (Area area : areas1) {
                                        Arealist.add(area.getName());
                                    }
                                }
                                selectarea(Arealist, 1, myOneLineView, new WheelView.OnWheelViewListener() {
                                    @Override
                                    public void onSelected(int selectedIndex, String item) {
                                        selectText = item;
                                    }
                                });
                            }
                            //myOneLineView.setRightTextColor(this.getResources().getColor(R.color.green));
                        })
                .setNegativeButton("取消",null).create();
        alertDialog.show();
        int green = this.getResources().getColor(R.color.green);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(green);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(green);
    }
    private void selectarea(ArrayList<String> dataList1,int selected,MyOneLineView myOneLineView,WheelView.OnWheelViewListener listener){
        selectText = "";
        LayoutInflater inflater = getLayoutInflater();
        View outerView = inflater.inflate(R.layout.dialog_wheelview,null);
        final WheelView wheelView = outerView.findViewById(R.id.wheel_view);
        wheelView.setOffset(2);// 对话框中当前项上面和下面的项数
        wheelView.setItems(dataList1);// 设置数据源
        wheelView.setSeletion(selected);// 默认选中第三项
        wheelView.setOnWheelViewListener(listener);         // 显示对话框，点击确认后将所选项的值显示到Button上
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(outerView)
                .setPositiveButton("确认",
                        (dialogInterface, i) -> {
                            int n=0;
                            List<Area> areas = LitePal.where("name=?",selectText).find(Area.class);
                            if (areas.size() > 0) {
                                n=areas.get(0).getArea_id();
                            }
                            myOneLineView.setEditContent(String.valueOf(n));
                            myOneLineView.setEditSize(18);
                            myOneLineView.setEditColor(R.color.blue);
                            //myOneLineView.setRightTextColor(this.getResources().getColor(R.color.green));
                        })
                .setNegativeButton("取消",null).create();
        alertDialog.show();
        int green = this.getResources().getColor(R.color.green);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(green);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(green);
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
                break;
            case R.id.save:
                spref=PreferenceManager.getDefaultSharedPreferences(this);
                shangchuan();
                default:
                    break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            case 2:
                showDialog(constelltionlist,0,pfouritem);
                break;
            case 3:
                showDialog(agelist,17,ptwoitem);
                break;
            case 4:
                showDialog(sexlist,0,pthreeitem);
                break;
            case 5:
                showDialog(careerlist,0,pfiveitem);
                break;
            case 6:
                selectprovince(provincelist,0,psixitem,new WheelView.OnWheelViewListener(){
                    @Override
                    public void onSelected(int selectedIndex, String item) {
                        selectText = item;
                    }
                });
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
                if(resultCode == RESULT_OK) {
                    handleImageOnKitKat(data);
                }
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

    public void shangchuan(){
        String logToken=AccountUtils.getLogToken();
        String connToken=AccountUtils.getConnToken();
        mingzi=poneitem.getEditContent();
        nianling=ptwoitem.getEditContent().toString().trim();
        if (nianling.length()==0)
        {
            age= 0;
        }
        else {
            age=Integer.parseInt(nianling);
        }
        xingbie=pthreeitem.getEditContent();
        xingzuo=pfouritem.getEditContent();
        zhiye=pfiveitem.getEditContent();
        diqu=psixitem.getEditContent().toString().trim();
        if (diqu.length()==0)
        {
            areaId=0;
        }
        else {
            areaId=Integer.parseInt(diqu);
        }
        youxiang=psevenitem.getEditContent();
        qianming=peightitem.getEditContent();
        String icon1=spref.getString("imagepath",null);
        String icon= null;
        if (icon1!=null) {
            icon = bitmapToString(icon1);
        }
        JsonObject requestData=new JsonObject();
        requestData.addProperty("name",mingzi);
        requestData.addProperty("sign",xingbie);
        requestData.addProperty("sex",xingbie);
        requestData.addProperty("icon",icon);
        requestData.addProperty("areaId",areaId);
        requestData.addProperty("age",age);
        requestData.addProperty("constellation",xingzuo);
        requestData.addProperty("career",zhiye);
        JsonObject requestRoot=new JsonObject();
        requestRoot.add("info",requestData);
        //requestData.addProperty("email",email);

        if(logToken.equals("null")) {    //其实还要判断logToken是否失效
            runOnUiThread(()->{
                Toast.makeText(PersonalActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
            });
        } else {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Net.BaseHost)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            MyResponse.infoService service=retrofit.create(MyResponse.infoService.class);
            String encrypted = AESUtils.encryptFromLocal(requestRoot.toString());
            if(encrypted == null || connToken == null) {  //其实还要判断connToken是否失效
                runOnUiThread(()->{
                    Toast.makeText(PersonalActivity.this, "请先建立私密链接", Toast.LENGTH_SHORT).show();
                });
            } else {
                runOnUiThread(()-> {
                    Call<MyResponse> call = service.info(encrypted, logToken, connToken);
                    call.enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.body().getCode() == 0)
                                Log.d("lianjie", "连接成功");
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                });
            }
        }
    }

    public static String getImageStr(String imgFile)throws IOException {
        InputStream inputStream=null;
        byte[]data=null;
        try {
            inputStream=new FileInputStream(imgFile);
            data=new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        BASE64Encoder encoder=new BASE64Encoder();
        return encoder.encode(data);
    }
    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 200, 200);
        //options.inSampleSize=15;
        //图片压缩的倍率
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    //把bitmap转换成String
    public static String bitmapToString(String filePath) {

        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40,baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

}
