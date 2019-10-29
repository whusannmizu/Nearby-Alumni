package com.sannmizu.nearby_alumni;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.sannmizu.nearby_alumni.database.Users;
import com.sannmizu.nearby_alumni.chat.BaseObject;
import com.sannmizu.nearby_alumni.chat.ChatObject;
import com.sannmizu.nearby_alumni.chat.NewMsgBean;
import com.sannmizu.nearby_alumni.chat.UserListAdapter;
import com.sannmizu.nearby_alumni.chat.FriendListActivity;
import com.sannmizu.nearby_alumni.chat.StartObject;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity {
    private List<BaseObject> mChatList = new ArrayList<>();
    private List<StartObject> mStartList = Arrays.asList(new StartObject("好友列表", FriendListActivity.class));
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private UserListAdapter mAdapter;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener mSharedListener = ((sharedPreferences, s) -> {
        String msg = sharedPreferences.getString(s, "");
        if(msg.equals("")) {
            deleteFromChatList(Integer.valueOf(s));
        } else {
            replaceIntoChatList(Integer.valueOf(s), NewMsgBean.getInstance(msg));
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        initView();
        setAttributes();
        setListener();
        initChat();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(mSharedListener);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);

        mRecyclerView = findViewById(R.id.recycle_view);
        mAdapter = new UserListAdapter(mChatList);
        mSharedPreferences = getSharedPreferences("currentChatList", MODE_PRIVATE);
    }
    private void setAttributes() {
        toolbar.setTitle("聊天");
        toolbar.inflateMenu(R.menu.chat_menu);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
    private void setListener() {
        toolbar.setNavigationOnClickListener(v->{
            finish();
        });
        mSharedPreferences.registerOnSharedPreferenceChangeListener(mSharedListener);
    }
    private void initChat() {
        mChatList.addAll(mStartList);
        for(Map.Entry<String, ?> entry : mSharedPreferences.getAll().entrySet()) {
            replaceIntoChatList(Integer.valueOf(entry.getKey()), NewMsgBean.getInstance((String)entry.getValue()));
        }
    }

    private synchronized void replaceIntoChatList(int id, NewMsgBean msg) {
        //如果有，获取位置更新
        int oldPos = -1;
        int i = 0;
        for(BaseObject object : mChatList) {
            if (object.getType() == UserListAdapter.TYPE_CHAT) {
                if(((ChatObject)object).getFriend_id() == id) {
                    oldPos = i;
                    break;
                }
            }
            i++;
        }
        int newPos = mStartList.size();
        ChatObject chat;
        if(oldPos >= 0) {   //更新
            chat = (ChatObject) mChatList.get(oldPos);
            mChatList.remove(oldPos);
            chat.setText(msg.getContent());
            chat.setTime(msg.getTime());
        } else {        //插入
            Users user = LitePal.where("user_id = ?", String.valueOf(id)).findFirst(Users.class);
            chat = new ChatObject(user, msg);
        }
        mChatList.add(newPos, chat);
        mAdapter.notifyDataSetChanged();
    }
    private synchronized void deleteFromChatList(int id) {
        int position = mChatList.size();
        ListIterator<BaseObject> iterator = mChatList.listIterator();
        while(iterator.hasNext()) {
            BaseObject object = iterator.next();
            if(object.getType() == ChatObject.TYPE) {
                if(((ChatObject) object).getFriend_id() == id) {
                    position = iterator.previousIndex();
                    iterator.remove();
                }
            }
        }
        mAdapter.notifyItemRangeChanged(position, mChatList.size() - position);
    }
}
