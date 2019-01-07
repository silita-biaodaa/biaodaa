package com.silita.biaodaa.common;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.service.LoginInfoService;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import static com.silita.biaodaa.common.SecurityCheck.checkSigner;
import static com.silita.biaodaa.utils.EncryptUtils.Base64Decode;
import static com.silita.biaodaa.utils.HttpUtils.parseRequest;

/**
 * Created by zhangxiahui on 17/7/26.
 */
public class CheckLoginFilter implements Filter {

    private Logger logger = Logger.getLogger(this.getClass());

    LoginInfoService loginInfoService;


    /**
     * 验证token版本是否和当前server版本一致
     * @param xToken
     * @return
     */
    private boolean verifyTokenVersion(String xToken){
        String[] tokens = xToken.split(Constant.TOKEN_SPLIT);
        try {
            String version = Base64Decode(tokens[0]);
            String tokenVersion = PropertiesUtils.getProperty("token.version");
            if(version.equals(tokenVersion)){
                return true;
            }
        }catch (Exception e){
            logger.error(e,e);
        }
        return false;
    }

    /**
     * 校验token信息
     * @param tokenStr
     * @return
     */
    private boolean validateToken(String tokenStr){
        boolean flag = false;
        //（token版本号.用户信息.sign）
        if(tokenStr != null && tokenStr.length()>0){
            String[] sArray = tokenStr.split(Constant.TOKEN_SPLIT);
//            String tVersion = sArray[0];
            String paramJson = sArray[1];
            String sign = sArray[2];
            try {
//                tVersion = Base64Decode(tVersion);
                paramJson = Base64Decode(paramJson);
                Map<String,String> paramMap = parseJsonString(paramJson);
                flag= SecurityCheck.checkSigner(paramMap,sign);
            }catch (Exception e){
                logger.error(e,e);
            }
        }
        return flag;
    }

    private Map<String, String> parseJsonString(String paramJson){
        JSONObject jsonObject = (JSONObject) JSONObject.parse(paramJson);
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("login_name",jsonObject.getString("login_name"));
        paramMap.put("user_name",jsonObject.getString("user_name"));
        paramMap.put("pkid", jsonObject.getString("pkid"));
        paramMap.put("channel", jsonObject.getString("channel"));
        paramMap.put("phone_no", jsonObject.getString("phone_no"));
        paramMap.put("login_time",jsonObject.getString("login_time"));
        paramMap.put("tokenVersion",jsonObject.getString("tokenVersion"));
        return paramMap;
    }

    private boolean greenWayVerify(String requestUri, String filterUrl,String xToken){
        boolean greenWay= false;
        if (requestUri.equals("/") || "biaodaaTestToken".equals(xToken)) {
            greenWay = true;
        }else {
            if (filterUrl != null) {
                String[] urls = filterUrl.split(",");
                for (String url : urls) {
                    if (requestUri.contains(url)) {
                        greenWay = true;
                        break;
                    }
                }
            }
        }
        return greenWay;
    }

    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestUri = request.getRequestURI();
        String ipAddr = parseRequest(request).get("ipAddr");
        String xToken = SecurityCheck.getHeaderValue(request, "X-TOKEN");
        String filterUrl = PropertiesUtils.getProperty("FILTER_URL");
        //绿色通道检查
        boolean greenWay =greenWayVerify(requestUri,filterUrl,xToken);
        logger.debug("requestUri:"+requestUri);
        if (greenWay) {//无需校验token
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            String name = null;
            String phone = null;
            String userId = null;
            Long date = null;
            Map<String, String> parameters = new HashedMap();
            boolean isHei = false;
            boolean tokenValid= false;
            if (xToken != null) {
                String[] token = xToken.split("\\.");
                if (token.length == 2) {//旧token验证逻辑
                    String sign = token[0];
                    String json = Base64Decode(token[1]);
                    JSONObject jsonObject = (JSONObject) JSONObject.parse(json);
                    name = jsonObject.getString("name");
                    String password = jsonObject.getString("password");
                    phone = jsonObject.getString("phone");
                    userId = jsonObject.getString("userId");
                    date = jsonObject.getLong("date");
                    parameters.put("name", name);
                    parameters.put("password", password);
                    parameters.put("phone", phone);
                    parameters.put("userId", userId);
                    parameters.put("date", String.valueOf(date));
                    tokenValid = checkSigner(parameters, sign);
                }else if(token.length ==3){//新版token校验
                    if(verifyTokenVersion(xToken)){
                        String[] sArray = xToken.split(Constant.TOKEN_SPLIT);
                        String paramJson = sArray[1];
                        String sign = sArray[2];
                        paramJson = Base64Decode(paramJson);
                        Map<String,String> paramMap = parseJsonString(paramJson);
                        tokenValid= SecurityCheck.checkSigner(paramMap,sign);

                        name = paramMap.get("login_name");
                        phone = paramMap.get("phone_no");
                        date = Long.parseLong(paramMap.get("login_time")!=null ? paramMap.get("login_time"):"0");
                    }else{
                        outPrintMsg(response,"{\"code\":0,\"msg\":\"请重新登录!!!\"}");
                        return;
                    }
                }else{
                    logger.debug("非法token![token:"+token+"][ip:"+ipAddr+"]");
                    printError(response);
                    return ;
                }

                String blacklist = PropertiesUtils.getProperty("blacklist");
                if (blacklist.contains(phone)) {
                    isHei = true;
                }

                if ("/foundation/version".equals(requestUri)) {
                    loginInfoService.saveLoginInfo(name,phone,date);
                }
            }

            VisitInfoHolder.setUserId(phone);
            VisitInfoHolder.setUid(userId);

            if (tokenValid) {
                if (isHei) {
                    printError(response);
                } else {
                    filterChain.doFilter(servletRequest, servletResponse);
                }
            }else {
                outPrintMsg(response,"{\"code\":0,\"msg\":\"请重新登录!!!\"}");
            }
        }
    }

    private void printError(HttpServletResponse response) throws IOException {
        outPrintMsg(response,"{\"code\":0,\"msg\":\"您的账号疑似非法爬虫，现已冻结，如有疑问，请联系标大大客服(0731-85076077)\"}");
    }

    private void outPrintMsg(HttpServletResponse response,String Msg) throws IOException {
        response.setCharacterEncoding(Constant.STR_ENCODING);
        response.setContentType("application/json; charset="+Constant.STR_ENCODING);
        PrintWriter out = response.getWriter();
        out.print(Msg);
    }

    public void destroy() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext sc = filterConfig.getServletContext();
        XmlWebApplicationContext cxt = (XmlWebApplicationContext) WebApplicationContextUtils.getWebApplicationContext(sc);
        if (cxt != null && cxt.getBean("loginInfoService") != null && loginInfoService == null) {
            loginInfoService = (LoginInfoService) cxt.getBean("loginInfoService");
        }
    }
}
