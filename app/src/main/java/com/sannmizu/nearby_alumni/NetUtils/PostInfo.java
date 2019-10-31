package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.annotations.SerializedName;

public class PostInfo {
    @SerializedName("authorId")
    private int author_id;          //作者id
    @SerializedName("authorName")
    private String author_name;     //作者名字
    @SerializedName("title")
    private String title;           //标题
    @SerializedName("content")
    private String content;         //正文
    @SerializedName("media")
    private String media_json;      //媒体文件位置
    @SerializedName("numlikes")
    private int num_likes;          //点赞数
    @SerializedName("numreposts")
    private int num_reposts;        //转发数
    @SerializedName("areaId")
    private int area_id;            //位置id

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

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

    public String getMedia_json() {
        return media_json;
    }

    public void setMedia_json(String media_json) {
        this.media_json = media_json;
    }

    public int getNum_likes() {
        return num_likes;
    }

    public void setNum_likes(int num_likes) {
        this.num_likes = num_likes;
    }

    public int getNum_reposts() {
        return num_reposts;
    }

    public void setNum_reposts(int num_reposts) {
        this.num_reposts = num_reposts;
    }

    public int getArea_id() {
        return area_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }
}
