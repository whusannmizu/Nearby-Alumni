package com.sannmizu.nearby_alumni.NetUtils;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.sannmizu.nearby_alumni.R;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class ChatResponse extends MyResponse<ChatResponse.ChatData> {
    public static interface ChatService {
        @FormUrlEncoded
        @POST("chat/user/{userId}")
        Observable<ChatResponse> chat(@Path("userId")int userId, @Field("value")String value, @Query("logToken")String logToken, @Query("connToken")String connToken);
    }
    public static ChatService generateService(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.ServerBaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(ChatService.class);
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
