package com.sannmizu.nearby_alumni.chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sannmizu.nearby_alumni.database.Users;
import com.sannmizu.nearby_alumni.utils.BitmapUtils;

public class ChatObject extends BaseObject{
    public static final int TYPE = UserListAdapter.TYPE_CHAT;
    private int friend_id;
    private String name;
    private String text;
    private String time;
    private Bitmap icon;

    public ChatObject(int id, String name, Bitmap icon, String text, String time) {
        super(TYPE);
        this.friend_id = id;
        this.name = name;
        this.text = text;
        this.icon = icon;
        this.time = time;
    }

    public ChatObject(int id, String name, Bitmap icon) {
        this(id, name, icon, null, null);
    }

    public ChatObject(Users user, NewMsgBean bean) {
        super(TYPE);
        this.friend_id = user.getUser_id();
        this.name = user.getNickname();
        this.text = bean.getContent();
        this.time = bean.getTime();
        if (user.getIcon() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(user.getIcon(), 0, user.getIcon().length);
            this.icon = BitmapUtils.zoomBitmap(bitmap, 100, 100);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public int getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(int friend_id) {
        this.friend_id = friend_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
