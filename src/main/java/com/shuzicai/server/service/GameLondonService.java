package com.shuzicai.server.service;

import com.google.gson.Gson;
import com.shuzicai.server.GlobalConstants;
import com.shuzicai.server.entry.GameInfo;
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
        headers.put("Authorization", GlobalConstants.APP_CODE);
        Map<String, String> querys = new HashMap<String, String>();

        try {
            HttpResponse response = HttpUtils.doGet(GlobalConstants.London_Host, GlobalConstants.London_Path,
                    GlobalConstants.method,
                    headers, querys);
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

    /**
     * 解析并上传
     *
     * @param jsonResult
     */
    private static void startAnalysisLondonGold(String jsonResult) {
        JSONObject myJsonObject = new JSONObject(jsonResult);
        String goldResult = myJsonObject.optJSONArray("result").get(0).toString();
        //解析并进行上传
        Gson gson = new Gson();
        LondonGold londonGold = gson.fromJson(goldResult, LondonGold.class);
        if (null != londonGold) {
            getGameInfo(londonGold);
        } else {
            logger.error("获取伦敦金指数失败");
        }
    }

    /**
     * 获取游戏期数信息
     *
     * @param londonGold
     */
    private static void getGameInfo(final LondonGold londonGold) {
        logger.info("----------开始获取游戏信息-----------");
        APIInteractive.getGameInfo(GameInfo.objectId_london, new INetworkResponse() {
            public void onFailure(int code) {
                logger.error("获取游戏信息失败：" + code);
            }

            public void onSucceed(JSONObject result) {
                logger.info("获取游戏信息成功：" + result.toString());
                //更新数据
                try {
                    GameInfo gameInfo = new Gson().fromJson(result.toString(), GameInfo.class);
                    if (null != gameInfo) {
                        //保存上一期的结果
                        int num = gameInfo.getNewestNum();
                        londonGold.setPeriodsNum(num);
                        gameInfo.setNewestNum(num + 1);
                        updateInfo(londonGold, gameInfo);
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
    private static void updateInfo(LondonGold londonGold, GameInfo gameInfo) {
        logger.info("\n开始上传伦敦金数据与最新的期数更新");

        List<BmobBatch> batches = new ArrayList<BmobBatch>();
        //添加股票信息数据
        String path1 = "/1/classes/LondonGold";
        BmobBatch indexBatch = new BmobBatch("POST", path1, londonGold);
        batches.add(indexBatch);
        //更新游戏信息数据
        String path2 = "/1/classes/GameInfo/" + GameInfo.objectId_london;
        BmobBatch gameBatch = new BmobBatch("PUT", path2, gameInfo);
        batches.add(gameBatch);

        //获取最终封装好的batch
        APIInteractive.bmobBatch(BmobBatch.getBatchCmd(batches), new INetworkResponse() {

            public void onFailure(int code) {
                logger.info("上传伦敦金数据与最新的期数更新失败");
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
                    logger.error("上传伦敦金数据与最新的期数更新失败");
                }
                logger.info("上传伦敦金数据与最新的期数成功,resultCount = " + resultCount);
            }
        });
    }
}
