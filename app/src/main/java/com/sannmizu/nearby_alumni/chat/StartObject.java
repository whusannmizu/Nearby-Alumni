package com.sannmizu.nearby_alumni.chat;

public class StartObject extends  BaseObject {
    private String text;
    private Class aClass;
    public StartObject(String text, Class aClass) {
        super(UserListAdapter.TYPE_HEAD);
        this.text = text;
        this.aClass = aClass;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }
}
