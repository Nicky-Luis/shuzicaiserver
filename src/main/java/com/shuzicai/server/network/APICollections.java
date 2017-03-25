package com.shuzicai.server.network;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shuzicai.server.entry.GameInfo;
import com.shuzicai.server.entry.HuShenIndex;
import com.shuzicai.server.entry.LondonGold;
import com.shuzicai.server.entry.StockIndex;
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
     * @param objectId
     * @return
     */
    @GET("1/classes/GameInfo/{objectId}")
    Call<JsonObject> getGameInfo(@Path("objectId") String objectId);


    /**
     * 提交股票信息
     *
     * @param bean
     * @return
     */
    @POST("1/classes/StockIndex")
    Call<JsonObject> submitStockIndex(@Body StockIndex bean);

    /**
     * 提交股票信息
     *
     * @param bean
     * @return
     */
    @POST("1/classes/HuShenIndex")
    Call<JsonObject> submitHuShenIndex(@Body HuShenIndex bean);

    /**
     * 添加伦敦金信息
     *
     * @param bean
     * @return
     */
    @POST("1/classes/LondonGold")
    Call<JsonObject> submitLondonGold(@Body LondonGold bean);


    /**
     * 修改游戏期数信息
     *
     * @param objectID
     * @param bean
     * @return
     */
    @PUT("1/classes/GameInfo/{objectID}")
    Call<JsonObject> updateGameInfo(@Path("objectID") String objectID,
                                    @Body GameInfo bean);


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
