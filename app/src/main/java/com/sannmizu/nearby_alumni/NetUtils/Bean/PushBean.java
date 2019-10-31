package com.sannmizu.nearby_alumni.NetUtils.Bean;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.List;

public class PushBean {
    private PostBean post;

    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public PushBean() {};

    public PostBean getPost() {
        return post;
    }

    public void setPost(PostBean post) {
        this.post = post;
    }

    public static class PostBean {
        private String title;
        private String content;
        private String repost;
        private LocationBean location;
        private List<MentionBean> mentions;
        private LinkedHashMap<String, String> extra;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getRepost() {
            return repost;
        }

        public void setRepost(String repost) {
            this.repost = repost;
        }

        public LocationBean getLocation() {
            return location;
        }

        public void setLocation(LocationBean location) {
            this.location = location;
        }

        public List<MentionBean> getMentions() {
            return mentions;
        }

        public void setMentions(List<MentionBean> mentionBeans) {
            this.mentions = mentionBeans;
        }

        public LinkedHashMap<String, String> getExtra() {
            return extra;
        }

        public void setExtra(LinkedHashMap<String, String> extra) {
            this.extra = extra;
        }
    }
    public static class LocationBean {
        private int areaId;
        private String latitude;
        private String longitude;

        public LocationBean() {}

        public LocationBean(int areaId, String latitude, String longitude) {
            this.areaId = areaId;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public int getAreaId() {
            return areaId;
        }

        public void setAreaId(int areaId) {
            this.areaId = areaId;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
    }
    public static class MentionBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
