package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class RegisterResponse extends MyResponse<RegisterResponse.RegisterData>{
    public static interface RegisterService {
        @FormUrlEncoded
        @POST("account/new")
        Call<RegisterResponse> register(@Field("value")String value);
    }

    public static class RegisterData {
        @SerializedName("id")
        private String user_id;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
    }
}