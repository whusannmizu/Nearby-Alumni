package com.sannmizu.nearby_alumni.NetUtils;

public interface MyCallback {
    public void onSuccess();
    public void onFailure(String reason);
    public void onError(Throwable t);
}
