package com.sannmizu.nearby_alumni.NetUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.sannmizu.nearby_alumni.NetUtils.Bean.PushBean;
import com.sannmizu.nearby_alumni.utils.AESUtils;
import com.sannmizu.nearby_alumni.utils.AccountUtils;
import com.sannmizu.nearby_alumni.utils.BitmapUtils;
import com.sannmizu.nearby_alumni.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class PostPushResponse extends MyResponse<PostPushResponse.PostPushReturnData>{
    public static interface PostPushService {
        @FormUrlEncoded
        @POST("post/blog/")
        Observable<PostPushResponse> push(@Query("logToken")String logToken, @Query("connToken")String connToken, @Field("value")String value);

        @FormUrlEncoded
        @POST("post/blog/")
        Observable<PostPushResponse> push(@Query("logToken")String logToken, @Query("connToken")String connToken, @Field("value")String value, @FieldMap Map<String, String> pics);
    }
    public static PostPushService generateService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Net.BaseHost)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(PostPushService.class);
    }
    public static Disposable sendPost(PushBean bean, MyCallback callback) {
        String s = bean.toString();
        String s2 = bean.toString();


        return generateService().push(AccountUtils.getLogToken(), AccountUtils.getConnToken(), AESUtils.encryptFromLocal(bean.toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<PostPushResponse>() {
                    @Override
                    public void accept(PostPushResponse postPushResponse) throws Exception {
                        if (postPushResponse.getCode() == 0) {
                            callback.onSuccess();
                        } else {
                            callback.onFailure(postPushResponse.getReason());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callback.onError(throwable);
                    }
                });
    }
    public static Disposable sendPost_Pics(PushBean bean, Map<String, String> picsPath, MyCallback callback) {
        String s = AESUtils.encryptFromLocal(bean.toString());

        Map<String, String> picsField = new HashMap<>();
        //path<“图片key”，“图片位置”>
        for(Map.Entry<String, String> path : picsPath.entrySet()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path.getValue());
            String picString = BitmapUtils.getPNGFromBitmapToBase64(bitmap);
            picsField.put(path.getKey(), picString);
        }

        String s2 = AESUtils.encryptFromLocal(bean.toString());
        return generateService().push(AccountUtils.getLogToken(), AccountUtils.getConnToken(), AESUtils.encryptFromLocal(bean.toString()), picsField)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<PostPushResponse>() {
                    @Override
                    public void accept(PostPushResponse postPushResponse) throws Exception {
                        if (postPushResponse.getCode() == 0) {
                            callback.onSuccess();
                        } else {
                            callback.onFailure(postPushResponse.getReason());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        callback.onError(throwable);
                    }
                });
    }
    public static class PostPushReturnData {
        @SerializedName("postId")
        private int post_id;
        @SerializedName("commentId")
        private int comment_id;
    }


}
