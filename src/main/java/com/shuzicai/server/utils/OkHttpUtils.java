package com.shuzicai.server.utils;


import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Nicky on 2017/2/21.
 */
public class OkHttpUtils {
    //日之类
    private static Logger logger = Logger.getLogger(OkHttpUtils.class);
    //OkHttpClient对象
    private static OkHttpClient okHttpClient = null;
    //超时5秒
    private static final int DEFAULT_TIMEOUT = 5;

    /**
     * 初始化
     */
    public static void init() {

    }

    private void get(String url) {
        if (null == okHttpClient) {
            init();
        }
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {

            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("我是异步线程,线程Id为:" + Thread.currentThread().getId());
            }
        });
    }

    /**
     * 获取okhttp对象,在这里进行相关的配置
     *
     * @return OkHttpClient
     */
    private static OkHttpClient getOkHttpClient() {
        //新建header
        Interceptor headInterceptor = new Interceptor() {

            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("X-Bmob-Email", "m13539420022@163.com")
                        .addHeader("X-Bmob-Password", "1234554321")
                        .addHeader("X-Bmob-Application-Id", "cd89b563ca70dfd60befb89fa9ad6e42")
                        .addHeader("X-Bmob-REST-API-Key", "f21c0ff7f6aa0405e9f97c30fc9a414f")
                        .build();
                //设置Content-Type
//                request.newBuilder().post(new RequestBody() {
//
//                    @Override
//                    public MediaType contentType() {
//                        return MediaType.parse("image/jpeg");
//                    }
//
//                    @Override
//                    public void writeTo(BufferedSink sink) throws IOException {
//                    }
//                });
                return chain.proceed(request);
            }
        };

        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        //添加header
        httpClientBuilder.addInterceptor(headInterceptor);

        return httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }
}
