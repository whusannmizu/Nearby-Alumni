package com.sannmizu.nearby_alumni.NetUtils;

import android.database.Observable;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public class infoResponse extends MyResponse{
    public static interface infoService{
        @PUT("account/info")
        Observable<infoResponse> info(@Query("newInfo") String newInfo, @Query("logToken")String logToken, @Query("connToken")String connToken);
    }
    public static class infoData{
        @SerializedName("NewInfo")
        private NewInfo newInfo;
        public static class NewInfo {
            @SerializedName("name")
            private String name;
            @SerializedName("age")
            private String age;
            @SerializedName("sign")
            private String sign;
            @SerializedName("sex")
            private String sex;
            @SerializedName("constellation")
            private String constellation;
            @SerializedName("career")
            private String career;
            @SerializedName("areaId")
            private String areaId;
            @SerializedName("email")
            private String email;
            @SerializedName("icon")
            private String icon;

            public void setName(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getSex() {
                return sex;
            }

            public void setSign(String sign) {
                this.sign = sign;
            }

            public String getSign() {
                return sign;
            }

            public void setAge(String age) {
                this.age = age;
            }

            public String getAge() {
                return age;
            }

            public void setAreaId(String areaId) {
                this.areaId = areaId;
            }

            public String getAreaId() {
                return areaId;
            }

            public void setCareer(String career) {
                this.career = career;
            }

            public String getCareer() {
                return career;
            }

            public void setConstellation(String constellation) {
                this.constellation = constellation;
            }

            public String getConstellation() {
                return constellation;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public String getEmail() {
                return email;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getIcon() {
                return icon;
            }
        }
        @SerializedName("logToken")
        private String logToken;
        @SerializedName("connToken")
        private ConnectResponse.ConnectData.Token token;

        public void setLogToken(String logToken) {
            this.logToken = logToken;
        }

        public String getLogToken() {
            return logToken;
        }

        public void setNewInfo(NewInfo newInfo) {
            this.newInfo = newInfo;
        }

        public NewInfo getNewInfo() {
            return newInfo;
        }

        public void setToken(ConnectResponse.ConnectData.Token token) {
            this.token = token;
        }

        public ConnectResponse.ConnectData.Token getToken() {
            return token;
        }
    }
}
