package com.shuzicai.server.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuzicai.server.entry.GuessMantissaRecord;
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
 * Created by Nicky on 2017/4/8.
 * 尾数预测获奖结果处理
 */
public class GuessMantissaService {
    //日志类
    private static Logger logger = Logger.getLogger(GuessMantissaService.class);

    /**
     * 开始处理伦敦金尾数预测信息
     *
     * @param londonIndex
     * @param periodNum
     */
    public static void startMantissaHandler(LondonGold londonIndex, int periodNum) {
        //最新的价格
        float currentPrice = Float.valueOf(londonIndex.getLatestpri());
        //取小数点后两位
        int Num = (int) ((currentPrice * 100) - (((int) currentPrice) * 100));
        logger.info("\n\n=======开始处理尾数预测信息==========\n\n");
        logger.info("当前的价格：" + currentPrice + ",后两位" + Num);
        startGetForecastInfo(periodNum, Num, GuessMantissaRecord.Index_Type_Gold);
    }

    /**
     * 获取尾数预测的信息列表
     *
     * @param periodNum
     */
    private static void startGetForecastInfo(int periodNum, final int resultNum, int type) {
        BmobQueryUtils utils = BmobQueryUtils.newInstance();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("periodNum", periodNum);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("indexType", type);

        APIInteractive.getGuessMantissaRecordInfo(utils.and(jsonObject1, jsonObject2), new INetworkResponse() {
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
                    Type listType = new TypeToken<List<GuessMantissaRecord>>() {
                    }.getType();
                    List<GuessMantissaRecord> mantissaRecords = gson.fromJson(bodyArrays.toString(), listType);

                    //如果上限对于50
                    int num = mantissaRecords.size() / 50;
                    for (int count = 0; count <= num; count++) {
                        int endNum = (count * 50) + 49;
                        int end = endNum > mantissaRecords.size() ? mantissaRecords.size() : endNum;
                        batchesUpdate(mantissaRecords.subList(count * 50, end), resultNum);
                    }
                }
            }
        });
    }

    /**
     * 拼装结果
     *
     * @param mantissaRecords
     * @param resultNum
     */
    private static void batchesUpdate(List<GuessMantissaRecord> mantissaRecords, int resultNum) {
        logger.info("-------开始更新尾数预测结果-------");
        //获取个位数
        int secondNum = resultNum % 10;
        List<BmobBatch> batches = new ArrayList<BmobBatch>();
        //记录金币的操作状态
        for (GuessMantissaRecord forecastRecord : mantissaRecords) {
            if (forecastRecord.getHandlerFlag() == 1) {
                logger.info("----数据已经处理过了-----");
                continue;
            }
            Map<String, Object> bodyMap = new HashMap<String, Object>();
            bodyMap.put("indexResult", secondNum);
            bodyMap.put("rewardCount", 0);
            bodyMap.put("handlerFlag", 1);
            bodyMap.put("rewardFlag", 1);

            //计算押注的结果
            if (forecastRecord.getGuessType() == GuessMantissaRecord.Guess_Type_Percentile) {
                //百分位直选
                boolean result = forecastRecord.getGuessValue() == secondNum;
                bodyMap.put("betStatus", result ? 1 : 2);
                if (result) {
                    bodyMap.put("rewardCount", 60);
                    bodyMap.put("rewardFlag", 0);
                }
            } else if (forecastRecord.getGuessType() == GuessMantissaRecord.Guess_Type_DoubleDirect) {
                //双数位直选
                boolean result = forecastRecord.getGuessValue() == resultNum;
                bodyMap.put("betStatus", result ? 1 : 2);
                if (result) {
                    bodyMap.put("rewardCount", 600);
                    bodyMap.put("rewardFlag", 0);
                }
            } else if (forecastRecord.getGuessType() == GuessMantissaRecord.Guess_Type_DoubleGroup) {
                //数据反转
                int num = ((forecastRecord.getGuessValue() % 10) * 10)
                        + (forecastRecord.getGuessValue() / 10) % 10;
                //结果
                boolean result = (forecastRecord.getGuessValue() == resultNum)
                        || (forecastRecord.getGuessValue() == num);
                bodyMap.put("betStatus", result ? 1 : 2);
                if (result) {
                    bodyMap.put("rewardCount", 3000);
                    bodyMap.put("rewardFlag", 0);
                }
            }

            String path = "/1/classes/GuessMantissaRecord/" + forecastRecord.getObjectId();
            BmobBatch goldRecordBatch = new BmobBatch("PUT", path, bodyMap);
            batches.add(goldRecordBatch);
        }
        //获取最终封装好的batch
        APIInteractive.bmobBatch(BmobBatch.getBatchCmd(batches), new INetworkResponse() {

            public void onFailure(int code) {
                logger.error("更新尾数预测结果失败");
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
                    logger.error("更新尾数预测结果失败");
                }
                logger.info("更新尾数预测结果成功,resultCount = " + resultCount);
            }
        });
    }
}
