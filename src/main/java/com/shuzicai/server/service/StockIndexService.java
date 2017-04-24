package com.shuzicai.server.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuzicai.server.entry.StockIndex;
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

import static com.shuzicai.server.GlobalConstants.APP_CODE;
import static com.shuzicai.server.GlobalConstants.Index_Host;
import static com.shuzicai.server.GlobalConstants.Index_Path;
import static com.shuzicai.server.GlobalConstants.method;

/**
 * Created by Nicky on 2017/3/25.
 * 作为显示的四个指数信息处理
 */
public class StockIndexService {

    //日志类
    private static Logger logger = Logger.getLogger(StockIndexService.class);
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
            //解析并进行上传
            for (StockIndex indices : stockIndices) {
                updateStockIndexInfo(indices);
            }
        } else {
            logger.error("获取股票信息失败");
        }
    }

    /***
     * 更新伦敦金信息
     * @param stockIndex
     */
    private static void updateStockIndexInfo(StockIndex stockIndex) {
        logger.info("\n==========开始上传股票信息==========\n");
        APIInteractive.submitStockIndexData(stockIndex, new INetworkResponse() {
            public void onFailure(int code) {
                logger.error("上传伦股票信息失败");
            }

            public void onSucceed(JSONObject result) {
                logger.info("上传股票信息成功");
            }
        });
    }

}
