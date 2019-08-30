package com.sannmizu.nearby_alumni.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.sannmizu.nearby_alumni.R;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class JsonConverterFactory extends Converter.Factory {
    private static final String TAG = "JsonConverterFactory";
    private final Context context;
    private final Gson gson;

    public static JsonConverterFactory create(Context context) {
        return create(new Gson(), context);
    }

    public static JsonConverterFactory create(Gson gson, Context context) {
        return new JsonConverterFactory(gson, context);
    }

    private JsonConverterFactory(Gson gson, Context context) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
        this.context = context;
    }
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new JsonResponseBodyConverter<>(gson, context, adapter); //响应
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new JsonRequestBodyConverter<>(gson, context, adapter); //请求
    }

    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return super.stringConverter(type, annotations, retrofit);
    }

    /**
     * JsonRequestBodyConverter<T>
     * @param <T>
     */
    public static class JsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
        private final Gson gson;
        private final Context context;
        private final TypeAdapter<T> adapter;

        /**
         * 构造器
         */
        public JsonRequestBodyConverter(Gson gson, Context context, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.context = context;
            this.adapter = adapter;
        }

        @Override
        public RequestBody convert(T value) throws IOException {

            //这里需要，特别注意的是，request是将T转换成json数据。
            //你要在T转换成json之后再做加密。
            //再将数据post给服务器，同时要注意，你的T到底指的那个对象

            //加密操作，返回字节数组
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String key = sharedPreferences.getString(context.getString(R.string.connect_aes_key), "1234567890000000");
            String iv = sharedPreferences.getString(context.getString(R.string.connect_aes_iv), "1234567890000000");
            String encrypt = AESUtils.encrypt(value.toString(), key, iv);

            Log.i("sannmizu.JsonConverter","使用的密钥是" + key + "|" + iv);

            //传入字节数组，创建RequestBody 对象
            return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),encrypt.getBytes());
        }
    }

    /**
     * JsonResponseBodyConverter<T>
     * @param <T>
     */
    public class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson mGson;//gson对象
        private final Context context;
        private final TypeAdapter<T> adapter;

        /**
         * 构造器
         */
        public JsonResponseBodyConverter(Gson gson, Context context, TypeAdapter<T> adapter) {
            this.mGson = gson;
            this.context = context;
            this.adapter = adapter;
        }

        /**
         * 转换
         *
         * @param responseBody
         * @return
         * @throws IOException
         */
        @Override
        public T convert(ResponseBody responseBody) throws IOException {

            String string = responseBody.string();

            //对字节数组进行解密操作
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String key = sharedPreferences.getString(context.getString(R.string.connect_aes_key), "1234567890000000");
            String iv = sharedPreferences.getString(context.getString(R.string.connect_aes_iv), "1234567890000000");
            String decryptString = AESUtils.decrypt(string, key, iv);

            //对解密的字符串进行处理
            int position = decryptString.lastIndexOf("}");
            String jsonString = decryptString.substring(0,position+1);

            Log.i("sannmizu.JsonConverter","使用的密钥是" + key + "|" + iv);

            //这部分代码参考GsonConverterFactory中GsonResponseBodyConverter<T>的源码对json的处理
            Reader reader = StringToReader(jsonString);
            JsonReader jsonReader = gson.newJsonReader(reader);
            try {
                return adapter.read(jsonReader);
            } finally {
                reader.close();
                jsonReader.close();
            }
        }

        /**
         * String转Reader
         * @param json
         * @return
         */
        private Reader StringToReader(String json){
            Reader reader  = new StringReader(json);
            return reader;
        }
    }
}
