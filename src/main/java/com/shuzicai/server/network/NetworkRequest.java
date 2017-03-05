package com.shuzicai.server.network;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * Created by nicky on 2016/12/29.
 * 网络请求
 */

public class NetworkRequest {
    //日之类
    private static Logger logger = Logger.getLogger(NetworkRequest.class);
    //BASE_URL
    private static final String TAG = "NetworkRequest";
    //超时5秒
    private static final int DEFAULT_TIMEOUT = 5;
    //
    private static Retrofit retrofit;

    //构造方法私有
    private NetworkRequest() {
    }

    /**
     * 创建 RetrofitManage 服务
     */
    public static <T> T getRetrofitClient(final Class<T> clss) {
        String baseURL = "https://api.bmob.cn/";
        retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())//使用自己创建的OkHttp
                .addConverterFactory(ScalarsConverterFactory.create()) //增加返回值为String的支持
                .addConverterFactory(GsonConverterFactory.create())//增加返回值为Gson的支持(以实体类返回)
                .build();

        return retrofit.create(clss);
    }

    /**
     * 通用的网络请求模块
     *
     * @param call     请求call
     * @param callback 回调
     */
    public static void netRequest(Call<JsonObject> call, final INetworkResponse callback) {
        //请求
        call.enqueue(new Callback<JsonObject>() {

            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.body() == null) {
                    logger.error("data is null");
                    callback.onFailure(INetworkResponse.ERR_ANALYSIS_DATA);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        callback.onSucceed(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onFailure(INetworkResponse.ERR_ANALYSIS_DATA);
                    }
                }
            }

            public void onFailure(Call<JsonObject> call, Throwable t) {
                //网络问题会走该回调
                if (t instanceof SocketTimeoutException) {
                    logger.debug("请求超时");
                } else if (t instanceof ConnectException) {
                    logger.debug("连接错误");
                } else if (t instanceof RuntimeException) {
                    logger.debug("错误");
                }
                logger.debug("Net Request Failure," + t.getMessage());
                callback.onFailure(INetworkResponse.ERR_RESULT_FAILURE);
            }
        });
    }

    /**
     * 通用的网络请求模块,用于批量操作
     *
     * @param call     请求call
     * @param callback 回调
     */
    public static void netRequest2(Call<JsonArray> call, final INetworkResponse callback) {
        //请求
        call.enqueue(new Callback<JsonArray>() {

            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.body() == null) {
                    logger.error( "data is null");
                    callback.onFailure(INetworkResponse.ERR_ANALYSIS_DATA);
                } else {
                    try {
                        JSONArray jsonArray =  new JSONArray(response.body().getAsJsonArray().toString());
                        JSONObject object =new JSONObject();
                        object.put("results",jsonArray);
                        callback.onSucceed(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure(INetworkResponse.ERR_ANALYSIS_DATA);
                    }
                }
            }

            public void onFailure(Call<JsonArray> call, Throwable t) {
                //网络问题会走该回调
                if (t instanceof SocketTimeoutException) {
                    logger.debug("请求超时");
                } else if (t instanceof ConnectException) {
                    logger.debug("连接错误");
                } else if (t instanceof RuntimeException) {
                    logger.debug( "错误");
                }
                logger.debug( "Net Request Failure," + t.getMessage());
                callback.onFailure(INetworkResponse.ERR_RESULT_FAILURE);
            }
        });
    }

    /**
     * 创建bean对象
     *
     * @param bean
     * @return
     */
    public static RequestBody createJsonString(Object bean) {
        Gson gson = new Gson();
        //通过Gson将Bean转化为Json字符串形式
        String route = gson.toJson(bean);
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), route);
    }
    ////////////////////////////////////private method/////////////////////////////////////

    /**
     * 获取okhttp对象,在这里进行相关的配置
     *
     * @return OkHttpClient
     */
    private static OkHttpClient getOkHttpClient() {
        //日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(
                new HttpLoggingInterceptor.Logger() {

                    public void log(String message) {
                        logger.debug("Message:" + message);
                    }
                });

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

        loggingInterceptor.setLevel(level);
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        //OkHttp进行添加拦截器loggingInterceptor
        httpClientBuilder.addInterceptor(loggingInterceptor);
        //添加header
        httpClientBuilder.addInterceptor(headInterceptor);

        return httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

}
