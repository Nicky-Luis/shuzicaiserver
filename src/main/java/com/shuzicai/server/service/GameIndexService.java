package com.shuzicai.server.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuzicai.server.GlobalConstants;
import com.shuzicai.server.entry.GameInfo;
import com.shuzicai.server.entry.HuShenIndex;
import com.shuzicai.server.network.APIInteractive;
import com.shuzicai.server.network.INetworkResponse;
import com.shuzicai.server.utils.HttpUtils;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nicky on 2017/3/25.
 * 作为游戏的沪深300数据处理
 */
public class GameIndexService {

    //日志类
    private static Logger logger = Logger.getLogger(GameIndexService.class);
    //沪深300股票数据
    private final static String stocks = "sz399300";

    //获取股票信息
    public static void getStockIndexDate() {
        logger.info("\n=============开始获取作为游戏信息的沪深300=============\n");
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", GlobalConstants.APP_CODE);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("stocks", stocks);
        //querys.put("needIndex", "1");
        //querys.put("need_k_pic", "1");

        try {
            HttpResponse response = HttpUtils.doGet(GlobalConstants.Index_Host, GlobalConstants.Index_Path,
                    GlobalConstants.method,
                    headers, querys);
            System.out.println(response.toString());
            //获取response的body
            String jsonResult = EntityUtils.toString(response.getEntity());
            logger.info("--------返回的股票结果--------");
            logger.info(jsonResult);
            startAnalysisStockIndex(jsonResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 解析并上传
     *
     * @param jsonResult
     */
    private static void startAnalysisStockIndex(String jsonResult) {
        JSONObject myJsonObject = new JSONObject(jsonResult);
        JSONObject jsonObject = myJsonObject.optJSONObject("showapi_res_body");
        if (jsonObject.optInt("ret_code") == 0) {
            JSONArray bodyArrays = jsonObject.optJSONArray("indexList");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<HuShenIndex>>() {
            }.getType();
            List<HuShenIndex> stockIndices = gson.fromJson(bodyArrays.toString(), listType);
            //解析并进行上传
            for (HuShenIndex indices : stockIndices) {
                //沪深300则更新期数
                if (indices.getCode().equals("sz399300")) {
                    getGameInfo(indices);
                }
            }
        } else {
            logger.error("获取股票信息失败");
        }
    }

    /**
     * 获取游戏信息
     *
     * @param stockIndex
     */
    private static void getGameInfo(final HuShenIndex stockIndex) {
        logger.info("----------开始获取沪深300游戏信息-----------");
        APIInteractive.getGameInfo(GameInfo.objectId_hushen, new INetworkResponse() {
            public void onFailure(int code) {
                logger.error("获取沪深300游戏信息失败：" + code);
            }

            public void onSucceed(JSONObject result) {
                logger.info("获取沪深300游戏信息成功：" + result.toString());
                //更新数据
                try {
                    GameInfo gameInfo = new Gson().fromJson(result.toString(), GameInfo.class);
                    if (null != gameInfo) {
                        //保存上一期的结果
                        stockIndex.setPeriodsNum(gameInfo.getNewestNum());
                        updateStockIndexInfo(stockIndex);
                        //更新期数信息
                        gameInfo.setNewestNum(gameInfo.getNewestNum() + 1);
                        updateGameInfo(GameInfo.objectId_hushen, gameInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /***
     * 更新沪深300数据信息
     * @param stockIndex
     */
    private static void updateStockIndexInfo(HuShenIndex stockIndex) {
        logger.info("----------开始上传沪深300信息-----------");
        APIInteractive.submitHuShenIndex(stockIndex, new INetworkResponse() {
            public void onFailure(int code) {
                logger.error("上传沪深300信息失败");
            }

            public void onSucceed(JSONObject result) {
                logger.info("上传沪深300信息成功");
            }
        });
    }

    //更新游戏期数信息
    private static void updateGameInfo(String objectID, GameInfo gameInfo) {
        logger.info("----------开始更新沪深300游戏期数信息-----------");
        APIInteractive.updateGameInfo(objectID, gameInfo, new INetworkResponse() {
            public void onFailure(int code) {
                logger.error("更新沪深300游戏期数失败：" + code);
            }

            public void onSucceed(JSONObject result) {
                logger.info("更新沪深300游戏期数成功：" + result.toString());
            }
        });
    }
}
