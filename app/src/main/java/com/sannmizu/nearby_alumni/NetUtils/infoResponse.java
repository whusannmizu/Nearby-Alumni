package com.sannmizu.nearby_alumni.NetUtils;

import android.database.Observable;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class infoResponse extends MyResponse<User>{
    public static interface infoService{
        @GET("account/user/{userid}/all")
        Call<infoResponse> info(@Path("userid")int userid);
    }
    public static class infoData{
        @SerializedName("user")
        private User user;

        public void setUser(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }
}
