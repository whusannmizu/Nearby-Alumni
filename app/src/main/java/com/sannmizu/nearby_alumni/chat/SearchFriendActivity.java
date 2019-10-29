package com.sannmizu.nearby_alumni.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sannmizu.nearby_alumni.database.Users;
import com.sannmizu.nearby_alumni.NetUtils.MyResponse;
import com.sannmizu.nearby_alumni.NetUtils.RequestsResponse;
import com.sannmizu.nearby_alumni.NetUtils.User;
import com.sannmizu.nearby_alumni.NetUtils.UserSearchResponse;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static org.litepal.LitePalApplication.getContext;


public class SearchFriendActivity extends AppCompatActivity {
    private List<BaseObject> mUserList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private List<UserObject> mRequestList = new ArrayList<>();
    private RecyclerView mRequestsView;

    private ConstraintLayout mLayout;
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private AppCompatImageView mSearchImage;
    private AppCompatTextView mKanbanView;
    private TextView mSearchHintView;
    private ProgressBar mProgressBar;

    private Disposable disposable = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        initView();
        setAttributes();
        setListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //initRequestList();
        RequestsResponse.generateService().getRequestList(Utils.getLogToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response->{
                    if(response.getCode() == 0) {
                        for(User user : response.getData().getRequests()) {
                            mRequestList.add(new UserObject(new Users(user)));
                        }
                        mRequestsView.getAdapter().notifyDataSetChanged();
                    } else {
                        Toast.makeText(SearchFriendActivity.this, "获取好友申请列表失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initView() {
        mLayout = findViewById(R.id.root_layout);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.search_friend_menu);
        mKanbanView = findViewById(R.id.pretend_tv);
        mSearchHintView = findViewById(R.id.search_tv);
        mSearchView = mToolbar.findViewById(R.id.menu_search);
        mSearchImage = mSearchView.findViewById(androidx.appcompat.R.id.search_button);
        mRecyclerView = findViewById(R.id.recycle_view);
        mRequestsView = findViewById(R.id.friend_request);
        mProgressBar = findViewById(R.id.progress_bar);
    }

    private void setAttributes() {
        mToolbar.setTitle("添加好友");
        mSearchView.setQueryHint("手机号/校友号/昵称");
        mSearchImage.setVisibility(View.GONE);
        //好友申请列表
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        mRequestsView.setLayoutManager(layoutManager1);
        RequestAdapter adapter1 = new RequestAdapter(mRequestList);
        mRequestsView.setAdapter(adapter1);
        //搜索用户列表
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager2);
        UserListAdapter adapter2 = new UserListAdapter(mUserList);
        mRecyclerView.setAdapter(adapter2);
    }

    private void setListener() {
        mLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mLayout.requestFocus();
                InputMethodManager manager = ((InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE));
                if (manager != null)
                    manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
        mToolbar.setNavigationOnClickListener(v->{
            onBackPressed();
        });
        mKanbanView.setOnClickListener(v->{
            showSearchPage();
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUser(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchHintView.setText("搜索用户" + newText);
                if(newText.equals("")) {
                    mSearchHintView.setVisibility(View.GONE);
                } else {
                    mSearchHintView.setVisibility(View.VISIBLE);
                }
                mUserList.clear();
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {;
                showAddPage();
                return true;
            }
        });
        mSearchHintView.setOnClickListener(v->{
            searchUser(String.valueOf(mSearchView.getQuery()));
        });
    }

    @Override
    public void onBackPressed() {
        if(mSearchView.hasFocus()) {
            showAddPage();
        } else {
            finish();
        }
    }
    private void showAddPage() {
        mSearchView.onActionViewCollapsed();
        mSearchImage.setVisibility(View.GONE);
        mToolbar.setTitle("添加好友");
        mSearchHintView.setVisibility(View.GONE);
        mKanbanView.setVisibility(View.VISIBLE);
        mRequestsView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mUserList.clear();
    }

    private void showSearchPage() {
        mToolbar.setTitle("");
        mSearchView.setFocusable(true);
        mSearchView.setIconified(false);
        mKanbanView.setVisibility(View.GONE);
        mRequestsView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void searchUser(String data) {
        Observable<UserSearchResponse> tel = UserSearchResponse.generateService().search("tel", data).subscribeOn(Schedulers.io());
        Observable<UserSearchResponse> name = UserSearchResponse.generateService().search("name", data, 1).subscribeOn(Schedulers.io());
        Observable<String> userFromId = Observable.just(data);

        Observable<String> userFromTelAndName =
                Observable.merge(tel, name)
                .flatMap(new Function<UserSearchResponse, ObservableSource<UserSearchResponse.AUser>>() {
                    @Override
                    public ObservableSource<UserSearchResponse.AUser> apply(UserSearchResponse userSearchResponse) throws Exception {
                        Log.i("sannmizu.search", userSearchResponse.getDescription() + ":" + userSearchResponse.getReason());
                        return Observable.fromIterable(userSearchResponse.getData().getUsers());
                    }
                })
                .map(new Function<UserSearchResponse.AUser, String>() {
                    @Override
                    public String apply(UserSearchResponse.AUser user) throws Exception {
                        return String.valueOf(user.getUser().getId());
                    }
                });

        Observable.merge(userFromTelAndName, userFromId)
                .filter( s -> s.matches("[0-9]+"))
                .observeOn(Schedulers.io())
                .flatMap(new Function<String, ObservableSource<MyResponse<User>>>() {
                    @Override
                    public ObservableSource<MyResponse<User>> apply(String s) throws Exception {
                        return UserSearchResponse.generateService().searchById(s);
                    }
                })
                .filter(response -> response.getCode() == 0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MyResponse<User>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mSearchHintView.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(MyResponse<User> response) {
                        //更新视图
                        mUserList.add(new UserObject(new Users(response.getData())));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("sannmizu.search", e.getMessage());
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete() {
                        try {
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }
}
