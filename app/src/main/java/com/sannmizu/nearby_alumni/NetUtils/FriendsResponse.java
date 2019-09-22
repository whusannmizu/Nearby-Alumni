package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.annotations.SerializedName;
import com.sannmizu.nearby_alumni.utils.Utils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class FriendsResponse extends MyResponse<FriendsResponse.FriendsData> {
    public static interface FriendsService {
        @GET("account/friends/")
        Observable<FriendsResponse> getFriendList(@Query("logToken")String logToken);

        @POST("account/friends/{userId}")
        Observable<MyResponse> addFriend(@Query("logToken")String logToken, @Path("userId")int id);

        @DELETE("account/friends/{userId}")
        Observable<MyResponse> deleteFriend(@Query("logToken")String logToken, @Path("userId")int id);
    }
    public static FriendsService generateService() {
        Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Net.BaseHost)
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
    public static Disposable addFriend(int id, MyCallback callback) {
        return generateService().addFriend(Utils.getLogToken(), id)
                .subscribeOn(Schedulers.io())
                .subscribe(response->{
                    if(response.getCode() == 0) {
                        //成功，回调成功
                        callback.onSuccess();
                    } else {
                        //失败，回调失败
                        callback.onFailure(response.getReason());
                    }
                }, callback::onError );
    }
    public static Disposable deleteFriend(int id, MyCallback callback) {
        return generateService().deleteFriend(Utils.getLogToken(), id)
                .subscribeOn(Schedulers.io())
                .subscribe(response->{
                    if(response.getCode() == 0) {
                        //成功，回调成功
                        callback.onSuccess();
                    } else {
                        //失败，回调失败
                        callback.onFailure(response.getReason());
                    }
                }, callback::onError );
    }
}
