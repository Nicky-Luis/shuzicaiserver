package com.shuzicai.server.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuzicai.server.entry.GuessWholeRecord;
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
 * 全数结果处理
 */
public class GuessWholeService {
    //日志类
    private static Logger logger = Logger.getLogger(GuessWholeService.class);

    /**
     * 开始处理全数预测信息
     *
     * @param stockIndices
     * @param periodNum
     */
    public static void startGuessWholeHandler(LondonGold stockIndices, int periodNum) {
        logger.error("\n\n===============开始进行全数预测中奖计算=============");
        float currentPrice = Float.valueOf(stockIndices.getLatestpri());
        int Num = (int) (currentPrice * 100);//变成整数
        logger.info("当前的价格：" + currentPrice + ",数据:" + Num);
        startGetForecastInfo(periodNum, Num);
    }

    /**
     * 获取用户的预测信息列表
     *
     * @param periodNum
     */
    private static void startGetForecastInfo(int periodNum, final int currentPrice) {
        BmobQueryUtils utils = BmobQueryUtils.newInstance();
        String where = utils.setValue("periodNum").equal(periodNum);
        //获取全数预测的信息
        APIInteractive.getGuessWholeRecordInfo(where, new INetworkResponse() {
            public void onFailure(int code) {
                logger.error("获取全数预测信息失败：" + code);
            }

            public void onSucceed(JSONObject result) {
                logger.info("获取全数预测信息成功：" + result);
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
                    int length = wholeRecords.size();
                    int num = length / 50;
                    for (int count = 0; count <= num; count++) {
                        int endNum = (count * 50) + 49;
                        int end = endNum > length ? length : endNum;
                        batchesUpdate(wholeRecords.subList(count * 50, end), currentPrice);
                    }
                }
            }
        });
    }

    /**
     * 拼装结果
     *
     * @param wholeRecords
     * @param currentPrice
     */
    private static void batchesUpdate(List<GuessWholeRecord> wholeRecords, int currentPrice) {
        logger.info("-------开始更新全数预测结果-------");

        List<BmobBatch> batches = new ArrayList<BmobBatch>();
        //记录金币的操作状态
        for (GuessWholeRecord wholeRecord : wholeRecords) {
            if (wholeRecord.getHandlerFlag() == 1) {
                logger.info("----数据已经处理过了-----");
                continue;
            }
            Map<String, Object> bodyMap = new HashMap<String, Object>();
            bodyMap.put("indexResult", currentPrice);
            bodyMap.put("rewardCount", 0);
            boolean result = wholeRecord.getGuessValue() == currentPrice;
            bodyMap.put("betStatus", result ? 1 : 2);
            if (result) {
                bodyMap.put("rewardCount", 60);
                bodyMap.put("rewardFlag", 0);
            }
            bodyMap.put("handlerFlag", 1);

            String path = "/1/classes/GuessWholeRecord/" + wholeRecord.getObjectId();
            BmobBatch goldRecordBatch = new BmobBatch("PUT", path, bodyMap);
            batches.add(goldRecordBatch);
        }
        //获取最终封装好的batch
        APIInteractive.bmobBatch(BmobBatch.getBatchCmd(batches), new

                INetworkResponse() {

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
                            logger.error("更新全数预测失败");
                        }
                        logger.info("更新全数预测成功,resultCount = " + resultCount);
                    }
                });
    }

}
