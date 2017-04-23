package com.shuzicai.server.network;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shuzicai.server.entry.GameInfo;
import com.shuzicai.server.entry.GuessForecastRecord;
import com.shuzicai.server.entry.GuessMantissaRecord;
import com.shuzicai.server.entry.GuessWholeRecord;
import com.shuzicai.server.entry.HuShenIndex;
import com.shuzicai.server.entry.LondonGold;
import com.shuzicai.server.entry.StockIndex;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

/**
 * Created by nicky on 2017/1/4.
 * 所有的api请求
 */

public class APIInteractive {

    //请求对象
    private static APICollections request = null;

    //初始化
    public static void initRetrofit() {
        if (null == request) {
            request = NetworkRequest.getRetrofitClient(APICollections.class);
        }
    }

    /**
     * 获取所有APP的信息
     *
     * @param callback
     */
    public static void getAllAPPInfos(final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.getApps();
        NetworkRequest.netRequest(call, callback);
    }


    /**
     * 更新游戏信息
     *
     * @param objectID
     * @param bean
     * @param callback
     */
    public static void updateGameInfo(String objectID, GameInfo bean, final INetworkResponse
            callback) {
        if (null == request) {
            initRetrofit();
        }

        Call<JsonObject> call = request.updateGameInfo(objectID, bean);
        NetworkRequest.netRequest(call, callback);
    }


    /**
     * 获取游戏记录信息
     *
     * @param objectId
     * @param callback
     */
    public static void getGameInfo(String objectId, final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.getGameInfo(objectId);
        NetworkRequest.netRequest(call, callback);
    }

    /**
     * 提交伦敦金信息
     *
     * @param indexData
     * @param callback
     */
    public static void submitLondonGoldData(LondonGold indexData, final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.submitLondonGold(indexData);
        NetworkRequest.netRequest(call, callback);
    }

    /**
     * 提交伦敦金信息
     *
     * @param where
     * @param callback
     */
    public static void getLondonGoldData(String where, final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.getLondonGold(where);
        NetworkRequest.netRequest(call, callback);
    }

    /**
     * 提交股票信息
     *
     * @param indexData
     * @param callback
     */
    public static void submitStockIndexData(StockIndex indexData, final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.submitStockIndex(indexData);
        NetworkRequest.netRequest(call, callback);
    }

    /**
     * 提交沪深300 情况
     *
     * @param index
     * @param callback
     */
    public static void submitHuShenIndex(HuShenIndex index, final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.submitHuShenIndex(index);
        NetworkRequest.netRequest(call, callback);
    }

    /**
     * 获取最新的沪深300 情况
     *
     * @param where
     * @param callback
     */
    public static void getHuShenIndex(String where, final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.getHuShenIndex(where);
        NetworkRequest.netRequest(call, callback);
    }

    /**
     * 获取涨跌预测的信息
     *
     * @param where
     * @param callback
     */
    public static void getGuessForecastRecordInfo(String where, final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.getGuessForecastRecordInfo(where);
        NetworkRequest.netRequest(call, callback);
    }

    /**
     * 获取涨跌预测的信息
     *
     * @param where
     * @param callback
     */
    public static void getGuessMantissaRecordInfo(String where, final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.getGuessMantissaRecordInfo(where);
        NetworkRequest.netRequest(call, callback);
    }


    /**
     * 更新涨跌预测的结果
     *
     * @param objectId
     * @param forecastRecord
     * @param callback
     */
    public static void updateGuessForecastRecord(String objectId, GuessForecastRecord forecastRecord,
                                                 final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.updateGuessForecastRecord(objectId, forecastRecord);
        NetworkRequest.netRequest(call, callback);
    }

    /**
     * 获取全数预测的信息
     *
     * @param where
     * @param callback
     */
    public static void getGuessWholeRecordInfo(String where, final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.getGuessWholeRecord(where);
        NetworkRequest.netRequest(call, callback);
    }


    /**
     * 更新尾数预测的结果
     *
     * @param objectId
     * @param mantissaRecord
     * @param callback
     */
    public static void updateGuessMantissaRecord(String objectId, GuessMantissaRecord mantissaRecord,
                                                 final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.updateGuessMantissaRecord(objectId, mantissaRecord);
        NetworkRequest.netRequest(call, callback);
    }

    /**
     * 更新全数预测的结果
     *
     * @param objectId
     * @param wholeRecord
     * @param callback
     */
    public static void updateGuessWholeRecord(String objectId, GuessWholeRecord wholeRecord,
                                              final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.updateGuessWholeRecord(objectId, wholeRecord);
        NetworkRequest.netRequest(call, callback);
    }

    /**
     * 获取服务器时间
     *
     * @param callback
     */
    public static void getServerTime(final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.getServerTime();
        NetworkRequest.netRequest(call, callback);
    }


    /**
     * 批量操作
     *
     * @param bean
     * @param callback
     */
    public static void bmobBatch(Map<String, List> bean, final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonArray> call = request.bmobBatch(bean);
        NetworkRequest.netRequest2(call, callback);
    }

}
