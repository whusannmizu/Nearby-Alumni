package com.sannmizu.nearby_alumni.MiPush.Bean;

public class FriendReBean extends InfoBean<FriendReBean.RequestData> {
    public static class RequestData {
        private int fromId;
        private int toId;
        private String name;

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
    }
}
