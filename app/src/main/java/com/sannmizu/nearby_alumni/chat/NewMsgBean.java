package com.sannmizu.nearby_alumni.chat;

import com.google.gson.Gson;

public class NewMsgBean {
    private String time;
    private String content;

    public static NewMsgBean getInstance(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, NewMsgBean.class);
    }
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public NewMsgBean() {
    }

    public NewMsgBean(String time, String content) {
        this.time = time;
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
