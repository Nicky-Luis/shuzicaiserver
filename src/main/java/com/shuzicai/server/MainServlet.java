package com.shuzicai.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shuzicai.server.entry.GameInfo;
import com.shuzicai.server.network.APIInteractive;
import com.shuzicai.server.network.BmobQueryUtils;
import com.shuzicai.server.network.INetworkResponse;
import com.shuzicai.server.utils.HttpUtils;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by Nicky on 2017/2/19.
 */
public class MainServlet extends HttpServlet {
    //日之类
    private static Logger logger = Logger.getLogger(MainServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        System.out.println(getServletName() + "doGet");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(getServletName() + "doPost");
    }

    public void destroy() {
        logger.info("---销毁,任务结束---");
    }

    public void init() throws ServletException {
        logger.info("---执行初始化,开始任务---");
        startTask();
    }

    private void startTask() {
        final long timeInterval = 3000;
        Runnable runnable = new Runnable() {
            public void run() {
                //getGameInfo();
                getStockIndexDate();
                getLondonGoldValue();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    //获取游戏信息
    private void getGameInfo() {
        BmobQueryUtils utils = BmobQueryUtils.newInstance();
        String where = utils.setValue("gameType").equal(0);
        APIInteractive.getGameInfo(where, new INetworkResponse() {
            public void onFailure(int code) {
                logger.info("失败：" + code);
            }

            public void onSucceed(JSONObject result) {
                //logger.info("成功：" + result.toString());
                //更新数据
                try {
                    String jArray = result.optString("results");
                    List<GameInfo> gameInfos = new Gson().fromJson(jArray, new
                            TypeToken<List<GameInfo>>() {
                            }.getType());
                    if (null != gameInfos && gameInfos.size() > 0) {
                        GameInfo gameInfo = gameInfos.get(0);
                        updateGameInfo(gameInfo.getObjectId());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //更新游戏信息
    private void updateGameInfo(String onjectID) {
        GameInfo gameInfo = new GameInfo();
        gameInfo.setNewestNum(2);

        APIInteractive.updateGameInfo(onjectID, gameInfo, new INetworkResponse() {
            public void onFailure(int code) {
                logger.info("失败：" + code);
            }

            public void onSucceed(JSONObject result) {
                logger.info("成功：" + result.toString());
            }
        });
    }


    //获取股票信息
    public static void getStockIndexDate() {
        String host = "https://ali-stock.showapi.com";
        String path = "/stockIndex";
        String method = "GET";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE b382d11abeaa468d91ce7c166e448567");
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("stocks", "sh000001,sz399001,sz399006,sz399300");
        //querys.put("needIndex", "1");
        //querys.put("need_k_pic", "1");

        logger.info("--------开始执行任务--------");
        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            System.out.println(response.toString());
            //获取response的body
            String result = EntityUtils.toString(response.getEntity());
            logger.info("--------返回的股票结果--------");
            logger.info(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取伦敦金
    private  void getLondonGoldValue() {
        String host = "http://jisugold.market.alicloudapi.com";
        String path = "/gold/london";
        String method = "GET";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "APPCODE b382d11abeaa468d91ce7c166e448567");
        Map<String, String> querys = new HashMap<String, String>();

        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            System.out.println(response.toString());
            //获取response的body
            String result = EntityUtils.toString(response.getEntity());
            logger.info("--------返回的伦敦金结果--------");
            logger.info(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
