package com.shuzicai.server.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuzicai.server.entry.ForecastStatistic;
import com.shuzicai.server.entry.GuessForecastRecord;
import com.shuzicai.server.entry.LondonGold;
import com.shuzicai.server.network.APIInteractive;
import com.shuzicai.server.network.BmobQueryUtils;
import com.shuzicai.server.network.INetworkResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

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
    public static void startForecastHandler(final LondonGold currentIndex,
                                            final int currentPeriodNum) {
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
                        int betResult = currentPrice >= lastPrice ? 1 : 0;
                        for (GuessForecastRecord forecastRecord : forecastRecords) {
                            if (betResult == forecastRecord.getBetValue()) {
                                rewardSum = rewardSum + forecastRecord.getBetSilverValue();
                            } else {
                                loserSum = loserSum + forecastRecord.getBetSilverValue();
                            }
                        }

                        logger.info("赢钱的总和：" + rewardSum + "\n输钱的总和：" + loserSum);
                        ForecastStatistic forecastStatistic = new ForecastStatistic(
                                currentPrice, periodNum, betResult, rewardSum, loserSum);
                        APIInteractive.addForecastStatistic(forecastStatistic, new INetworkResponse() {
                            public void onFailure(int code) {
                                logger.error("更新涨跌预测信息失败");
                            }

                            public void onSucceed(JSONObject result) {
                                logger.info("更新涨跌预测信息成功");
                            }
                        });
                    }
                } else {
                    logger.error("获取获取涨跌预测信息为空");
                }
            }
        });
    }
}
