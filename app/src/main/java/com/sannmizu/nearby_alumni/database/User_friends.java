package com.sannmizu.nearby_alumni.Database;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class User_friends extends LitePalSupport {
    private int id;
    @Column(nullable = false)
    private int user_id;
    @Column(nullable = false)
    private int friend_id;

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

    public int getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(int friend_id) {
        this.friend_id = friend_id;
    }
}
