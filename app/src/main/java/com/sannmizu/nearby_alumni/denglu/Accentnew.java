package com.sannmizu.nearby_alumni.denglu;

public class Accentnew {
    private String type;
    private String timestamp;
    private Data data;

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("\"type\":").append(type).append(",\n").append("\"timestamp\":").append(timestamp).append(",\n").append("\"data\":").append(data).append('\n');
        return sb.toString();
    }
}
