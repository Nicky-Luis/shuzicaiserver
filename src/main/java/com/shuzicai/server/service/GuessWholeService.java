package com.shuzicai.server.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuzicai.server.entry.GameInfo;
import com.shuzicai.server.entry.GuessWholeRecord;
import com.shuzicai.server.entry.HuShenIndex;
import com.shuzicai.server.network.APIInteractive;
import com.shuzicai.server.network.BmobQueryUtils;
import com.shuzicai.server.network.INetworkResponse;
import com.shuzicai.server.network.entity.BmobBatch;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nicky on 2017/4/8.
 * 全数结果处理
 */
public class GuessWholeService {
    //日志类
    private static Logger logger = Logger.getLogger(GameIndexService.class);


    //开始处理中奖结果
    public static void startGuessWholeHandler() {
        getGameInfo();
    }

    /**
     * 获取最新一期游戏信息
     */
    private static void getGameInfo() {
        logger.info("----------开始获取沪深300游戏信息-----------");
        APIInteractive.getGameInfo(GameInfo.objectId_hushen, new INetworkResponse() {
            public void onFailure(int code) {
                logger.error("获取最新信息失败：" + code);
            }

            public void onSucceed(JSONObject result) {
                logger.info("获取最沪深300信息成功：" + result.toString());
                //更新数据
                try {
                    GameInfo gameInfo = new Gson().fromJson(result.toString(), GameInfo.class);
                    if (null != gameInfo) {
                        //获取股票信息
                        startGetHuShenInfo(gameInfo.getNewestNum() - 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取最新的沪深300信息
     *
     * @param periodNum
     */
    private static void startGetHuShenInfo(final int periodNum) {
        BmobQueryUtils utils = BmobQueryUtils.newInstance();
        String where = utils.setValue("periodsNum").equal(periodNum);
        APIInteractive.getHuShenIndex(where, new INetworkResponse() {
            public void onFailure(int code) {
                logger.error("获取最新沪深信息失败：" + code);
            }

            public void onSucceed(JSONObject result) {
                logger.info("获取沪深信息成功：" + result);
                JSONArray bodyArrays = result.optJSONArray("results");
                if (null != bodyArrays) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<HuShenIndex>>() {
                    }.getType();
                    List<HuShenIndex> stockIndices = gson.fromJson(bodyArrays.toString(), listType);
                    if (stockIndices.size() > 0) {
                        //最新的价格
                        float currentPrice = Float.valueOf(stockIndices.get(0).getNowPrice());
                        int Num = (int) (currentPrice * 100);//变成整数
                        logger.info("当前的价格：" + currentPrice + ",数据" + Num);
                        startGetForecastInfo(periodNum, Num);
                    }
                }
            }
        });
    }

    /**
     * 获取用户的预测信息列表
     *
     * @param periodNum
     */
    private static void startGetForecastInfo(int periodNum, final int currentPrice) {
        BmobQueryUtils utils = BmobQueryUtils.newInstance();
        String where = utils.setValue("periodsNum").equal(periodNum);
        //获取全数预测的信息
        APIInteractive.getGuessWholeRecordInfo(where, new INetworkResponse() {
            public void onFailure(int code) {
                logger.error("获取尾数预测信息失败：" + code);
            }

            public void onSucceed(JSONObject result) {
                logger.info("获取尾数预测信息成功：" + result);
                if (null != result) {
                    JSONArray bodyArrays = result.optJSONArray("results");
                    if (null == bodyArrays) {
                        return;
                    }
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<GuessWholeRecord>>() {
                    }.getType();
                    List<GuessWholeRecord> wholeRecords = gson.fromJson(bodyArrays.toString(), listType);

                    //如果上限对于50
                    int num = wholeRecords.size() / 50;
                    for (int count = 0; count <= num; count++) {
                        int endNum = (count * 50) + 49;
                        int end = endNum > wholeRecords.size() ? wholeRecords.size() : endNum;
                        batchesUpdate(wholeRecords.subList(count * 50, end), currentPrice);
                    }
                }
            }
        });
    }

    /**
     * 拼装结果
     *
     * @param mantissaRecords
     * @param currentPrice
     */
    private static void batchesUpdate(List<GuessWholeRecord> mantissaRecords, int currentPrice) {
        logger.info("-------开始更新结果-------");

        List<BmobBatch> batches = new ArrayList<BmobBatch>();
        //记录金币的操作状态
        for (GuessWholeRecord forecastRecord : mantissaRecords) {
            Map<String, Object> bodyMap = new HashMap<String, Object>();
            bodyMap.put("indexResult", currentPrice);
            bodyMap.put("rewardCount", 0);
            boolean result = forecastRecord.getGuessValue() == currentPrice;
            bodyMap.put("isReward", result);
            if (result) {
                bodyMap.put("rewardCount", 60);
            }

            String path = "/1/classes/GuessWholeRecord/" + forecastRecord.getObjectId();
            BmobBatch goldRecordBatch = new BmobBatch("PUT", path, bodyMap);
            batches.add(goldRecordBatch);
        }
        //获取最终封装好的batch
        APIInteractive.bmobBatch(BmobBatch.getBatchCmd(batches), new INetworkResponse() {

            public void onFailure(int code) {
                logger.info("更新全数预测失败");
            }

            public void onSucceed(JSONObject result) {
                int resultCount = 0;
                try {
                    JSONArray jsonArray = result.optJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        if (object.has("success")) {
                            resultCount++;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    logger.error("更新全数失败");
                }
                logger.info("更新成功,resultCount = " + resultCount);
            }
        });
    }

}
