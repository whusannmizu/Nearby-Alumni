package com.sannmizu.nearby_alumni.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sannmizu.nearby_alumni.database.User_friends;
import com.sannmizu.nearby_alumni.database.Users;
import com.sannmizu.nearby_alumni.NetUtils.FriendsResponse;
import com.sannmizu.nearby_alumni.NetUtils.User;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.utils.AccountUtils;
import com.sannmizu.nearby_alumni.utils.SharedPreUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class FriendListActivity extends AppCompatActivity {
    public static List<BaseObject> mFriendList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private Disposable disposable = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("好友列表");
        toolbar.inflateMenu(R.menu.friendlist_menu);
        toolbar.setNavigationOnClickListener(v->{
            finish();
        });
        toolbar.setOnMenuItemClickListener(v->{
            switch (v.getItemId()) {
                case R.id.item_search_friend:
                    Intent intent = new Intent(this, SearchFriendActivity.class);
                    startActivity(intent);
                    break;
            }
            return true;
        });

        mRecyclerView = findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        UserListAdapter adapter = new UserListAdapter(mFriendList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(()->{
            SharedPreUtils.putBoolean("FriendListExpired", true);
            initFriend();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initFriend();
    }


    private void initFriend() {
        int currentUser = SharedPreUtils.getInt("currentUser",0);
        if(currentUser == 0){
            //请先登录
            Toast.makeText(FriendListActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
        } else {
            Observable<List<BaseObject>> memory = Observable.create(new ObservableOnSubscribe<List<BaseObject>>() {
                @Override
                public void subscribe(ObservableEmitter<List<BaseObject>> emitter) throws Exception {
                    if(SharedPreUtils.getBoolean("FriendListExpired", false)) {
                        emitter.onComplete();
                    } else {
                        if (mFriendList.size() != 0) {
                            emitter.onNext(mFriendList);
                        } else {
                            emitter.onComplete();
                        }
                    }
                }
            });

            Observable<List<BaseObject>> local = Observable.create(new ObservableOnSubscribe<List<BaseObject>>() {
                @Override
                public void subscribe(ObservableEmitter<List<BaseObject>> emitter) throws Exception {
                    if(SharedPreUtils.getBoolean("FriendListExpired", false)) {
                        emitter.onComplete();
                    } else {
                        if (SharedPreUtils.getLong("FriendListDB_LastInsertTime", 0) - new Date().getTime() > 24 * 60 * 60 * 1000) {
                            emitter.onComplete();
                        } else {
                            List<Users> friends = LitePal.where("user_id in (select friend_id from user_friends where user_id = ?)", String.valueOf(currentUser)).find(Users.class);
                            if (friends.size() == 0) {
                                emitter.onComplete();
                            } else {
                                //数据库数据转化为内存数据
                                List<BaseObject> list = new ArrayList<>();
                                for (Users user : friends) {
                                    list.add(new UserObject(user));
                                }
                                emitter.onNext(list);
                            }
                        }
                    }
                }
            });

            Observable<List<BaseObject>> network = FriendsResponse.generateService().getFriendList(AccountUtils.getLogToken())
                    .flatMap(new Function<FriendsResponse, ObservableSource<List<BaseObject>>>() {
                        @Override
                        public ObservableSource<List<BaseObject>> apply(FriendsResponse friendsResponse) throws Exception {
                            if(friendsResponse.getCode() == 0) {
                                //网络数据转化为数据库数据
                                //存储用户信息
                                List<Users> friends = new ArrayList<>();
                                //存储好友关系数据
                                List<User_friends> user_friends = new ArrayList<>();
                                for (User user : friendsResponse.getData().getFriends()) {
                                    Users users = new Users(user);
                                    friends.add(users);
                                    users.saveOrUpdate("user_id = ?", String.valueOf(user.getId()));
                                    //存储用户信息
                                    User_friends user_friends_param = new User_friends();
                                    user_friends_param.setUser_id(currentUser);
                                    user_friends_param.setFriend_id(user.getId());
                                    user_friends.add(user_friends_param);
                                    //存储好友关系数据
                                    user_friends_param.saveOrUpdate("user_id = ? and friend_id = ?",String.valueOf(currentUser),String.valueOf(user.getId()));
                                }
                                //记录下操作时间，用于过期处理
                                SharedPreUtils.putLong("FriendListDB_LastInsertTime", new Date().getTime());
                                SharedPreUtils.putBoolean("FriendListExpired", false);
                                //数据库数据转化为内存数据
                                List<BaseObject> list = new ArrayList<>();
                                for (Users user : friends) {
                                    list.add(new UserObject(user));
                                }
                                return Observable.just(list);
                            } else {
                                Log.i("sannmizu.friend", "获取好友列表失败：" + friendsResponse.getReason());
                                return Observable.error(new Throwable(friendsResponse.getReason()));
                            }
                        }
                    });

            disposable = Observable.concat(memory, local, network)
                    .firstElement()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<BaseObject>>() {
                        @Override
                        public void accept(List<BaseObject> baseObjects) throws Exception {
                            if(baseObjects != mFriendList) {
                                mFriendList.clear();
                                for (BaseObject object : baseObjects) {
                                    mFriendList.add(object);
                                }
                                mRecyclerView.getAdapter().notifyDataSetChanged();
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(FriendListActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
