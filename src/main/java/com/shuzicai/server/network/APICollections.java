package com.shuzicai.server.network;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shuzicai.server.entry.Config;
import com.shuzicai.server.entry.GuessForecastRecord;
import com.shuzicai.server.entry.GuessMantissaRecord;
import com.shuzicai.server.entry.GuessWholeRecord;
import com.shuzicai.server.entry.LondonGold;
import com.shuzicai.server.entry.StockIndex;

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
    @GET("1/classes/Config/{objectId}")
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
     * 添加伦敦金信息
     *
     * @param bean
     * @return
     */
    @POST("1/classes/LondonGold")
    Call<JsonObject> submitLondonGold(@Body LondonGold bean);

    /**
     * 查询伦敦金信息
     *
     * @param where
     * @return
     */
    @GET("1/classes/LondonGold")
    Call<JsonObject> getLondonGold(@Query("where") String where);


    /**
     * 修改游戏期数信息
     *
     * @param objectID
     * @param bean
     * @return
     */
    @PUT("1/classes/Config/{objectID}")
    Call<JsonObject> updateGameInfo(@Path("objectID") String objectID,
                                    @Body Config bean);


    /**
     * 更新涨跌预测的结果
     *
     * @param bean
     * @return
     */
    @PUT("1/classes/GuessForecastRecord/{objectID}")
    Call<JsonObject> updateGuessForecastRecord(@Path("objectID") String objectID,
                                               @Body GuessForecastRecord bean);

    /**
     * 查询涨跌预测的情况
     *
     * @param where
     * @return
     */
    @GET("1/classes/GuessForecastRecord")
    Call<JsonObject> getGuessForecastRecordInfo(@Query("where") String where);

    /**
     * 更新尾数预测的结果
     *
     * @param bean
     * @return
     */
    @PUT("1/classes/GuessMantissaRecord/{objectID}")
    Call<JsonObject> updateGuessMantissaRecord(@Path("objectID") String objectID,
                                               @Body GuessMantissaRecord bean);

    /**
     * 查询涨跌预测的情况
     *
     * @param where
     * @return
     */
    @GET("1/classes/GuessMantissaRecord")
    Call<JsonObject> getGuessMantissaRecordInfo(@Query("where") String where);

    /**
     * 更新全数预测的结果
     *
     * @param bean
     * @return
     */
    @PUT("1/classes/GuessWholeRecord/{objectID}")
    Call<JsonObject> updateGuessWholeRecord(@Path("objectID") String objectID,
                                            @Body GuessWholeRecord bean);

    /**
     * 查询全数预测的信息
     *
     * @param where
     * @return
     */
    @GET("1/classes/GuessWholeRecord")
    Call<JsonObject> getGuessWholeRecord(@Query("where") String where);

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
     * 获取服务器时间
     *
     * @return
     */
    @GET("1/timestamp")
    Call<JsonObject> getLondonGold();
}
