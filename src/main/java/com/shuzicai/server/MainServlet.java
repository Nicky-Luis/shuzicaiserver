package com.shuzicai.server;

import org.apache.log4j.Logger;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Created by Nicky on 2017/2/19.
 * l
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

    /**
     * 开始任务
     */
    private void startTask() {
        final long timeInterval = 3000;
        Runnable runnable = new Runnable() {
            public void run() {
                //getGameInfo();
                StockIndexService.getStockIndexDate();
                GameIndexService.getStockIndexDate();
                GameLondonService.getLondonGoldValue();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

}
