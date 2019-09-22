package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class RequestsResponse extends MyResponse<RequestsResponse.RequestList> {
    public static interface RequestsService {
        @GET("account/requests/")
        Observable<RequestsResponse> getRequestList(@Query("logToken")String logToken);
    }
    public static RequestsResponse.RequestsService generateService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Net.BaseHost)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(RequestsResponse.RequestsService.class);
    }
    public static class RequestList {
        @SerializedName("requestlist")
        private List<User> requests;

        public List<User> getRequests() {
            return requests;
        }

        public void setRequests(List<User> requests) {
            this.requests = requests;
        }
    }
}
