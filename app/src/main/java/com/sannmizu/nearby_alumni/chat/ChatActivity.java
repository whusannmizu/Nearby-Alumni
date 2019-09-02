package com.sannmizu.nearby_alumni.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sannmizu.nearby_alumni.Database.ChatRecord;
import com.sannmizu.nearby_alumni.NetUtils.ChatResponse;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.cacheUtils.LocalCacheUtils;
import com.sannmizu.nearby_alumni.utils.AESUtils;
import com.sannmizu.nearby_alumni.utils.SharedPreUtils;
import com.sannmizu.nearby_alumni.utils.Utils;

import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChatActivity extends AppCompatActivity {
    //传入和谁的聊天窗口
    public static void actionStart(Context context, int id, String name) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        context.startActivity(intent);
    }
    public static final int CHOOSE_PHOTO = 2;

    private int user_id;
    private int friend_id;
    private int startMsg;
    private String friend_name;
    private List<BaseObject> mChatRecordList = new ArrayList<>();
    private Date mLatestTime;
    private Date mEarliestTime;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecordAdapter mRecordAdapter;
    private EditText mEditTv;
    private Button mSendBtn;
    private ImageButton mSendPic;

    private IntentFilter intentFilter;
    private NewMessageReceiver newMessageReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        friend_id = intent.getIntExtra("id",0);
        friend_name = intent.getStringExtra("name");
        startMsg = 0;
        if(friend_id == 0) {
            Log.i("sannmizu.chat", "intent缺少id");
            finish();
        } else if(friend_id == SharedPreUtils.getInt("currentUser", 0)) {
            Log.i("sannmizu.chat", "id==self");
            finish();
        }

        intentFilter = new IntentFilter();
        intentFilter.addAction("sannmizu.chat.NEW_MESSAGE");
        newMessageReceiver = new NewMessageReceiver();
        registerReceiver(newMessageReceiver, intentFilter);
        //初始化数据
        initView();
        setAttributes();
        setListener();
        initRecord(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(newMessageReceiver);
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.recycle_view);
        mEditTv = findViewById(R.id.input_text);
        mSendBtn = findViewById(R.id.send_button);
        mSendPic = findViewById(R.id.choose_picture);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
    }

    private void setAttributes() {
        mToolbar.setTitle(friend_name);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecordAdapter = new RecordAdapter(mChatRecordList);
        mRecyclerView.setAdapter(mRecordAdapter);
    }

    private void setListener() {
        mToolbar.setNavigationOnClickListener(v->{
            finish();
        });
        mEditTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() != 0) {
                    mSendBtn.setVisibility(View.VISIBLE);
                    mSendPic.setVisibility(View.GONE);
                } else {
                    mSendBtn.setVisibility(View.GONE);
                    mSendPic.setVisibility(View.VISIBLE);
                }
            }
        });
        mSendBtn.setOnClickListener(v->{
            if(mEditTv.getText().length() != 0) {
                ChatRecord chatRecord = createMessage();
                mEditTv.getText().clear();
                int position = showMessage(chatRecord);
                sendMessage(chatRecord, position);
            }
        });
        mSendPic.setOnClickListener(v->{
            choosePicture();
        });
        mSwipeRefreshLayout.setOnRefreshListener(()->{
            SharedPreUtils.putBoolean("FriendListExpired", true);
            initRecord(false);
        });
    }
    private void initRecord(boolean isFirst) {
        user_id = SharedPreUtils.getInt("currentUser", 0);
        LitePal.where("user_id = ? and friend_id = ?", String.valueOf(user_id), String.valueOf(friend_id))
            .order("time desc").limit(10).offset(startMsg)
            .findAsync(ChatRecord.class).listen(list -> {
                for(ChatRecord record : list) {
                    addToRecordList(new RecordObject(record), false);
                }
                if(list.size() < 10) {
                    mSwipeRefreshLayout.setEnabled(false);
                }
                mRecyclerView.getAdapter().notifyDataSetChanged();
                if(isFirst) {
                    mRecyclerView.scrollToPosition(mChatRecordList.size() - 1);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            });
    }

    private ChatRecord createMessage() {
        ChatRecord record = new ChatRecord();
        record.setContent(mEditTv.getText().toString());
        record.setText(true);
        record.setFriend_id(friend_id);
        record.setUser_id(user_id);
        record.setSubject(0);
        record.setTime(new Date(System.currentTimeMillis()));
        return record;
    }
    private int showMessage(ChatRecord chatRecord) {
        int position = addToRecordList(new RecordObject(chatRecord), true);
        mRecordAdapter.getWhichList().add(position);
        mRecordAdapter.notifyItemChanged(position);
        mRecyclerView.smoothScrollToPosition(position);
        return position;
    }
    private void sendMessage(ChatRecord chatRecord, int position) {
        String sendValue = ChatResponse.getTextRequest(chatRecord.getContent());
        ChatResponse.generateService().chat(friend_id, sendValue, SharedPreUtils.getString("logToken", ""), SharedPreUtils.getString("connToken", ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChatResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ChatResponse chatResponse) {
                        if(chatResponse.getCode() == 0) {
                            Toast.makeText(ChatActivity.this, chatResponse.getData().getState(), Toast.LENGTH_SHORT).show();
                            chatRecord.save();
                        } else {
                            //TODO:设置重试按钮
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mRecordAdapter.getWhichList().remove(Integer.valueOf(position));
                        mRecordAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onComplete() {
                        mRecordAdapter.getWhichList().remove(Integer.valueOf(position));
                        mRecordAdapter.notifyItemChanged(position);
                    }
                });
    }

    private synchronized int addToRecordList(RecordObject recordObject, boolean positive) {
        if (mChatRecordList.size() == 0) {
            mLatestTime = mEarliestTime = recordObject.getTime();
            TimeObject timeObject = new TimeObject(mLatestTime);
            if(positive) {
                mChatRecordList.add(timeObject);
                mChatRecordList.add(recordObject);
            } else {
                mChatRecordList.add(0, recordObject);
                mChatRecordList.add(0, timeObject);
            }
        } else {
            if(positive) {
                if (mLatestTime.getTime() < recordObject.getTime().getTime() - 180000) {    //3分钟
                    mLatestTime = recordObject.getTime();
                    TimeObject timeObject = new TimeObject(mLatestTime);
                    mChatRecordList.add(timeObject);
                    mChatRecordList.add(recordObject);
                } else {
                    mLatestTime = recordObject.getTime();
                    mChatRecordList.add(recordObject);
                }
            } else {
                if (mEarliestTime.getTime() > recordObject.getTime().getTime() + 180000) {    //3分钟
                    mEarliestTime = recordObject.getTime();
                    TimeObject timeObject = new TimeObject(mEarliestTime);
                    mChatRecordList.add(0, recordObject);
                    mChatRecordList.add(0, timeObject);
                } else {
                    mEarliestTime = recordObject.getTime();
                    TimeObject timeObject = new TimeObject(mEarliestTime);
                    mChatRecordList.remove(0);
                    mChatRecordList.add(0, recordObject);
                    mChatRecordList.add(0, timeObject);
                }
            }
        }
        startMsg += 1;
        mRecyclerView.getAdapter().notifyDataSetChanged();
        return mChatRecordList.size() - 1;
    }

    private class NewMessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            try {
                if (bundle.getInt("user_id", 0) == user_id && bundle.getInt("friend_id", 0) == friend_id) {
                    addToRecordList(bundle.getParcelable("message"), true);
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(mChatRecordList.size() - 1);
                    //TODO:改成有个气泡提示
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void choosePicture() {
        if(ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "你拒绝了权限请求，无法打开相册", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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
        sendPicture(imagePath);
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
    private void sendPicture(String imagePath) {
        Bitmap bitmap = getBitmap(imagePath);
        ChatRecord chatRecord = createMessage(imagePath);
        int position = showMessage(chatRecord, bitmap);
        sendMessage(chatRecord, position, bitmap);
    }
    private Bitmap getBitmap(String imagePath) {
        if(imagePath != null) {
            return BitmapFactory.decodeFile(imagePath);
        } else {
            return null;
        }
    }
    private ChatRecord createMessage(String url) {
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setUser_id(user_id);
        chatRecord.setFriend_id(friend_id);
        chatRecord.setSubject(0);
        chatRecord.setContent(url);
        chatRecord.setText(false);
        chatRecord.setTime(new Date(System.currentTimeMillis()));
        return chatRecord;
    }
    private int showMessage(ChatRecord chatRecord, Bitmap bitmap) {
        //存入本地
        LocalCacheUtils localCacheUtils = new LocalCacheUtils();
        localCacheUtils.setBitmapToLocal(chatRecord.getContent(), bitmap);
        //加入列表
        int position = addToRecordList(new RecordObject(chatRecord), true);
        mRecordAdapter.getWhichList().add(position);
        mRecordAdapter.notifyItemChanged(position);
        mRecyclerView.smoothScrollToPosition(position);
        return position;
    }
    private void sendMessage(ChatRecord chatRecord, int position, Bitmap bitmap) {
        String sendValue = ChatResponse.getPictureRequest("png");
        String picString = getPNGFromBitmapToString(bitmap);
        ChatResponse.generateService().chat(friend_id, sendValue, picString, Utils.getLogToken(), Utils.getConnToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ChatResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ChatResponse chatResponse) {
                        if(chatResponse.getCode() == 0) {
                            Toast.makeText(ChatActivity.this, chatResponse.getData().getState(), Toast.LENGTH_SHORT).show();
                            chatRecord.save();
                        } else {
                            //TODO:设置重试按钮
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mRecordAdapter.getWhichList().remove(Integer.valueOf(position));
                        mRecordAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onComplete() {
                        mRecordAdapter.getWhichList().remove(Integer.valueOf(position));
                        mRecordAdapter.notifyItemChanged(position);
                    }
                });
    }
    private String getPNGFromBitmapToString(Bitmap bitmap) {
        byte[] bytes;
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            bytes = outStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }
}
