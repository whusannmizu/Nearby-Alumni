package com.sannmizu.nearby_alumni.MiPush.Bean;

public class ChatBean extends InfoBean<ChatBean.ChatData>{

    public static class ChatData {
        private int fromId;
        private int toId;
        private String name;
        private String time;
        private String content;
        private MediaBean media;

        public int getFromId() {
            return fromId;
        }

        public void setFromId(int fromId) {
            this.fromId = fromId;
        }

        public int getToId() {
            return toId;
        }

        public void setToId(int toId) {
            this.toId = toId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public MediaBean getMedia() {
            return media;
        }

        public void setMedia(MediaBean media) {
            this.media = media;
        }
    }
}
