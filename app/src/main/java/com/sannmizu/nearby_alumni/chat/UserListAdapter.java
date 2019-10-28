package com.sannmizu.nearby_alumni.chat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sannmizu.nearby_alumni.R;
import com.sannmizu.nearby_alumni.denglu.guanzhu;

import java.util.List;


public class UserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BaseObject> mChatList;
    public static final int TYPE_HEAD = 0;
    public static final int TYPE_CHAT = 1;
    public static final int TYPE_USER = 2;
    public UserListAdapter(List<BaseObject> mChatList) {
        this.mChatList = mChatList;
    }

    public static class StartHolder extends RecyclerView.ViewHolder {
        TextView startText;
        public StartHolder(@NonNull View itemView) {
            super(itemView);
            startText = itemView.findViewById(R.id.start_piece);
        }
    }

    public static class ChatHolder extends RecyclerView.ViewHolder {
        ImageView chatImage;
        TextView chatName;
        TextView chatText;
        TextView chatTime;
        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            chatImage = itemView.findViewById(R.id.piece_icon);
            chatName = itemView.findViewById(R.id.piece_name);
            chatText = itemView.findViewById(R.id.piece_text);
            chatTime = itemView.findViewById(R.id.piece_time);
        }
    }

    public static class FriendHolder extends RecyclerView.ViewHolder {
        ImageView friendImage;
        TextView friendName;
        TextView friendSign;
        public FriendHolder(@NonNull View itemView) {
            super(itemView);
            friendImage = itemView.findViewById(R.id.piece_icon);
            friendName = itemView.findViewById(R.id.piece_name);
            friendSign = itemView.findViewById(R.id.piece_text);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BaseObject object = mChatList.get(position);
        switch(getItemViewType(position)) {
            case TYPE_HEAD: {
                StartObject start = (StartObject) object;
                ((StartHolder)holder).startText.setText(start.getText());
                if(!holder.itemView.hasOnClickListeners()) {
                    holder.itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(v.getContext(), start.getaClass());
                        v.getContext().startActivity(intent);
                    });
                }
            }
                break;
            case TYPE_CHAT: {
                ChatObject chat = (ChatObject) object;
                ((ChatHolder)holder).chatName.setText(chat.getName());
                ((ChatHolder)holder).chatText.setText(chat.getText());
                ((ChatHolder)holder).chatTime.setText(chat.getTime());
                if(chat.getIcon() != null) {
                    ((ChatHolder)holder).chatImage.setImageBitmap(chat.getIcon());
                } else {
                    ((ChatHolder) holder).chatImage.setImageResource(R.drawable.vector_drawable_default);
                }
                if(!holder.itemView.hasOnClickListeners()) {
                    holder.itemView.setOnClickListener(v -> {
                        ChatActivity.actionStart(v.getContext(), chat.getFriend_id(), chat.getName());
                    });
                }
            }
                break;
            case TYPE_USER: {
                UserObject friend = (UserObject) object;
                ((FriendHolder)holder).friendName.setText(friend.getName());
                ((FriendHolder)holder).friendSign.setText(friend.getSign());
                if(friend.getIcon() != null) {
                    ((FriendHolder)holder).friendImage.setImageBitmap(friend.getIcon());
                } else {
                    ((FriendHolder) holder).friendImage.setImageResource(R.drawable.vector_drawable_default);
                }
                if(!holder.itemView.hasOnClickListeners()) {
                    holder.itemView.setOnClickListener(v -> {
                        //guanzhu.actionStart(v.getContext(), friend.getId());
                    });
                }
            }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mChatList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch(viewType) {
            case TYPE_HEAD: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_start, parent, false);
                holder = new StartHolder(view);
            }
                break;
            case TYPE_CHAT: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_piece, parent, false);
                holder = new ChatHolder(view);
            }
                break;
            case TYPE_USER: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friend_piece, parent, false);
                holder = new FriendHolder(view);
            }
                break;
        }
        return holder;
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }
}
