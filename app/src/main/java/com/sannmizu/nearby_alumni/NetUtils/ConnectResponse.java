package com.sannmizu.nearby_alumni.NetUtils;


import com.google.gson.annotations.SerializedName;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;


public class ConnectResponse extends MyResponse<ConnectResponse.ConnectData>{
    public static interface ConnectService {
        @FormUrlEncoded
        @POST("app/connect")
        Call<ConnectResponse> connect(@Query("logToken")String logToken, @Field("value")String value);
    }

    public static class ConnectData {
        @SerializedName("connToken")
        private Token token;

        @SerializedName("aes")
        private Aes aes;

        public static class Token {
            @SerializedName("expiretime")
            private String expire_time;
            @SerializedName("value")
            private String value;

            public String getExpire_time() {
                return expire_time;
            }

            public void setExpire_time(String expire_time) {
                this.expire_time = expire_time;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class Aes {
            @SerializedName("key")
            private String key;
            @SerializedName("iv")
            private String iv;
            @SerializedName("mode")
            private String mode;
            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public String getIv() {
                return iv;
            }

            public void setIv(String iv) {
                this.iv = iv;
            }

            public String getMode() {
                return mode;
            }

            public void setMode(String mode) {
                this.mode = mode;
            }
        }

        public Token getToken() {
            return token;
        }

        public void setToken(Token token) {
            this.token = token;
        }

        public Aes getAes() {
            return aes;
        }

        public void setAes(Aes aes) {
            this.aes = aes;
        }
    }
}
