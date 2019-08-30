package com.sannmizu.nearby_alumni.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sannmizu.nearby_alumni.Database.ChatRecord;
import com.sannmizu.nearby_alumni.NetUtils.ChatResponse;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.utils.AESUtils;
import com.sannmizu.nearby_alumni.utils.SharedPreUtils;

import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

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
        mSendBtn.setOnClickListener(v->{
            if(mEditTv.getText().length() != 0) {
                ChatRecord chatRecord = createMessage();
                mEditTv.getText().clear();
                int position = showMessage(chatRecord);
                sendMessage(chatRecord, position);
            }
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
        String sendValue = AESUtils.encryptFromLocal( "{\"content\":\"" + chatRecord.getContent() + "\"}", this);
        ChatResponse.generateService(ChatActivity.this).chat(friend_id, sendValue, SharedPreUtils.getString("logToken", ""), SharedPreUtils.getString("connToken", ""))
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
