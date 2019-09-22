package com.sannmizu.nearby_alumni.chat;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.cacheUtils.MyBitmapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class RecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BaseObject> mRecordList;
    private List<Long> mWhichList = new ArrayList<>();
    public static final int SendMsg = 0;
    public static final int ReceiveMsg = 1;
    public static final int TimeMsg = 2;
    public static final MyBitmapUtils mBitmapUtils = new MyBitmapUtils();

    public RecordAdapter(List<BaseObject> mRecordList) {
        this.mRecordList = mRecordList;
    }

    public static class LeftMsgHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        public LeftMsgHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.left_msg);
            imageView = itemView.findViewById(R.id.left_image);
        }
    }
    public static class RightMsgHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        ProgressBar progressBar;
        public RightMsgHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.right_msg);
            imageView = itemView.findViewById(R.id.right_image);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
    public static class TimeMsgHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public TimeMsgHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.time_msg);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case SendMsg:
                view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_msg_right, parent, false);
                holder = new RightMsgHolder(view);
                break;
            case ReceiveMsg:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_msg_left, parent, false);
                holder = new LeftMsgHolder(view);
                break;
            case TimeMsg:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_msg_time, parent, false);
                holder = new TimeMsgHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return mRecordList.get(position).getType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(getItemViewType(position)) {
            case SendMsg: {
                RecordObject object = (RecordObject) mRecordList.get(position);
                if (object.isText()) {
                    ((RightMsgHolder) holder).imageView.setVisibility(View.GONE);
                    ((RightMsgHolder) holder).textView.setVisibility(View.VISIBLE);
                    ((RightMsgHolder) holder).textView.setText(object.getContent());
                } else {
                    ((RightMsgHolder) holder).imageView.setVisibility(View.VISIBLE);
                    ((RightMsgHolder) holder).textView.setVisibility(View.GONE);
                    //获得图片bitmap
                    mBitmapUtils.disPlay(((RightMsgHolder) holder).imageView, object.getContent());
                }
                if (mWhichList.contains(getTAG(position))) {
                    ((RightMsgHolder) holder).progressBar.setVisibility(View.VISIBLE);
                } else {
                    ((RightMsgHolder) holder).progressBar.setVisibility(View.GONE);
                }
                if(!((RightMsgHolder) holder).imageView.hasOnClickListeners()) {
                    ((RightMsgHolder) holder).imageView.setOnClickListener(v->{
                        //TODO:查看大图
                    });
                }
            }
                break;
            case ReceiveMsg: {
                RecordObject object = (RecordObject) mRecordList.get(position);
                if (object.isText()) {
                    ((LeftMsgHolder) holder).imageView.setVisibility(View.GONE);
                    ((LeftMsgHolder) holder).textView.setVisibility(View.VISIBLE);
                    ((LeftMsgHolder) holder).textView.setText(object.getContent());
                } else {
                    ((LeftMsgHolder) holder).imageView.setVisibility(View.VISIBLE);
                    ((LeftMsgHolder) holder).textView.setVisibility(View.GONE);
                    //获得图片bitmap
                    mBitmapUtils.disPlay(((LeftMsgHolder) holder).imageView, object.getContent());
                }
                if(!((LeftMsgHolder) holder).imageView.hasOnClickListeners()) {
                    ((LeftMsgHolder) holder).imageView.setOnClickListener(v->{
                        //TODO:查看大图
                    });
                }
            }
                break;
            case TimeMsg: {
                TimeObject object = (TimeObject) mRecordList.get(position);
                ((TimeMsgHolder) holder).textView.setText(object.getFormatTime());
                break;
            }
        }
    }

    private Long getTAG(int position) {
        return mRecordList.get(position).getTAG();
    }
    @Override
    public int getItemCount() {
        return mRecordList.size();
    }

    public List<Long> getWhichList() {
        return mWhichList;
    }

    public void notifyItemChanged(Long Tag) {
        ListIterator<BaseObject> iterator = mRecordList.listIterator();
        int num = 0;
        while(iterator.hasNext()) { //游标定位到结尾
            iterator.next();
        }
        while(iterator.hasPrevious()) { //逆向遍历
            if(iterator.previous().getTAG().equals(Tag)) {
                notifyItemChanged(iterator.nextIndex());
                break;
            }
        }
    }
}
