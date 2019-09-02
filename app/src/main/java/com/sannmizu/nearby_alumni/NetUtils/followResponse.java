package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class followResponse extends MyResponse<followResponse.followData> {
    public static interface followService{
        @GET("account/follows/")
        Call<followResponse>follow(@Query("logToken")String logToken);
    }
    public static class followData{
        @SerializedName("followlist")
        private  Followlist followlist;

        public static class Followlist{
            @SerializedName("relation")
            private String relation;
            @SerializedName("user")
            private User user;

            public void setRelation(String relation) {
                this.relation = relation;
            }

            public String getRelation() {
                return relation;
            }

            public void setUser(User user) {
                this.user = user;
            }

            public User getUser() {
                return user;
            }
        }
        public static class User{
            @SerializedName("userId")
            private String useId;
            @SerializedName("info")
            private Info info;

            public void setUseId(String useId) {
                this.useId = useId;
            }

            public String getUseId() {
                return useId;
            }

            public void setInfo(Info info) {
                this.info = info;
            }

            public Info getInfo() {
                return info;
            }
        }
        public static class Info{
            @SerializedName("name")
            private String name;
            @SerializedName("sign")
            private String sign;
            @SerializedName("sex")
            private String sex;

            public void setName(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

            public void setSign(String sign) {
                this.sign = sign;
            }

            public String getSign() {
                return sign;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public String getSex() {
                return sex;
            }
        }

        public void setFollowlist(Followlist followlist) {
            this.followlist = followlist;
        }

        public Followlist getFollowlist() {
            return followlist;
        }
    }
}
