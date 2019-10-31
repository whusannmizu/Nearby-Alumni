package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("postId")
    private int id;             //用户id
    @SerializedName("time")
    private String time;        //时间
    @SerializedName("repost")
    private int repost_id;      //转发
    @SerializedName("info")
    private PostInfo info;      //信息

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getRepost_id() {
        return repost_id;
    }

    public void setRepost_id(int repost_id) {
        this.repost_id = repost_id;
    }

    public PostInfo getInfo() {
        return info;
    }

    public void setInfo(PostInfo info) {
        this.info = info;
    }
}
