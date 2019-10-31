package com.sannmizu.nearby_alumni.MiPush.Bean;

import com.google.gson.Gson;

import java.util.List;

public class MediaBean {
    private String root;
    private List<String> files;

    public static MediaBean toBean(String s) {
        Gson gson = new Gson();
        return gson.fromJson(s, MediaBean.class);
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
