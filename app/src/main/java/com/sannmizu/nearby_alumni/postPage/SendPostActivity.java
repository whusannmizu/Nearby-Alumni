package com.sannmizu.nearby_alumni.postPage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.sannmizu.nearby_alumni.NetUtils.Bean.PushBean;
import com.sannmizu.nearby_alumni.NetUtils.MyCallback;
import com.sannmizu.nearby_alumni.NetUtils.PostPushResponse;
import com.sannmizu.nearby_alumni.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SendPostActivity extends AppCompatActivity {
    private TextView btn_cancel;
    private AppCompatButton btn_send;
    private TextInputEditText send_text;
    private ImageButton choose_pic;
    private CheckBox share_box;
    private GridLayout pics_layout;
    private ProgressBar progressBar;

    public static final int CHOOSE_PHOTO = 2;

    private List<String> picsPath = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_post);

        initView();
        setAttributes();
        setListener();
    }

    private void initView() {
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_send = findViewById(R.id.btn_send);
        send_text = findViewById(R.id.edit_text);
        choose_pic = findViewById(R.id.choose_picture);
        share_box = findViewById(R.id.check_nearby);
        pics_layout = findViewById(R.id.pics);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setAttributes() {

    }

    private void setListener() {
        btn_cancel.setOnClickListener(v->{
            finish();
        });
        btn_send.setOnClickListener(v->{
            if(send_text.getText().length() != 0 || picsPath.size() != 0) {
                if(progressBar.getVisibility() != View.VISIBLE) {
                    send();
                }
            }
        });
        choose_pic.setOnClickListener(v->{
            choosePicture();
        });
    }

    private void send() {
        progressBar.setVisibility(View.VISIBLE);
        PushBean pushBean = new PushBean();

        PushBean.PostBean postBean = new PushBean.PostBean();
        postBean.setContent(send_text.getText().toString() + "");
        if(share_box.isChecked()) {
            String latitude, longitude;

        }
        postBean.setLocation(new PushBean.LocationBean(420106, "30.539741", "114.358639"));
        postBean.setTitle("");

        LinkedHashMap<String, String> extras = new LinkedHashMap<>();
        Map<String, String> extra_post = new HashMap<>();
        int i = 0;
        for(String pic_path : picsPath) {
            if(pic_path != null) {
                i++;
                extras.put("extra." + i, "png");
                extra_post.put("extra." + i, pic_path);
            }
        }
        postBean.setExtra(extras);

        pushBean.setPost(postBean);
        String resultJson = pushBean.toString();

        MyCallback myCallback = new MyCallback() {
            @Override
            public void onSuccess() {
                Log.i("sannmizu.sendpost", "success");
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SendPostActivity.this, "成功发送", Toast.LENGTH_SHORT).show();
                send_text.setText("");
                picsPath.clear();
                pics_layout.removeAllViews();
            }

            @Override
            public void onFailure(String reason) {
                Log.i("sannmizu.sendpost", reason);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SendPostActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable t) {
                Log.e("sannmizu.sendpost", t.getMessage());
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SendPostActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
            }
        };
        if(picsPath.size() == 0) {
            PostPushResponse.sendPost(pushBean, myCallback);
        } else {
            PostPushResponse.sendPost_Pics(pushBean, extra_post, myCallback);
        }
    }

    private void choosePicture() {
        //获取权限
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); //打开相册
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK) {
                    handleImage(data);
                }
        }
    }

    private void handleImage(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            //如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        addPicture(imagePath);
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri,null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void addPicture(String path) {
        picsPath.add(path);
        Bitmap bitmap = getBitmap(path);
        //创建一个view
        ImageView view = new ImageView(this);
        view.setImageBitmap(bitmap);
        LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(300,500);
        l.setMarginStart(5);
        l.setMarginEnd(5);
        view.setLayoutParams(l);
        //点击
        view.setOnClickListener(v->{
            picsPath.remove(path);
            pics_layout.removeView(view);
        });
        //添加进布局
        pics_layout.addView(view);
    }
    private Bitmap getBitmap(String imagePath) {
        if(imagePath != null) {
            return BitmapFactory.decodeFile(imagePath);
        } else {
            return null;
        }
    }
}
