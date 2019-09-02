package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class starsResponse extends MyResponse<starsResponse.starsData>{
    private static interface starsService{
        @GET("post/stars/")
        Call<starsResponse>stars(@Query("logToken")String logToken);
    }
    public static class starsData{
        @SerializedName("posts")
        private Posts posts;
        public static class Posts{
            @SerializedName("post")
            private Post post;
            public static class Post{
                @SerializedName("postId")
                private String postId;

                public void setPostId(String postId) {
                    this.postId = postId;
                }

                public String getPostId() {
                    return postId;
                }
            }

            public void setPost(Post post) {
                this.post = post;
            }

            public Post getPost() {
                return post;
            }
        }

        public void setPosts(Posts posts) {
            this.posts = posts;
        }

        public Posts getPosts() {
            return posts;
        }
    }
}
