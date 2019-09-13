package com.sannmizu.nearby_alumni.NetUtils;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class MyResponse<T> {
    private String result;
    private String reason;
    private String description;
    private int code;
    private T data;
    public static interface BasePostService{
        @FormUrlEncoded
        @POST("{host}")
        Call<ConnectResponse> post(@Path("host")String path);
    }
    public static interface infoService{
        @PUT("account/info")
        Call<MyResponse> info(@Query("newInfo") String newInfo, @Query("logToken")String logToken, @Query("connToken")String connToken);
    }
    public static interface locateService{
        @PUT("app/locate")
        Call<locateResponse>locate(@Query("latitude")String latitude,@Query("longitude")String longitude,@Query("logToken")String logToken);
    }
    public static interface addService {
        @POST("account/friends/{id}")
        Call<MyResponse> add(@Path("id") int userid, @Query("logToken") String logToken);
    }
    public static interface deleteService{
        @DELETE("account/friends/{id}")
        Call<MyResponse>delete(@Path("userId")int userId,@Field("logToken")String logToken);
    }
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
