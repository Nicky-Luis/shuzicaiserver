package com.shuzicai.server.network;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shuzicai.server.entry.GameInfo;
import com.shuzicai.server.entry.UpDownResult;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by nicky on 2016/12/29.
 * 接口
 */

public interface APICollections {

    /**
     * 获取所有APP的信息
     */
    @GET("1/apps")
    Call<JsonObject> getApps();


    /**
     * 获取游戏记录信息
     *
     * @param where
     * @return
     */
    @GET("1/classes/GameInfo")
    Call<JsonObject> getGameInfo(@Query("where") String where);

    /**
     * 修改用户信息
     *
     * @param objectID
     * @param bean
     * @return
     */
    @PUT("1/classes/GameInfo/{objectID}")
    Call<JsonObject> updateGameInfo(@Path("objectID") String objectID,
                                    @Body GameInfo bean);


    /**
     * 获取商品信息
     *
     * @param limit
     * @param skip
     * @return
     */
    @GET("1/classes/Goods")
    Call<JsonObject> getGoods(@Query("limit") int limit,
                              @Query("skip") int skip);

    /**
     * 获取游戏结果值
     *
     * @param where
     * @return
     */
    @GET("1/classes/UpDownResult")
    Call<JsonObject> getUpDownResult(@Query("where") String where);



    /**
     * 提交订单
     *
     * @param bean
     * @return
     */
    @POST("1/classes/UpDownResult")
    Call<JsonObject> submitUpDownResult(@Body UpDownResult bean);


    /**
     * 获取服务器时间
     *
     * @return
     */
    @GET("1/timestamp")
    Call<JsonObject> getServerTime();


    /**
     * 批量操作
     *
     * @param bean
     * @return
     */
    @POST("1/batch")
    Call<JsonArray> bmobBatch(@Body Map<String, List> bean);

    /**
     * 查询用户信息
     *
     * @param where
     * @return
     */
    @GET("1/classes/_User")
    Call<JsonObject> getUserInfo(@Query("where") String where);
}
