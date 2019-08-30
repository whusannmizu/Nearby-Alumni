package com.sannmizu.nearby_alumni.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sannmizu.nearby_alumni.R;

import java.util.ArrayList;
import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BaseObject> mRecordList;
    private List<Integer> mWhichList = new ArrayList<>();
    public static final int SendMsg = 0;
    public static final int ReceiveMsg = 1;
    public static final int TimeMsg = 2;

    public RecordAdapter(List<BaseObject> mRecordList) {
        this.mRecordList = mRecordList;
    }

    public static class LeftMsgHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public LeftMsgHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.left_msg);
        }
    }
    public static class RightMsgHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ProgressBar progressBar;
        public RightMsgHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.right_msg);
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
                    ((RightMsgHolder) holder).textView.setText(object.getContent());
                }
                if (mWhichList.contains(position)) {
                    ((RightMsgHolder) holder).progressBar.setVisibility(View.VISIBLE);
                } else {
                    ((RightMsgHolder) holder).progressBar.setVisibility(View.GONE);
                }
            }
                break;
            case ReceiveMsg: {
                RecordObject object = (RecordObject) mRecordList.get(position);
                if (object.isText()) {
                    ((LeftMsgHolder) holder).textView.setText(object.getContent());
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

    @Override
    public int getItemCount() {
        return mRecordList.size();
    }

    public List<Integer> getWhichList() {
        return mWhichList;
    }
}
