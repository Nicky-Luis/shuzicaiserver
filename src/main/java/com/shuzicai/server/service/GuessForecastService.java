package com.shuzicai.server.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuzicai.server.entry.GuessForecastRecord;
import com.shuzicai.server.entry.LondonGold;
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
    private static Logger logger = Logger.getLogger(GuessForecastService.class);

    /**
     * 开始处理涨跌预测数据信息
     *
     * @param currentIndex
     * @param currentPeriodNum
     */
    public static void startForecastHandler(final LondonGold currentIndex, final int currentPeriodNum) {
        logger.info("\n\n===============开始处理涨跌预测数据信息=============\n\n");
        logger.info("先获取上一次伦敦金数据信息");
        BmobQueryUtils utils = BmobQueryUtils.newInstance();
        String where = utils.setValue("periodsNum").equal(currentPeriodNum - 1);
        APIInteractive.getLondonGold(where, new INetworkResponse() {
            public void onFailure(int code) {
                logger.error("获取上一次伦敦金数据信息失败：" + code);
            }

            public void onSucceed(JSONObject result) {
                logger.info("获取上一次伦敦金数据信息成功：" + result);
                JSONArray bodyArrays = result.optJSONArray("results");
                if (null != bodyArrays) {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<LondonGold>>() {
                    }.getType();
                    List<LondonGold> stockIndices = gson.fromJson(bodyArrays.toString(), listType);
                    if (stockIndices.size() >= 1) {
                        float currentPrice = Float.valueOf(currentIndex.getLatestpri());
                        float lastPrice = Float.valueOf(stockIndices.get(0).getLatestpri());
                        //获取预测信息
                        startGetForecastInfo(currentPeriodNum, currentPrice, lastPrice);
                    } else {
                        logger.info("伦敦金信息不足两次，无法进行涨跌预测");
                    }
                }
            }
        });
    }

    /**
     * 获取涨跌预测的信息
     *
     * @param periodNum
     */
    private static void startGetForecastInfo(final int periodNum,
                                             final float currentPrice,
                                             final float lastPrice) {
        logger.info("\n\n两伦敦金数据为，currentPrice：" + currentPrice + "，lastPrice：" + lastPrice);
        logger.info("\n\n===============开始获取涨跌预测信息==============\n\n");

        BmobQueryUtils utils = BmobQueryUtils.newInstance();
        final String where = utils.setValue("periodNum").equal(periodNum);
        APIInteractive.getGuessForecastRecordInfo(where, new INetworkResponse() {
            public void onFailure(int code) {
                logger.error("获取获取涨跌预测信息失败：" + code);
            }

            public void onSucceed(JSONObject result) {
                logger.info("获取获取涨跌预测信息成功：" + result);
                if (null != result) {
                    JSONArray bodyArrays = result.optJSONArray("results");
                    if (null == bodyArrays) {
                        return;
                    }
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<GuessForecastRecord>>() {
                    }.getType();
                    List<GuessForecastRecord> forecastRecords = gson.fromJson(bodyArrays.toString(), listType);
                    //计算涨跌预测的统计数据
                    if (null != forecastRecords) {
                        int rewardSum = 0;//赢钱总和
                        int loserSum = 0;//输钱总额
                        //涨跌结果
                        int betResult = lastPrice >= currentPrice ? 0 : 1;
                        for (GuessForecastRecord forecastRecord : forecastRecords) {
                            if (betResult == forecastRecord.getBetValue()) {
                                rewardSum = rewardSum + forecastRecord.getBetSilverValue();
                            } else {
                                loserSum = loserSum + forecastRecord.getBetSilverValue();
                            }
                        }

                        //如果上限对于50
                        int length = forecastRecords.size();
                        int num = length / 50;
                        for (int count = 0; count <= num; count++) {
                            int endNum = (count * 50) + 49;
                            int end = endNum > length ? length : endNum;
                            batchesUpdate(forecastRecords.subList(count * 50, end),
                                    rewardSum, loserSum, betResult, currentPrice);
                        }
                    }
                } else {
                    logger.error("获取获取涨跌预测信息为空");
                }
            }
        });
    }

    /**
     * 拼装结果
     *
     * @param forecastRecords
     * @param rewardSum
     * @param loserSum
     * @param betResult
     */
    private static void batchesUpdate(List<GuessForecastRecord> forecastRecords,
                                      int rewardSum, int loserSum,
                                      int betResult, float currentPrice) {
        logger.info("-------开始计算涨跌预测结果-------");
        logger.info("\nrewardSum：" + rewardSum
                + "\nloserSum：" + loserSum
                + "\nbetResult：" + betResult
                + "\ncurrentPrice：" + currentPrice);
        List<BmobBatch> batches = new ArrayList<BmobBatch>();

        //记录金币的操作状态
        for (GuessForecastRecord forecastRecord : forecastRecords) {
            if (forecastRecord.getHandlerFlag() == 1) {
                logger.info("----计算涨跌预测结果-----");
                continue;
            }
            //0:未开奖，1：中奖，2：未中奖
            int betStatus = (betResult == forecastRecord.getBetValue()) ? 1 : 2;
            //计算奖品
            float rewardValue = 0;
            if (betStatus == 1) {
                if (loserSum == 0) {
                    rewardValue = forecastRecord.getBetSilverValue();
                } else {
                    rewardValue = loserSum * (forecastRecord.getBetSilverValue() / rewardSum);
                }
            }
            Map<String, Object> bodyMap = new HashMap<String, Object>();
            bodyMap.put("indexResult", currentPrice);
            bodyMap.put("betResult", betResult);
            bodyMap.put("betStatus", betStatus);
            bodyMap.put("rewardValue", rewardValue);
            bodyMap.put("rewardFlag", betResult == 1 ? 0 : 1);//只有中奖了奖励才会未同步
            bodyMap.put("handlerFlag", 1);//预测已处理

            String path = "/1/classes/GuessForecastRecord/" + forecastRecord.getObjectId();
            BmobBatch goldRecordBatch = new BmobBatch("PUT", path, bodyMap);
            batches.add(goldRecordBatch);
        }
        //获取最终封装好的batch
        APIInteractive.bmobBatch(BmobBatch.getBatchCmd(batches), new INetworkResponse() {

            public void onFailure(int code) {
                logger.error("上传涨跌预测结果失败");
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
                    logger.error("更新涨跌预测中奖结果失败");
                    e.printStackTrace();
                }
                logger.info("更新涨跌预测中奖结果成功,resultCount=" + resultCount);
            }
        });
    }
}
