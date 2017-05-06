package com.shuzicai.server;

import com.shuzicai.server.task.QuartzManager;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.SchedulerException;

import java.io.IOException;

import javax.servlet.ServletConfig;
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

    public void init(ServletConfig config) throws ServletException {
        // 初始化日志路径
        initLog4j(config);
        logger.info("---执行初始化,开始任务---");
        try {
            QuartzManager.startTask();
        } catch (SchedulerException e) {
            logger.error("---任务开启失败---");
            e.printStackTrace();
        }
    }

    /**
     * 初始化log4j
     *
     * @param config
     */
    private void initLog4j(ServletConfig config) {
        String root = config.getServletContext().getRealPath("/");
        String log4jLocation = config.getInitParameter("log4jLocation");
        System.setProperty("webRoot", root);
        if (null != log4jLocation && !log4jLocation.equals("")) {
            PropertyConfigurator.configure(root + log4jLocation);
        }
    }
}
