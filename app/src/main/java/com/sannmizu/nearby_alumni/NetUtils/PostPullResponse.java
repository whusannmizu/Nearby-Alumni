package com.sannmizu.nearby_alumni.NetUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class PostPullResponse extends MyResponse<PostPullResponse.PostData> {
    public static interface PostPullService {
        @GET("post/")
        Observable<PostPullResponse> pull(@Query("logToken")String logToken, @Query("type")String type, @Query("line")Long unix_timestamp, @Query("limit")int limit, @Query("group")String group);

        @GET("post/?type=surround")
        Observable<PostPullResponse> pullByLoc(@Query("logToken")String logToken, @Query("line")Long unix_timestamp, @Query("limit")int limit, @Query("latitude")String latitude, @Query("longitude")String longitude);

        @GET("post/")
        Observable<PostPullResponse> pull(@Query("logToken")String logToken, @Query("type")String type, @Query("line")Long unix_timestamp, @Query("limit")int limit);

        @GET("post/blog/{postId}")
        Observable<MyResponse<APost>> searchById(@Path("postId")String postId);
    }
    public static PostPullResponse.PostPullService generateService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Net.BaseHost)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(PostPullResponse.PostPullService.class);
    }
    public static class PostData {
        @SerializedName("posts")
        private List<APost> posts;

        public List<APost> getPosts() {
            return posts;
        }

        public void setPosts(List<APost> posts) {
            this.posts = posts;
        }
    }

    public static class APost {
        @SerializedName("post")
        private Post post;

        public Post getPost() {
            return post;
        }

        public void setPost(Post post) {
            this.post = post;
        }
    }
}
