package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.JsonObject;
import com.sannmizu.nearby_alumni.utils.MD5Utils;
import com.sannmizu.nearby_alumni.utils.RSAUtils;

import java.util.Date;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.PUT;

public class ForgetResponse extends MyResponse{
    public static String RequestStr(String type, String account, String password) {
        String timestamp = Long.toString(new Date().getTime() / 1000);

        JsonObject requestRoot = new JsonObject();
        requestRoot.addProperty("type", type);
        requestRoot.addProperty("timestamp", timestamp);
        JsonObject requestData = new JsonObject();
        requestData.addProperty("account", account);
        requestData.addProperty("pwd", password);
        requestData.addProperty("sign", MD5Utils.md5(timestamp+account+password));
        requestRoot.add("data", requestData);

        return RSAUtils.encrypt(requestRoot.toString());
    }
    public static interface ForgetService{
        @FormUrlEncoded
        @PUT("account/new")
        Call<ForgetResponse>forget(@Field("value")String value);
    }
}
