package com.sannmizu.nearby_alumni.NetUtils;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.sannmizu.nearby_alumni.utils.AccountUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public class LocateResponse extends MyResponse<LocateResponse.LocateData>{
    public static interface LocateService{
        @PUT("app/locate")
        Call<MyResponse> locate(@Query("latitude")String latitude, @Query("longitude")String longitude, @Query("logToken")String logToken);
    }
    public static LocateService generateService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Net.BaseHost)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(LocateService.class);
    }
    public static void update(String latitude, String longitude) {
        generateService().locate(latitude, longitude, AccountUtils.getLogToken()).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if(response.body().getCode() == 0) {
                    Log.i("sannmizu.locate", "上传位置成功");
                } else {
                    Log.i("sannmizu.locate", "上传位置失败：" + response.body().getReason());
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                Log.i("sannmizu.locate", "上传位置失败：" + t.getMessage());
            }
        });
    }
    public static class LocateData{
        @SerializedName("logToken")
        private String logToken;
        @SerializedName("latitude")
        private String latitude;
        @SerializedName("longitude")
        private String longitude;

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLogToken(String logToken) {
            this.logToken = logToken;
        }

        public String getLogToken() {
            return logToken;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLongitude() {
            return longitude;
        }
    }
}
