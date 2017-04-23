package com.shuzicai.server.utils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Nicky on 2017/4/23.
 */
public class PathUtils {
    private static String webroot = null;

    static {
        webroot = getWebrootPath();
    }

    private final static String getWebrootPath() {
        String root = PathUtils.class.getResource("/").getFile();
        try {
            root = new File(root).getParentFile().getParentFile().getCanonicalPath();
            root += File.separator;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return root;
    }

    /**
     * 返回Web应用的路径
     *
     * @return
     */
    public static String root() {
        return webroot;
    }
}
