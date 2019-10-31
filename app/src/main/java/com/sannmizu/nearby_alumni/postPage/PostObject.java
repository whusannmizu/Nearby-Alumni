package com.sannmizu.nearby_alumni.postPage;

import android.graphics.Bitmap;

import com.sannmizu.nearby_alumni.MiPush.Bean.MediaBean;
import com.sannmizu.nearby_alumni.NetUtils.Post;

import java.util.ArrayList;
import java.util.List;

public class PostObject {
    private int postId;
    private int userId;
    private String userName;
    private String postTime;
    private String postText;
    private List<String> mediasPath = new ArrayList<>();
    private Bitmap avatar;
    private boolean hasAvatar;

    public PostObject() {
        hasAvatar = true;
    }

    public PostObject(Post post) {
        super();
        postId = post.getId();
        userId = post.getInfo().getAuthor_id();
        userName = post.getInfo().getAuthor_name();
        postTime = post.getTime();
        postText = post.getInfo().getContent();
        if(post.getInfo().getMedia_json() != null) {
            MediaBean mediaBean = MediaBean.toBean(post.getInfo().getMedia_json());
            if(mediaBean != null) {
                for (String part : mediaBean.getFiles()) {
                    mediasPath.add(mediaBean.getRoot() + part);
                }
            }
        }
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public boolean isHasAvatar() {
        return hasAvatar;
    }

    public void setHasAvatar(boolean hasAvatar) {
        this.hasAvatar = hasAvatar;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public List<String> getMediasPath() {
        return mediasPath;
    }

    public void setMediasPath(List<String> mediasPath) {
        this.mediasPath = mediasPath;
    }
}
