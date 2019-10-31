package com.sannmizu.nearby_alumni.postPage;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sannmizu.nearby_alumni.NetUtils.MyResponse;
import com.sannmizu.nearby_alumni.NetUtils.User;
import com.sannmizu.nearby_alumni.NetUtils.UserSearchResponse;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.cacheUtils.MyBitmapUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PostObject> mPostList;
    public static final MyBitmapUtils mBitmapUtils = new MyBitmapUtils();

    public static final int LOADING_MORE = 0;
    public static final int LOADING_NOW = 1;
    public static final int LOADING_OVER = 2;
    public static final int LOADING_ERROR = 3;
    private int loading = LOADING_MORE;

    public PostAdapter(List<PostObject> mRequestList) {
        this.mPostList = mRequestList;
    }

    public static class PostHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView userName;
        TextView postTime;
        TextView postText;
        GridLayout picsLayout;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            userName = itemView.findViewById(R.id.user_name);
            postTime = itemView.findViewById(R.id.post_time);
            postText = itemView.findViewById(R.id.post_text);
            picsLayout = itemView.findViewById(R.id.pics);
        }
    }
    public static class FooterHolder extends RecyclerView.ViewHolder {
        TextView tv;
        public FooterHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.prompt);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.post_piece, parent, false);
            return new PostHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rv_item_footer, parent, false);
            return new FooterHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PostHolder) {
            PostObject object = mPostList.get(position);
            ((PostHolder)holder).userName.setText(object.getUserName());
            ((PostHolder)holder).postTime.setText(object.getPostTime());
            ((PostHolder)holder).postText.setText(object.getPostText());
            //获取图片
            List<String> paths = object.getMediasPath();
            if(paths != null && ((PostHolder) holder).picsLayout.getChildCount() == 0) {
                for (String eachPath : paths) {
                    ImageView imageView = new ImageView(((PostHolder) holder).picsLayout.getContext());
                    imageView.setImageResource(R.drawable.vector_drawable_defaultpicture);
                    LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(300, 500);
                    l.setMarginStart(5);
                    l.setMarginEnd(5);
                    imageView.setLayoutParams(l);
                    ((PostHolder) holder).picsLayout.addView(imageView);
                    mBitmapUtils.disPlay(imageView, eachPath);
                }
            }
            //获取头像
            if(object.getAvatar() == null) {
                if(object.isHasAvatar()) {
                    UserSearchResponse.generateService().searchInfoById(String.valueOf(object.getUserId()), UserSearchResponse.Info_Avatar)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<MyResponse<User>>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                }

                                @Override
                                public void onNext(MyResponse<User> userMyResponse) {
                                    if (userMyResponse.getCode() == 0) {
                                        byte[] bytes = Base64.decode(userMyResponse.getData().getInfo().getIcon_base64(), Base64.NO_WRAP);
                                        object.setAvatar(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                        object.setHasAvatar(true);
                                        notifyItemChanged(position);
                                    } else {
                                        object.setHasAvatar(false);
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    object.setHasAvatar(false);
                                }

                                @Override
                                public void onComplete() {
                                }
                            });
                }
            } else {
                ((PostHolder)holder).avatar.setImageBitmap(object.getAvatar());
            }
        } else if (holder instanceof FooterHolder) {
            String say = "加载出错";
            switch (loading) {
                case LOADING_MORE:
                    say = "下拉加载更多";
                    break;
                case LOADING_NOW:
                    say = "正在加载";
                    break;
                case LOADING_OVER:
                    say = "已经没有更多了";
                    break;
                case LOADING_ERROR:
                    say = "加载出错";
                    break;
            }
            ((FooterHolder)holder).tv.setText(say);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == mPostList.size()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return mPostList.size() + 1;
    }

    public void setLoading(int i) {
        loading = i;
        notifyItemChanged(mPostList.size());
    }
}
