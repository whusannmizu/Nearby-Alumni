package com.sannmizu.nearby_alumni.chat;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sannmizu.nearby_alumni.NetUtils.FriendsResponse;
import com.sannmizu.nearby_alumni.NetUtils.MyCallback;
import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.denglu.guanzhu;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestHolder> {
    private List<UserObject> mRequestList;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public RequestAdapter(List<UserObject> mRequestList) {
        this.mRequestList = mRequestList;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(mCompositeDisposable != null) {
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }

    public static class RequestHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName;
        TextView userSign;
        ImageView agree;
        ImageView refuse;
        public RequestHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.piece_icon);
            userName = itemView.findViewById(R.id.piece_name);
            userSign = itemView.findViewById(R.id.piece_text);
            agree = itemView.findViewById(R.id.agree);
            refuse = itemView.findViewById(R.id.refuse);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RequestHolder holder, int position) {
        UserObject user = mRequestList.get(position);
        holder.userName.setText(user.getName());
        holder.userSign.setText(user.getSign());
        if(user.getIcon() != null) {
            holder.userImage.setImageBitmap(user.getIcon());
        } else {
            holder.userImage.setImageResource(R.drawable.vector_drawable_default);
        }
        if(!holder.userImage.hasOnClickListeners()) {
            holder.userImage.setOnClickListener(v->{
                //guanzhu.actionStart(v.getContext(), user.getId());
            });
        }
        if(!holder.agree.hasOnClickListeners()) {
            holder.agree.setOnClickListener(v->{
                if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
                    mCompositeDisposable.add(FriendsResponse.addFriend(user.getId(), new MyCallback() {
                        @Override
                        public void onSuccess() {
                            //TODO:图片变化
                            Toast.makeText(holder.itemView.getContext(), "成功添加好友", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String reason) {
                            Toast.makeText(holder.itemView.getContext(), "添加好友失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable t) {
                            Toast.makeText(holder.itemView.getContext(), "错误", Toast.LENGTH_SHORT).show();
                        }
                    }));
                }
            });
        }
        if(!holder.refuse.hasOnClickListeners()) {
            holder.refuse.setOnClickListener(v->{
                if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
                    mCompositeDisposable.add(FriendsResponse.deleteFriend(user.getId(), new MyCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(holder.itemView.getContext(), "成功添加好友", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String reason) {
                            Toast.makeText(holder.itemView.getContext(), "添加好友失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable t) {
                            Toast.makeText(holder.itemView.getContext(), "错误", Toast.LENGTH_SHORT).show();
                        }
                    }));
                }
            });
        }
    }

    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_request_piece, parent, false);
        return new RequestHolder(view);
    }

    @Override
    public int getItemCount() {
        return mRequestList.size();
    }

}
