package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.annotations.SerializedName;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class ChatResponse extends MyResponse<ChatResponse.ChatData> {
    public static interface ChatService {
        @FormUrlEncoded
        @POST("chat/user/{userId}")
        Observable<ChatResponse> chat(@Path("userId")String userId, @Field("value")String value, @Query("logToken")String logToken, @Query("connToken")String connToken);
    }

    public static class ChatData{
        @SerializedName("state")
        private String state;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}
