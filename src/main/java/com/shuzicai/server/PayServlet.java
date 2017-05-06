package com.shuzicai.server;

import com.shuzicai.server.pay.OrderInfoUtil2_0;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Nicky on 2017/4/29.
 * 支付接口
 */
public class PayServlet extends HttpServlet {

    /**
     * 支付宝支付业务：入参app_id
     */
    private static final String APPID = "2017042106881063";
    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /**
     * 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097
     * &docType=1
     */
    private static final String RSA2_PRIVATE =
            "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCXh8GyWZAt7rS+vqzfpRbJswz3" +
                    "+ByHMgz24qvDm4lHG86Gm5gcNiMi1HQ+hHD5PSIpfqHYhaO" +
                    "+Cv99DOyVTqblgv2J4pxaZLTPhbu2dwms7e04DrycgSbi79wBStJZKoI0UqjhWPHx03ynDczaAzJowl5HKbqBobN3OXIPbfQ4dkjjTyCvExchOo58iKylLXHEvByhWXtbhteE5DuccKZD6EXoy4DcJVEEkjEXXOBv+gmA8cqWybz5hQCtDSZozwnX3DeLBuTTLRI0pMi5CYPGiekQzKZ1wBz0MuWubVqLnVsOHwnDxJFUdOfZAHNIo8UyjvMD3FxUPnO7osIrnDSPAgMBAAECggEAaEnt3MLZgVNloFX5At9LZLxPc6aWc9HVrGFrF/BHNeKEbI5PzTLRUdpoutJKMq+JgId/+lZX0KcfGcC4XzZHlXRm7OxC8c+Xxa/j9nvM8/C4sDksRIZ67ftpLpaBWQ7xl/M/+gBOXEJS62KLF4zVt9X2LwIgM7HHo4ms38OsLuLiv6GhtjHxKTfDRDsxUBw/AtI0LSq03oAqxFFDsq19hWDUurI5ebHGGeOnBNgJWzlU1yKNNr89QbL0iFxHeRHswMNLjqmuUBfeC5shptQBPqvS8mzUYuYsyjWT1TPpJ/Qpvy/SzGo3tpQ70YWMZcchifDIjxuCupvR9yFSwZwaiQKBgQDGWOP4h0tS0AWu0rKzIeKgjRaYUah/1y594j+orxZy4O5VkeV89jF/zcSe0vYYYRWaTwRQ1i62O+TSY55H/w3AV5togPQ0wD626ny+1/kop8CzlWMPISWBy6vS24uVQWHinmRx2eNNIkOgke9zyOIgf2MQUppX4lkH5Ws6Mz5pAwKBgQDDkzJXeWddLyRJZSKdkNBr2CK0XtztxmpEQhJS5RT069THno8451AqtaZAQcjiRcK0IPdyGwcC2m8f+JiOr3Grj5amrKUUcX9nEdMZ4QgjHtkXbw1bdx65vIvf+KHQwWtHMr8bCtgQwM93v77dwWdTM/BLSKk382IG2yFhNinihQKBgCOwOtStA1CTPXKHVsAEiNJ6kGY3yym1fhgIbYvcdR2We0vKRJCHe8CTNZ2eN8mLLmflb9FbuGvxuupgIkqyCbdiDARxr38OWDpxVqDAG4TFKlcluf0u6aDBFNmLx0HD0ekEtu4FVnC6iZCKsZsTuhw+z4RQWJUXlQ1mqjR+o+whAoGBALgrXiW3opnCseYXwgAlBc/4qb22809KIHoEyiKbrR9zEV2lt/N7CCggBN3P1S8XSGG+BabDQtfyAOFXhbogBufrAJAWViIpOzNBOszM94+zFfYWUrrGMv3vSrzv8tmVusNCS2pYzN0B5J08yK0k2nsklu0eEJQqsi4InqdfjRPNAoGBAMF1s9pnHgziojMuBouj9aWlh464wbYMKxWJEkLWO/rKFXqRsfQCOQbE4ILNeqoD54vh0zi4JbMlukmYGy/KV+ciPwxOXdazEHG/CSH/2sxFxglWuWIvlLBKN+XeDME1P0yhwt5ZPw4wECRL+yQtSXAYE++Y3s1q3F6WCjz/e9N1";
    private static final String RSA_PRIVATE = "";


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String total_amount = request.getParameter("total_amount");
        String body = getNewString(request.getParameter("body"));
        String subject = getNewString(request.getParameter("subject"));
        System.out.println("pay doGet;参数"
                + "\ntotal_amount:" + total_amount
                + "\nbody：" + body
                + "\nsubject：" + subject);
        String order = getOrderInfo(total_amount, body, subject);
        //返回
        String jsonStr = "{\"order\":\"" + order + "\"}";
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.write(jsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        System.out.println("pay doPost");
    }

    /**
     * 获取OrderInfo
     *
     * @return String
     */
    private String getOrderInfo(String total_amount,
                                String subject,
                                String body) {
        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(
                total_amount,
                subject,
                body,
                APPID);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        return orderParam + "&" + sign;
    }

    /**
     * 解决中文乱码
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getNewString(String str) throws UnsupportedEncodingException {
        return new String(str.getBytes("ISO-8859-1"), "UTF-8");
    }
}
