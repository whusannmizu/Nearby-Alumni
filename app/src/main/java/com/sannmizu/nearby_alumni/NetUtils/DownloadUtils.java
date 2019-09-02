package com.sannmizu.nearby_alumni.NetUtils;

import java.net.URL;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public class DownloadUtils {
    public interface FileService {
        @Streaming
        @GET
        Observable<ResponseBody> download(@Url String url);
    }
    public static Observable<ResponseBody> fromUrl(String urlString) {
        if(!urlString.matches("http://(.*)") && !urlString.matches("https://(.*)")) {
            urlString = "http://" + urlString;
        }
        try {
            URL url = new URL(urlString);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url.getProtocol() + "://" + url.getHost())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            return retrofit.create(FileService.class).download(url.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            return Observable.error(e);
        }
    }
}
