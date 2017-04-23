package com.shuzicai.server;

import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Log4J初始化
 *
 * @author XiongChun
 * @since 2011-04-26
 */

public class Log4jInitServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        //初始化log4j日志组件
         initLogConfig(config);

    }

    private void initLogConfig(ServletConfig config) {
        String prifix = getServletContext().getRealPath("/");
        System.out.println("prifix:" + prifix);
        //获取log4j配置文件地址
        String Log4jFile = config.getInitParameter("Log4jFile");
        System.out.println("Log4jFile:" + Log4jFile);
        String filePath = prifix + Log4jFile;
        System.out.println("filePath:" + filePath);
        PropertyConfigurator.configure(filePath);
        Properties props = new Properties();
        try {
            String Log4jFileSavePath = config.getInitParameter("Log4jFileSavePath");
            System.out.println(Log4jFileSavePath);
            FileInputStream log4jStream = new FileInputStream(filePath);
            props.load(log4jStream);
            log4jStream.close();
            //设置日志保存地址
            String infoFile = prifix + Log4jFileSavePath +  "/logs/info/info.log";
            System.out.println("logFile:" + infoFile);
            props.setProperty("log4j.appender.info.File", infoFile);

            String errorFile = prifix + Log4jFileSavePath +  "/logs/error/error.log";
            System.out.println("logFile:" + errorFile);
            props.setProperty("log4j.appender.error.File", errorFile);

            String debugFile = prifix + Log4jFileSavePath +  "/logs/debug/debug.log";
            System.out.println("logFile:" + debugFile);
            props.setProperty("log4j.appender.debug.File", debugFile);
            //装入log4j配置信息
            PropertyConfigurator.configure(props);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println("初始化log4j日志组件");
    }
}