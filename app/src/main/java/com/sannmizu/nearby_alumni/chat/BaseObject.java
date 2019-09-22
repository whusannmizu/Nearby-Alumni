package com.sannmizu.nearby_alumni.chat;

public class BaseObject {
    protected static Long TAG = 0L;
    private int type;
    private Long tag;

    public BaseObject(int type) {
        this.type = type;
        this.tag = createTAG();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getTAG() {
        return tag;
    }

    public void setTAG(Long TAG) {
        this.tag = TAG;
    }

    public Long createTAG() {
        return TAG++;
    }
}
