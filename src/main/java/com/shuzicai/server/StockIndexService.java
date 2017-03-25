package com.shuzicai.server;

import com.shuzicai.server.utils.HttpUtils;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.shuzicai.server.GlobalConstants.*;

/**
 * Created by Nicky on 2017/3/25.
 * .
 */
public class StockIndexService {
    //日志类
    private static Logger logger = Logger.getLogger(StockIndexService.class);

    //获取股票信息
    public static void getStockIndexDate() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", APP_CODE);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("stocks", "sh000001,sz399001,sz399006,sz399300");
        //querys.put("needIndex", "1");
        //querys.put("need_k_pic", "1");

        logger.info("--------开始执行任务--------");
        try {
            HttpResponse response = HttpUtils.doGet(Index_Host, Index_Path, method,
                    headers, querys);
            System.out.println(response.toString());
            //获取response的body
            String result = EntityUtils.toString(response.getEntity());
            logger.info("--------返回的股票结果--------");
            logger.info(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
