package com.sannmizu.nearby_alumni.chat;

public class ChatObject extends BaseObject{
    private int friend_id;
    private String name;
    private String text;
    private String icon;

    public ChatObject(int id, String name, String icon, String text) {
        super(UserListAdapter.TYPE_CHAT);
        this.friend_id = id;
        this.name = name;
        this.text = text;
        this.icon = icon;
    }

    public ChatObject(int id, String name, String icon) {
        this(id, name, icon, null);
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(int friend_id) {
        this.friend_id = friend_id;
    }
}
