package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class friendResponse extends MyResponse<friendResponse.FriendsData>{
    public static interface friendService{
        @GET("account/friends/")
        Call<friendResponse>friend(@Query("logToken")String logToken);
    }
    public static class friendData{
        @SerializedName("friendlist")
        private List<Friendlist>friendlist;
        public static class Friendlist{
            @SerializedName("userId")
            private String userId;
            @SerializedName("info")
            private Info1 info;
            public static class Info1{
                @SerializedName("name")
                private String name;
                @SerializedName("sign")
                private String sign;
                @SerializedName("icon")
                private String icon;

                public void setName(String name) {
                    this.name = name;
                }

                public String getName() {
                    return name;
                }

                public void setIcon(String icon) {
                    this.icon = icon;
                }
                public String getIcon() {
                    return icon;
                }

                public void setSign(String sign) {
                    this.sign = sign;
                }

                public String getSign() {
                    return sign;
                }
            }

            public void setInfo(Info1 info) {
                this.info = info;
            }

            public Info1 getInfo() {
                return info;
            }

            public void setUserId(String userId) {
                this.userId = userId;
            }

            public String getUserId() {
                return userId;
            }
        }

        public void setFriendlist(List<Friendlist> friendlist) {
            this.friendlist = friendlist;
        }

        public List<Friendlist> getFriendlist() {
            return friendlist;
        }
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
}
