package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.annotations.SerializedName;
import com.sannmizu.nearby_alumni.utils.AccountUtils;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public class LocateResponse extends MyResponse<LocateResponse.LocateData>{
    public static interface LocateService{
        @PUT("app/Locate")
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    generateService().locate(AccountUtils.getLogToken(), latitude, longitude).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
