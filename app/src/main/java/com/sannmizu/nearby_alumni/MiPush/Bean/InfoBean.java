package com.sannmizu.nearby_alumni.MiPush.Bean;

public class InfoBean<T> {
    private String aim;
    private T data;

    public String getAim() {
        return aim;
    }

    public void setAim(String aim) {
        this.aim = aim;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
