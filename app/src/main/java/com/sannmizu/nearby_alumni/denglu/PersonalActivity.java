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
import com.sannmizu.nearby_alumni.Database.Area;
import com.sannmizu.nearby_alumni.Database.ChinaArea.ProvinceBean;
import com.sannmizu.nearby_alumni.NetUtils.ChatResponse;
import com.sannmizu.nearby_alumni.NetUtils.MyResponse;
import com.sannmizu.nearby_alumni.NetUtils.Net;
import com.sannmizu.nearby_alumni.NetUtils.infoResponse;
import com.sannmizu.nearby_alumni.NetUtils.locateResponse;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.locateActivity;
import com.sannmizu.nearby_alumni.utils.AESUtils;
import com.sannmizu.nearby_alumni.utils.encoder.BASE64Decoder;
import com.sannmizu.nearby_alumni.utils.encoder.BASE64Encoder;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

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
        poneitem.initItemWidthEdit(R.drawable.ic_xingming,"姓名","请输入你的名字");
        String mingzi=spref.getString("mingzi","");
        poneitem.setEditContent(mingzi);
        ptwoitem.initItemWidthEdit1(R.drawable.ic_nianling,"年龄","请设置你的年龄").setOnArrowClickListener(this,3);
        String nianling=spref.getString("nianling","");
        ptwoitem.setEditContent(nianling);
        pthreeitem.initItemWidthEdit1(R.drawable.ic_xingbie,"性别","请判断你的性别").setOnArrowClickListener(this,4);
        String xingbie=spref.getString("xingbie","");
        pthreeitem.setEditContent(xingbie);
        pfouritem.initItemWidthEdit1(R.drawable.ic__xingzuoyuncheng,"星座","请输入你的星座").setOnArrowClickListener(this,2);
        String xingzuo=spref.getString("xingzuo",null);
        pfouritem.setEditContent(xingzuo);
        pfiveitem.initItemWidthEdit1(R.drawable.ic_zhiye,"职业","请选择你的职业").setOnArrowClickListener(this,5);
        String zhiye=spref.getString("zhiye","");
        pfiveitem.setEditContent(zhiye);
        psixitem.initItemWidthEdit1(R.drawable.ic_diqu,"地区","请输入你的地区ID（6位）").setOnArrowClickListener(this,6);
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
        initData();
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
                shangchuan();
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
            case 2:
                showDialog(constelltionlist,3,pfouritem);
                break;
            case 3:
                showDialog(agelist,18,ptwoitem);
                break;
            case 4:
                showDialog(sexlist,3,pthreeitem);
                break;
            case 5:
                showDialog(careerlist,2,pfiveitem);
                break;
            case 6:
                selectprovince(provincelist,10,psixitem,new WheelView.OnWheelViewListener(){
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

    public void shangchuan(){
        spref= PreferenceManager.getDefaultSharedPreferences(this);
        String logToken = spref.getString("logToken", null);
        String connToken = spref.getString("connToken", null);
        String name=spref.getString("mingzi",null);
        int age= Integer.parseInt(spref.getString("nianling",null));
        String sign=spref.getString("qianming",null);
        String sex=spref.getString("xingbie",null);
        String constellation=spref.getString("xingzuo",null);
        String career=spref.getString("zhiye",null);
        int areaId= Integer.parseInt(spref.getString("diqu",null));
        String email=spref.getString("youxiang",null);
        //String icon=spref.getString("imagepath",null);
        String icon1=spref.getString("imagepath",null);
        String icon= null;
        if (icon1!=null) {
            try {
                icon = getImageStr(icon1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JsonObject requestData=new JsonObject();
        requestData.addProperty("name",name);
        requestData.addProperty("sign",sign);
        requestData.addProperty("sex",sex);
        requestData.addProperty("icon",icon);
        requestData.addProperty("areaId",areaId);
        requestData.addProperty("age",age);
        requestData.addProperty("constellation",constellation);
        requestData.addProperty("career",career);
        JsonObject requestRoot=new JsonObject();
        requestRoot.add("info",requestData);
        //requestData.addProperty("email",email);

        if(logToken == "null") {    //其实还要判断logToken是否失效
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
                //locateActivity.instance.getlocate(this);
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
    public static boolean generateImage(String imgStr,String path)throws IOException {
        if (imgStr==null){
            return false;
        }
        BASE64Decoder decoder=new BASE64Decoder();
        byte[] b=decoder.decodeBuffer(imgStr);

        for (int i=0;i<b.length;i++){
            if (b[i]<0){
                b[i]+=256;
            }
        }
        OutputStream out=new FileOutputStream(path);
        out.write(b);
        out.flush();
        out.close();
        return true;
    }
}
