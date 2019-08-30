package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.sannmizu.nearby_alumni.utils.MD5Utils;
import com.sannmizu.nearby_alumni.utils.RSAUtils;

import java.util.Date;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class RegisterResponse extends MyResponse<RegisterResponse.RegisterData>{
    public static String getRequestStr(String type, String account, String password, String nickname) {
        String timestamp = Long.toString(new Date().getTime() / 1000);

        JsonObject requestRoot = new JsonObject();
        requestRoot.addProperty("type", type);
        requestRoot.addProperty("timestamp", timestamp);
        JsonObject requestData = new JsonObject();
        requestData.addProperty("account", account);
        requestData.addProperty("nickname", nickname);
        requestData.addProperty("pwd", password);
        requestData.addProperty("sign", MD5Utils.md5(timestamp+account+nickname+password));
        requestRoot.add("data", requestData);

        return RSAUtils.encrypt(requestRoot.toString());
    }

    public static interface RegisterService {
        @FormUrlEncoded
        @POST("account/new")
        Observable<RegisterResponse> register(@Field("value")String value);
    }

    public static class RegisterData {
        @SerializedName("userId")
        private int user_id;

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }
    }
}