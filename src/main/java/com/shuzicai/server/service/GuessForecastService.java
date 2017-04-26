package com.shuzicai.server.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuzicai.server.entry.GameInfo;
import com.shuzicai.server.entry.GuessForecastRecord;
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
 * Created by Nicky on 2017/3/26.
 * 涨跌预测的处理
 */
public class GuessForecastService {
    //日志类
    private static Logger logger = Logger.getLogger(GameIndexService.class);

    //开始处理中奖结果
    public static void startForecastHandler() {
        getGameInfo();
    }

    /**
     * 获取最新一期游戏信息
     */
    private static void getGameInfo() {
        logger.info("----------开始获取沪深300游戏信息-----------");
        APIInteractive.getGameInfo(GameInfo.objectId_hushen, new INetworkResponse() {
            public void onFailure(int code) {
                logger.error("获取最新期数信息失败：" + code);
            }

            public void onSucceed(JSONObject result) {
                logger.info("获取最新期数信息成功：" + result.toString());
                //更新数据
                try {
                    GameInfo gameInfo = new Gson().fromJson(result.toString(), GameInfo.class);
                    if (null != gameInfo) {
                        //本期的期数
                        int num = gameInfo.getNewestNum() - 1;
                        startGetHuShenInfo(num);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取涨跌预测的信息
     *
     * @param periodNum
     */
    private static void startGetForecastInfo(int periodNum, final float currentPrice,
                                             final float lastPrice) {
        logger.error("\n两次指数数据为，currentPrice：" + currentPrice + "，lastPrice：" + lastPrice);
        BmobQueryUtils utils = BmobQueryUtils.newInstance();
        final String where = utils.setValue("periodNum").equal(periodNum);
        APIInteractive.getGuessForecastRecordInfo(where, new INetworkResponse() {
            public void onFailure(int code) {
                logger.error("获取涨跌预测信息失败：" + code);
            }

            public void onSucceed(JSONObject result) {
                logger.info("获取涨跌预测信息成功：" + result);
                if (null != result) {
                    JSONArray bodyArrays = result.optJSONArray("results");
                    if (null == bodyArrays) {
                        return;
                    }
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<GuessForecastRecord>>() {
                    }.getType();
                    List<GuessForecastRecord> forecastRecords = gson.fromJson(bodyArrays.toString(), listType);

                    //如果上限对于50
                    int num = forecastRecords.size() / 50;
                    for (int count = 0; count <= num; count++) {
                        int endNum = (count * 50) + 49;
                        int end = endNum > forecastRecords.size() ? forecastRecords.size() : endNum;
                        batchesUpdate(forecastRecords.subList(count * 50, end), currentPrice, lastPrice);
                    }
                }
            }
        });
    }

    /**
     * 拼装结果
     *
     * @param forecastRecords
     * @param currentPrice
     * @param lastPrice
     */
    private static void batchesUpdate(List<GuessForecastRecord> forecastRecords,
                                      final float currentPrice, final float lastPrice) {
        logger.info("-------开始计算涨跌预测与结果-------");
        List<BmobBatch> batches = new ArrayList<BmobBatch>();
        float rewardSum = 0;//赢钱总和
        float loserSum = 0;//输钱总额
        for (GuessForecastRecord forecastRecord : forecastRecords) {
            int betResult = lastPrice > currentPrice ? 1 : 0;
            int betStatus = (betResult == forecastRecord.getBetValue()) ? 1 : 0;
            if (1 == betStatus) {
                rewardSum = rewardSum + forecastRecord.getBetSilverValue();
            } else {
                loserSum = loserSum + forecastRecord.getBetSilverValue();
            }
        }
        //记录金币的操作状态
        for (GuessForecastRecord forecastRecord : forecastRecords) {
            if (forecastRecord.getHandlerFlag() == 1) {
                logger.info("----数据已经处理过了-----");
                continue;
            }
            int betResult = lastPrice > currentPrice ? 1 : 0;
            int betStatus = (betResult == forecastRecord.getBetValue()) ? 1 : 0;

            float rewardValue = 0;
            //计算奖品
            if (betStatus == 1) {
                rewardValue = loserSum * (forecastRecord.getBetSilverValue() / rewardSum);
            }
            Map<String, Object> bodyMap = new HashMap<String, Object>();
            bodyMap.put("indexResult", currentPrice);
            bodyMap.put("betResult", betResult);
            bodyMap.put("betStatus", betStatus);
            bodyMap.put("rewardValue", rewardValue);
            bodyMap.put("rewardFlag", 0);
            bodyMap.put("handlerFlag", 1);

            String path = "/1/classes/GuessForecastRecord/" + forecastRecord.getObjectId();
            BmobBatch goldRecordBatch = new BmobBatch("PUT", path, bodyMap);
            batches.add(goldRecordBatch);
        }
        //获取最终封装好的batch
        APIInteractive.bmobBatch(BmobBatch.getBatchCmd(batches), new INetworkResponse() {

            public void onFailure(int code) {
                logger.info("更新失败");
            }

            public void onSucceed(JSONObject result) {
                logger.info("更新成功");
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
                }
                //只有三个值都成功时才提示成功
                if (3 == resultCount) {
                    // ToastUtils.showShortToast("兑换成功");
                } else {
                    // ToastUtils.showShortToast("兑换提交异常");
                }
            }
        });
    }

    /**
     * 获取最新的沪深300信息与上次的沪深指数信息
     *
     * @param periodNum
     */
    private static void startGetHuShenInfo(final int periodNum) {
        BmobQueryUtils utils = BmobQueryUtils.newInstance();
        String where = utils.setValue("periodsNum").Include(new Integer[]{periodNum, periodNum - 1});
        APIInteractive.getHuShenIndex(where, new INetworkResponse() {
            public void onFailure(int code) {
                logger.error("获取沪深信息失败：" + code);
            }

            public void onSucceed(JSONObject result) {
                logger.info("获取两次沪深信息成功：" + result);
                JSONArray bodyArrays = result.optJSONArray("results");
                if (null != bodyArrays) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<HuShenIndex>>() {
                    }.getType();
                    List<HuShenIndex> stockIndices = gson.fromJson(bodyArrays.toString(), listType);
                    if (stockIndices.size() >= 2) {
                        float currentPrice = Float.valueOf(stockIndices.get(0).getNowPrice());
                        float lastPrice = Float.valueOf(stockIndices.get(1).getNowPrice());
                        startGetForecastInfo(periodNum, currentPrice, lastPrice);
                    } else {
                        logger.info("沪深信息不足两次");
                    }
                }
            }
        });
    }
}
