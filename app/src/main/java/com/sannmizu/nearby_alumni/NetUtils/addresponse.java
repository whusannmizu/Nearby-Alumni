package com.sannmizu.nearby_alumni.NetUtils;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class addresponse extends MyResponse<addresponse.addData>{
    public static interface addService {
        @POST("account/friends/{id}")
        Call<addresponse> add(@Path("userId") int userId, @Field("logToken") String logToken);
    }
    public static class addData{

    }
}
