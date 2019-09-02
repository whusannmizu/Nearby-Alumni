package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public class locateResponse extends MyResponse<locateResponse.locateData>{
    public static interface locateService{
        @PUT("app/locate")
        Call<locateResponse>locate(@Query("latitude")String latitude,@Query("longitude")String longitude,@Query("logToken")String logToken);
    }
    public static class locateData{
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
