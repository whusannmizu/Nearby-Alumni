package com.sannmizu.nearby_alumni.bottomNavigation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sannmizu.nearby_alumni.Locate;
import com.sannmizu.nearby_alumni.NetUtils.LocateResponse;
import com.sannmizu.nearby_alumni.NetUtils.MyResponse;
import com.sannmizu.nearby_alumni.NetUtils.PostPullResponse;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.denglu.Locateback;
import com.sannmizu.nearby_alumni.postPage.EndlessRecyclerOnScrollListener;
import com.sannmizu.nearby_alumni.postPage.PostAdapter;
import com.sannmizu.nearby_alumni.postPage.PostObject;
import com.sannmizu.nearby_alumni.postPage.SendPostActivity;
import com.sannmizu.nearby_alumni.utils.AccountUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment {
    private View mView;

    private List<PostObject> mPostList = new ArrayList<>();
    private List<Integer> mPostIdList = new ArrayList<>();
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Spinner mSpinner;

    private Long mTimestamp;
    private int mPage;
    private String mLatitude;
    private String mLongitude;
    private String mPostType;
    Thread mCheckTimeout;

    private int overtime = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_show_post_page, container, false);

        initView();
        setAttributes();
        setListener();

        mTimestamp = new Date().getTime() / 1000;
        mPage = 1;
        refreshPost();
        return mView;
    }

    private void initView() {
        mToolbar = mView.findViewById(R.id.toolbar);
        mRecyclerView = mView.findViewById(R.id.recycle_view);
        mSwipeRefreshLayout = mView.findViewById(R.id.swipe_refresh);
        mSpinner = mView.findViewById(R.id.post_spinner);
    }

    private void setAttributes() {
        mToolbar.setTitle("动态");
        mToolbar.inflateMenu(R.menu.post_menu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        PostAdapter adapter = new PostAdapter(mPostList);
        mRecyclerView.setAdapter(adapter);
        ((PostAdapter)mRecyclerView.getAdapter()).setLoading(PostAdapter.LOADING_NOW);
        mSwipeRefreshLayout.setRefreshing(true);
    }

    private void setListener() {
        mSwipeRefreshLayout.setOnRefreshListener(()->{
            refreshPost();
        });
        mToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.send_post:
                    Intent intent = new Intent(getContext(), SendPostActivity.class);
                    getContext().startActivity(intent);
                    break;
            }
            return true;
        });
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                loadMorePost();
            }
        });
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        mPostType = "surround";
                        break;
                    case 1:
                        mPostType = "friend";
                        break;
                    case 2:
                        mPostType = "follow";
                        break;
                    default:
                        mPostType = "surround";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mPostType = "surround";
            }
        });
    }

    private void refreshPost() {
        mPostIdList.clear();
        mPostList.clear();
        //获取好友列表（待做）
        //1.获取当前位置（先做）
        mCheckTimeout = new Thread(new Runnable() {
            @Override
            public void run() {
                while(overtime <= 15 && !Thread.currentThread().isInterrupted()) {
                    try {
                       Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    overtime++;
                }
                if(overtime > 15) {
                    Log.i("sannmizu.blog", "第一次定位失败");
                    getLocation();
                }
            }
        });
        mCheckTimeout.start();
        //获取权限
        List< String > permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add( Manifest.permission.ACCESS_FINE_LOCATION);
        } if (ContextCompat.checkSelfPermission( getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add( Manifest.permission.READ_PHONE_STATE);
        } if (ContextCompat.checkSelfPermission( getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add( Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray( new String[ permissionList. size()]);
            ActivityCompat.requestPermissions( getActivity(), permissions, 1);
        } else {
            getLocation();
        }
    }
    private void getLocation() {
        Locate.requestLocation(new Locateback() {
            @Override
            public void onReceiveLocation(String latitude, String longitude) {
                if(mCheckTimeout.isAlive()) {
                    mCheckTimeout.interrupt();
                }
                Log.i("sannmizu.blog","定位成功");
                //2.上传当前位置
             //   LocateResponse.update(latitude, longitude);
                //3.找到所有附近的动态的id,用拉取到的id获取动态内容
                pullPost(mTimestamp, 1);
                //4.转换格式存到mPostList中
                mPage = 1;
            }

            @Override
            public void onFailure() {
                ((PostAdapter) mRecyclerView.getAdapter()).setLoading(PostAdapter.LOADING_ERROR);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadMorePost() {
        if(mPostIdList.size() < 10*mPage) {
            ((PostAdapter)mRecyclerView.getAdapter()).setLoading(PostAdapter.LOADING_OVER);
            return;
        }
        ((PostAdapter)mRecyclerView.getAdapter()).setLoading(PostAdapter.LOADING_NOW);
        pullPost(mTimestamp, ++mPage);
    }

    private void pullPost(Long timestamp, int page) {
        Observable<PostPullResponse> postResponse;
        if(mPostType.equals("surround")) {
            if (mLatitude != null && mLongitude != null) {
                postResponse = PostPullResponse.generateService().pullByLoc(AccountUtils.getLogToken(), timestamp, page, mLatitude, mLongitude);
            } else {
                postResponse = PostPullResponse.generateService().pull(AccountUtils.getLogToken(), "surround", timestamp, page);
            }
        } else if(mPostType.equals("friend")) {
            postResponse = PostPullResponse.generateService().pull(AccountUtils.getLogToken(), "friend", timestamp, page);
        } else if(mPostType.equals("follow")) {
            postResponse = PostPullResponse.generateService().pull(AccountUtils.getLogToken(), "follow", timestamp, page, "normal");
        } else {
            postResponse = Observable.error(new Exception("Spinner错误"));
        }
        Observable<String> postId =
                postResponse.flatMap(new Function<PostPullResponse, ObservableSource<PostPullResponse.APost>>() {
                    @Override
                    public ObservableSource<PostPullResponse.APost> apply(PostPullResponse postPullResponse) throws Exception {
                        Log.i("sannmizu.blog", postPullResponse.getDescription() + ":" + postPullResponse.getResult());
                        if(postPullResponse.getCode() != 0) {
                            return Observable.error(new Exception(postPullResponse.getReason()));
                        }
                        return Observable.fromIterable(postPullResponse.getData().getPosts());
                    }
                })
                .map(new Function<PostPullResponse.APost, String>() {
                    @Override
                    public String apply(PostPullResponse.APost aPost) throws Exception {
                        int id = aPost.getPost().getId();
                        if(mPostIdList.contains(id)) {
                            return null;
                        } else {
                            mPostIdList.add(id);
                            return String.valueOf(aPost.getPost().getId());
                        }
                    }
                });

        postId.subscribeOn(Schedulers.io())
                .filter(s -> s != null && s.length() != 0)
                .flatMap(new Function<String, ObservableSource<MyResponse<PostPullResponse.APost>>>() {
                    @Override
                    public ObservableSource<MyResponse<PostPullResponse.APost>> apply(String s) throws Exception {
                        return PostPullResponse.generateService().searchById(s);
                    }
                })
                .filter(response -> response.getCode() == 0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MyResponse<PostPullResponse.APost>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MyResponse<PostPullResponse.APost> response) {
                        Log.i("sannmizu.blog", response.getResult());
                        mPostList.add(0, new PostObject(response.getData().getPost()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("sannmizu.blog", e.getMessage() + "");
                        mSwipeRefreshLayout.setRefreshing(false);
                        ((PostAdapter)mRecyclerView.getAdapter()).setLoading(PostAdapter.LOADING_ERROR);
                    }

                    @Override
                    public void onComplete() {
                        try {
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                        ((PostAdapter)mRecyclerView.getAdapter()).setLoading(PostAdapter.LOADING_MORE);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    refreshPost();
                } else {
                    ((PostAdapter) mRecyclerView.getAdapter()).setLoading(PostAdapter.LOADING_ERROR);
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "你拒绝了权限请求，无法查看附近的动态", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
