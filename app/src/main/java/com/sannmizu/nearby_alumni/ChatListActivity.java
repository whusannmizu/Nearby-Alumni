package com.sannmizu.nearby_alumni;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.sannmizu.nearby_alumni.chat.BaseObject;
import com.sannmizu.nearby_alumni.chat.UserListAdapter;
import com.sannmizu.nearby_alumni.chat.FriendListActivity;
import com.sannmizu.nearby_alumni.chat.StartObject;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {
    private List<BaseObject> chatList = new ArrayList<>();
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("聊天");
        toolbar.inflateMenu(R.menu.chat_menu);
        toolbar.setNavigationOnClickListener(v->{
            finish();
        });

        initChat();
        mRecyclerView = findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        UserListAdapter adapter = new UserListAdapter(chatList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    private void initChat() {
        chatList.add(new StartObject("好友列表", FriendListActivity.class));
    }
}
