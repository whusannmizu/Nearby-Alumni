package com.sannmizu.nearby_alumni.NetUtils;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.sannmizu.nearby_alumni.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class FriendsResponse extends MyResponse<FriendsResponse.FriendsData> {
    public static interface FriendsService {
        @GET("account/friends/")
        Observable<FriendsResponse> getFriendList(@Query("logToken")String logToken);
    }
    public static FriendsService generateService(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(context.getString(R.string.ServerBaseUrl))
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();
        return retrofit.create(FriendsService.class);
    }
    public static class FriendsData {
        @SerializedName("friendlist")
        private List<User> friends;

        public List<User> getFriends() {
            return friends;
        }

        public void setFriends(List<User> friends) {
            this.friends = friends;
        }
    }
}
