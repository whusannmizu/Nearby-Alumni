package com.sannmizu.nearby_alumni.denglu;

public interface Locateback {
    public void onReceiveLocation(String latitude, String longitude);
    public void onFailure();
}
