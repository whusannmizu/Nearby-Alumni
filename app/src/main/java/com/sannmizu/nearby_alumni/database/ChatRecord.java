package com.sannmizu.nearby_alumni.database;

import android.util.Log;

import com.sannmizu.nearby_alumni.MiPush.Bean.ChatBean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class ChatRecord extends LitePalSupport {
    private int id;
    @Column(nullable = false)
    private int user_id;        //本地用户id
    @Column(nullable = false)
    private int friend_id;      //与本地用户聊天的好友id
    @Column(nullable = false)
    private String content;     //聊天内容，如果有图片则是图片url
    @Column(nullable = false)
    private boolean isText;     //true:文本, false:图片url
    @Column(nullable = false)
    private Date time;          //聊天时间
    @Column(nullable = false)
    private int subject;        //主语，这句话是谁说的，0自己，1对方

    public ChatRecord(ChatBean.ChatData chatBean) {
        user_id = chatBean.getToId();
        friend_id = chatBean.getFromId();
        if(chatBean.getMedia().getRoot() != null) {
            content = chatBean.getMedia().getRoot() + chatBean.getMedia().getFiles().get(0);
            isText = false;
        } else {
            content = chatBean.getContent();
            isText = true;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            time = sdf.parse(chatBean.getTime());
        } catch (ParseException e) {
            Log.e("sannmizu.date", "ChatBean2ChatRecord:" + chatBean.getTime() + "|error in" + e.getErrorOffset());
        }
        subject = 1;
    }

    public ChatRecord() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public boolean isText() {
        return isText;
    }

    public void setText(boolean text) {
        isText = text;
    }

    public int getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(int friend_id) {
        this.friend_id = friend_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getSubject() {
        return subject;
    }

    public void setSubject(int subject) {
        this.subject = subject;
    }
}
