package com.sannmizu.nearby_alumni.chat;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sannmizu.nearby_alumni.database.Users;

public class UserObject extends BaseObject{
    public static final int TYPE = UserListAdapter.TYPE_USER;
    private int id = 0;
    private String name = null;
    private Bitmap icon = null;
    private String sign = null;

    public UserObject(String name, Bitmap icon, int id, String sign) {
        super(TYPE);
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.sign = sign;
    }

    public UserObject(String name, Bitmap icon) {
        this(name, icon, 0, null);
    }

    public UserObject(Users user) {
        super(TYPE);
        this.id = user.getUser_id();
        this.name = user.getNickname();
        this.sign = user.getSign();
        if (user.getIcon() != null) {
            this.icon = BitmapFactory.decodeByteArray(user.getIcon(), 0, user.getIcon().length);
        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
