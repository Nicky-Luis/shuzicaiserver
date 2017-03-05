package com.shuzicai.server.network;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shuzicai.server.entry.GameInfo;
import com.shuzicai.server.entry.UpDownResult;

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
     * @param where
     * @param callback
     */
    public static void getGameInfo(String where, final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.getGameInfo(where);
        NetworkRequest.netRequest(call, callback);
    }

    /**
     * 获取商品信息
     *
     * @param limit
     * @param skip
     * @param callback
     */
    public static void getGoods(int limit, int skip, final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.getGoods(limit, skip);
        NetworkRequest.netRequest(call, callback);
    }

    /**
     * 提交最新的涨跌情况
     *
     * @param order
     * @param callback
     */
    public static void submitUpDownResult(UpDownResult order, final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.submitUpDownResult(order);
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


    /**
     * 获取用户信息
     *
     * @param where
     * @param callback
     */
    public static void getUserInfo(String where, final INetworkResponse callback) {
        if (null == request) {
            initRetrofit();
        }
        Call<JsonObject> call = request.getUserInfo(where);
        NetworkRequest.netRequest(call, callback);
    }

}
