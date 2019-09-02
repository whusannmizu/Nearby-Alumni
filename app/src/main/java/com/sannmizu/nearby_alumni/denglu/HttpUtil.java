package com.sannmizu.nearby_alumni.denglu;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    //使用Get方式获得服务器上数据
    public static void sendOkHttpRequest(final String address, final okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
    //使用Post方式向服务器上提交数据并获取返回数据
    public static void sendOkHttpResponse(final String address, final RequestBody requestBody,final okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
}

