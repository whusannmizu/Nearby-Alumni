package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("userId")
    private int id;          //用户id
    @SerializedName("info")
    private UserInfo info;          //信息

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserInfo getInfo() {
        return info;
    }

    public void setInfo(UserInfo info) {
        this.info = info;
    }
}
