package com.shuzicai.server.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuzicai.server.entry.StockIndex;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shuzicai.server.GlobalConstants.APP_CODE;
import static com.shuzicai.server.GlobalConstants.Index_Host;
import static com.shuzicai.server.GlobalConstants.Index_Path;
import static com.shuzicai.server.GlobalConstants.method;

/**
 * Created by Nicky on 2017/3/25.
 * 作为显示的四个指数信息处理
 */
public class ShowIndexService {

    //日志类
    private static Logger logger = Logger.getLogger(ShowIndexService.class);
    //股票数据
    private final static String stocks = "sh000001,sz399001,sz399006,sz399300";

    //获取股票信息
    public static void getStockIndexDate() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", APP_CODE);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("stocks", stocks);

        try {
            HttpResponse response = HttpUtils.doGet(Index_Host, Index_Path, method,
                    headers, querys);
            System.out.println(response.toString());
            //获取response的body
            String jsonResult = EntityUtils.toString(response.getEntity());
            logger.info("\n============返回用于显示的股票数据=============\n");
            logger.info(jsonResult);
            logger.info("\n开始解析用于显示的股票数据");
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
            Type listType = new TypeToken<List<StockIndex>>() {
            }.getType();
            List<StockIndex> stockIndices = gson.fromJson(bodyArrays.toString(), listType);
            //解析并进行批量上传
            if (null != stockIndices) {
                updateStockIndexInfo(stockIndices);
            } else {
                logger.error("获取用户显示的股票信息失败");
            }
        } else {
            logger.error("获取用户显示的股票信息失败");
        }
    }

    /***
     * 更新伦敦金信息
     * @param stockIndexs
     */
    private static void updateStockIndexInfo(List<StockIndex> stockIndexs) {
        logger.info("\n开始上传用于显示的股票数据,长度为：" + stockIndexs.size());

        List<BmobBatch> batches = new ArrayList<BmobBatch>();
        for (StockIndex stockIndex : stockIndexs) {
            String path = "/1/classes/StockIndex";
            BmobBatch goldRecordBatch = new BmobBatch("POST", path, stockIndex);
            batches.add(goldRecordBatch);
        }
        //获取最终封装好的batch
        APIInteractive.bmobBatch(BmobBatch.getBatchCmd(batches), new INetworkResponse() {

            public void onFailure(int code) {
                logger.info("上传用于显示的股票数据失败");
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
                    logger.error("上传用于显示的股票数据失败");
                }
                logger.info("上传用于显示的股票数据成功,resultCount = " + resultCount);
            }
        });
    }

}
