package com.sannmizu.nearby_alumni.NetUtils;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.sannmizu.nearby_alumni.R;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class UserSearchResponse extends MyResponse<UserSearchResponse.UserSearchData>{
    public static interface UserSearchService {
        @GET("account/user/search")
        Observable<UserSearchResponse> search(@Query("type")String type, @Query("standard")String standard, @Query("limit")int limit);

        @GET("account/user/search")
        Observable<UserSearchResponse> search(@Query("type")String type, @Query("standard")String standard);

        @GET("account/user/{userId}/all")
        Observable<MyResponse<User>> searchById(@Path("userId")String userId);
    }
    public static UserSearchService generateService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Net.BaseHost)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(UserSearchService.class);
    }
    public static class UserSearchData {
        @SerializedName("users")
        private List<AUser> users;

        public List<AUser> getUsers() {
            return users;
        }

        public void setUsers(List<AUser> users) {
            this.users = users;
        }
    }
    public static class AUser {
        @SerializedName("user")
        private User user;
        @SerializedName("distance")
        private double distance;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }
    }
}
