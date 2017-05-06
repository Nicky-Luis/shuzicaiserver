package com.shuzicai.server.service;

import com.google.gson.Gson;
import com.shuzicai.server.GlobalConstants;
import com.shuzicai.server.entry.Config;
import com.shuzicai.server.entry.LondonGold;
import com.shuzicai.server.network.APIInteractive;
import com.shuzicai.server.network.INetworkResponse;
import com.shuzicai.server.network.entity.BmobBatch;
import com.shuzicai.server.utils.HttpUtils;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nicky on 2017/3/25.
 * 伦敦金数据信息处理
 */
public class GameLondonService {
    //日志类
    private static Logger logger = Logger.getLogger(GameLondonService.class);

    //获取伦敦金数据
    public static void getLondonGoldValue() {
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("key", "e1f2f94ade6c4ab9a761c03352fc5d8c");
        querys.put("paybyvas", "false");

        try {
            HttpResponse response = HttpUtils.doGet(GlobalConstants.London_Host, GlobalConstants.London_Path,
                    GlobalConstants.method, headers, querys);
            System.out.println(response.toString());
            //获取response的body
            String jsonResult = EntityUtils.toString(response.getEntity());
            logger.info("\n--------返回的伦敦金结果--------\n");
            logger.info(jsonResult);
            //转换为json object
            startAnalysisLondonGold(jsonResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //用于测试
    public static void main(String[] args) {
        getLondonGoldValue();
    }

    /**
     * 解析并上传
     *
     * @param jsonResult
     */
    private static void startAnalysisLondonGold(String jsonResult) {
        logger.info("\n开始解析伦敦金数据\n");
        JSONObject myJsonObject = new JSONObject(jsonResult);
        JSONArray jsonArray = myJsonObject.optJSONArray("result");
        for (int j = 0; j < jsonArray.length(); j++) {
            JSONObject jsonObject = jsonArray.getJSONObject(j);
            if (jsonObject.optString("name").equals("现货黄金")) {
                String goldResult = jsonObject.toString();
                //解析并进行上传
                Gson gson = new Gson();
                LondonGold londonGold = gson.fromJson(goldResult, LondonGold.class);
                if (null != londonGold) {
                    getGameInfo(londonGold);
                } else {
                    logger.error("获取伦敦金指数失败");
                }
            }
        }

    }

    /**
     * 获取游戏期数信息
     *
     * @param londonGold
     */
    private static void getGameInfo(final LondonGold londonGold) {
        logger.info("\n伦敦金数据解析成功，开始获取游戏期数信息");
        APIInteractive.getGameInfo(Config.objectId_london, new INetworkResponse() {
            public void onFailure(int code) {
                logger.error("获取游戏期数信息失败：" + code);
            }

            public void onSucceed(JSONObject result) {
                logger.info("获取游戏期数信息成功：" + result.toString());
                //更新数据
                try {
                    Config gameInfo = new Gson().fromJson(result.toString(), Config.class);
                    if (null != gameInfo) {
                        //保存上一期的结果
                        int num = gameInfo.getNewestNum();
                        londonGold.setPeriodsNum(num);
                        gameInfo.setNewestNum(num + 1);
                        //上传数据
                        updateInfo(londonGold, gameInfo);
                    } else {
                        logger.error("获取游戏期数信息失败，为空");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 上传伦敦金数据与最新的期数更新失败
     *
     * @param londonGold
     * @param gameInfo
     */
    private static void updateInfo(final LondonGold londonGold, final Config gameInfo) {
        logger.info("\n开始上传伦敦金数据与最新的期数数据");

        List<BmobBatch> batches = new ArrayList<BmobBatch>();
        //添加伦敦金信息数据
        String path1 = "/1/classes/LondonGold";
        BmobBatch indexBatch = new BmobBatch("POST", path1, londonGold);
        batches.add(indexBatch);
        //更新游戏信息数据
        String path2 = "/1/classes/Config/" + Config.objectId_london;
        BmobBatch gameBatch = new BmobBatch("PUT", path2, gameInfo);
        batches.add(gameBatch);

        //获取最终封装好的batch
        APIInteractive.bmobBatch(BmobBatch.getBatchCmd(batches), new INetworkResponse() {

            public void onFailure(int code) {
                logger.error("上传伦敦金数据与最新的期数数据失败");
            }

            public void onSucceed(JSONObject result) {
                int resultCount = getBmobBatchResultCount(result);
                logger.info("上传伦敦金数据与最新的期数成功,resultCount = " + resultCount);
                logger.info("\n\n==========开始预测结果处理================ ");
                if (2 == resultCount) {
                    int currentNum = gameInfo.getNewestNum() - 1;
                    if (currentNum > 0) {
                        //三种猜测结果处理
                        GuessForecastService.startForecastHandler(londonGold, currentNum);
                        GuessMantissaService.startMantissaHandler(londonGold, currentNum);
                        GuessWholeService.startGuessWholeHandler(londonGold, currentNum);
                    }else {
                        logger.error("期数信息有误");
                    }
                } else {
                    logger.error("上传用于游戏的股票数据与最新的期数信息失败");
                }
            }
        });
    }

    /**
     * 获取批量处理结果
     *
     * @param result
     * @return
     */
    private static int getBmobBatchResultCount(JSONObject result) {
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
            logger.error("批量上传数据失败");
        }
        return resultCount;
    }
}
