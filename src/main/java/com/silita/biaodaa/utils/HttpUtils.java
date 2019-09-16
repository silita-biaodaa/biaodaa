package com.silita.biaodaa.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: HttpUtil
 *
 * @author zhushuai
 * @version 1.0
 * @since JDK 1.8
 */
public class HttpUtils {

    final static String ProxyUser = "H42I0796HK140EUD";
    final static String ProxyPass = "169AE1671CB4912F";

    // 代理服务器
    final static String ProxyHost = "http-dyn.abuyun.com";
    final static Integer ProxyPort = 9020;

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(HttpUtils.class);

    /**
     * Description向指定URL发送GET方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果，null表示数据接口异常(含SQL执行异常), -1表示接口调用超时
     * @date 2017年4月19日
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            if (null != param) {
                urlNameString = urlNameString + "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.62 Safari/537.36");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                logger.info(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("发送 GET请求出现异常！！！", e);
            return null;
        } finally {
            // 使用finally块来关闭输入流
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    public static String sendGetUrl(String url) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.62 Safari/537.36");
            logger.info("---------send begin---------url:" + url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            logger.info("---------send end--------------");
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String resultStr = EntityUtils.toString(httpResponse.getEntity());
                logger.info("send result:" + resultStr);
                return resultStr;
            } else if (httpResponse.getStatusLine().getStatusCode() == 403) {
                logger.info("------status code 403 重新调用--------------------");
                return sendGetUrl(url);
            }
            return null;
        } catch (IOException e) {
            logger.error("发送 GET请求出现异常！！！", e);
            return null;
        } finally {
            if (null != httpClient) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 代理请求
     *
     * @param url
     * @return
     */
    public static String sendProxyGetUrl(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ProxyHost, ProxyPort));
            Authenticator.setDefault(new Authenticator() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(ProxyUser, ProxyPass.toCharArray());
                }
            });
            logger.info("---------send begin---------url:" + url);
            URL sendUrl = new URL(url);
            URLConnection urlConnection = sendUrl.openConnection(proxy);
            // 设置通用的请求属性
            urlConnection.setRequestProperty("accept", "*/*");
            urlConnection.setRequestProperty("connection", "Keep-Alive");
            urlConnection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            urlConnection.connect();
            logger.info("---------send end--------------");
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (IOException e) {
            logger.error("发送 GET请求出现异常！！！", e);
            return null;
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //解析http信息
    public static Map<String, String> parseRequest(HttpServletRequest request) {
        String userId = (String) request.getSession().getAttribute("userId");
        String ipAddr = request.getHeader("X-real-ip");//获取用户真实ip
        HashMap map = new HashMap();
        map.put("userId", userId);
        map.put("ipAddr", ipAddr);
        return map;
    }

    public static String connectURL(String address, String jsonstr, String requestMethod) {
        String result = "";
        URL url = null;
        HttpURLConnection conn = null;
        try {
            url = new URL(address);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1000 * 60 * 5);
            conn.setReadTimeout(1000 * 60 * 5);
            conn.setDoOutput(true);
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
            OutputStream out = conn.getOutputStream();
            out.write(jsonstr.getBytes("UTF-8"));
            out.flush();
            out.close();

            String resCode = new Integer(conn.getResponseCode()).toString();
            logger.info("http请求响应码:" + resCode);
            InputStream input = resCode.startsWith("2") ? conn.getInputStream() : conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            result = reader.readLine();
            logger.info("http请求返回数据:" + result);
        } catch (Exception e) {
            logger.error("http提交请求异常,", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }
}
