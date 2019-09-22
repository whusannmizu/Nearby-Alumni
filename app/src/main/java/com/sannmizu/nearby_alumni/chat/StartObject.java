package com.sannmizu.nearby_alumni.chat;

public class StartObject extends BaseObject {
    public static final int TYPE = UserListAdapter.TYPE_HEAD;
    private String text;
    private Class aClass;
    public StartObject(String text, Class aClass) {
        super(TYPE);
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
