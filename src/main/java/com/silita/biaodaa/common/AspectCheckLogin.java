package com.silita.biaodaa.common;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.service.LoginInfoService;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.silita.biaodaa.common.SecurityCheck.checkSigner;
import static com.silita.biaodaa.utils.EncryptUtils.Base64Decode;
import static com.silita.biaodaa.utils.HttpUtils.parseRequest;
import static com.silita.biaodaa.utils.TokenUtils.parseJsonString;

@Aspect    //该标签把LoggerAspect类声明为一个切面
@Order(1)  //设置切面的优先级：如果有多个切面，可通过设置优先级控制切面的执行顺序（数值越小，优先级越高）
@Component //该标签把LoggerAspect类放到IOC容器中
public class AspectCheckLogin {

    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private LoginInfoService loginInfoService;

    @Autowired
    MyRedisTemplate myRedisTemplate;

    @Pointcut("(execution(public * com.silita.biaodaa.controller.*.*(..)))")
    public void declearJoinPointExpression(){}

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


    /**
     * 单渠道同时只允许用户在一个终端使用
     * @param userId
     * @param channel
     * @param loginTime
     * @return
     */
    private boolean verifyLoginByChannel(String userId,String channel,Long loginTime){
        boolean state = true;
        try {
            String hk = Constant.buildLoginChanelKey(userId, channel);
            Object v = myRedisTemplate.getHashValue(Constant.LOGIN_HASH_KEY, hk);
            if (v != null) {
                Long time = Long.parseLong(v.toString());
                if (time.equals(loginTime)) {
                    state = true;
                } else if (loginTime < time) {
                    state = false;
                } else {
                    myRedisTemplate.putToHash(Constant.LOGIN_HASH_KEY, hk, loginTime);
                    state = true;
                }
            }else{
                state = false;
            }
        }catch (Exception e){
            logger.error(e,e);
        }
        return state;
    }


    @Around(value="declearJoinPointExpression()")
    public Object aroundMethod(ProceedingJoinPoint point) throws UnsupportedEncodingException {
        Object result = null;
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String calssName = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        String permissions = VisitInfoHolder.getPermissions();
        String roleCode = VisitInfoHolder.getRoleCode();
        Object[] args = point.getArgs();
        logger.debug("AspectPermissions [calssName:"+calssName+"][methodName:"+methodName+"][Permissions:"+permissions+"][RoleCode:"+roleCode+"]");
        Map resMap = null;
        try {
            resMap = doFilter(req,point);
            if(resMap!=null){
                result =resMap;
            }else {
                //执行目标方法
                result = point.proceed();
            }
            //返回通知
            logger.debug("The method " + methodName + " end. result<" + result + ">");
        } catch (Throwable e) {
            logger.error(e,e);
            resMap = new HashMap();
            resMap.put("code", Constant.EXCEPTION_CODE);
            resMap.put("msg","登录校验异常。"+e.getMessage());
            result =resMap;
        }
        //后置通知
        logger.debug("The method "+ methodName+" end...");
        return result;
    }

    private Map doFilter(HttpServletRequest request,ProceedingJoinPoint point)
            throws IOException, ServletException {
        Map resMap = new HashMap();
        String requestUri = request.getRequestURI();
        String ipAddr = parseRequest(request).get("ipAddr");
        String xToken = SecurityCheck.getHeaderValue(request, "X-TOKEN");
        String filterUrl = PropertiesUtils.getProperty("FILTER_URL");
        //绿色通道检查
        boolean greenWay =greenWayVerify(requestUri,filterUrl,xToken);
        logger.debug("requestUri:"+requestUri);
        if (greenWay) {//无需校验token
            return null;
        } else {
            String name = null;
            String phone = null;
            String userId = null;
            Long date = null;
            String permissions=null;
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

                        VisitInfoHolder.setPermissions(paramMap.get("permissions"));
                        VisitInfoHolder.setRoleCode(paramMap.get("roleCode"));
                        name = paramMap.get("loginName");
                        phone = paramMap.get("phoneNo");
                        userId = paramMap.get("pkid");
                        String chanel = paramMap.get("channel");
                        date = Long.parseLong(paramMap.get("loginTime")!=null ? paramMap.get("loginTime"):"0");
                        //单用户多渠道登录状态校验
                        if(!verifyLoginByChannel(userId,chanel,date)) {
                            resMap.put("code",Constant.ERR_VERIFY_USER_TOKEN);
                            resMap.put("msg","用户登录失效，请重新登录。");
                            return resMap;
                        }
                        //验证签名
                        tokenValid = SecurityCheck.checkSigner(paramMap, sign);
                    }else{
                        resMap.put("code",Constant.FAIL_CODE);
                        resMap.put("msg",relogin);
                        return resMap;
                    }
                }else{
                    logger.warn("非法token![token:"+token+"][ip:"+ipAddr+"]");
                    resMap.put("code",Constant.FAIL_CODE);
                    resMap.put("msg",errMsg);
                    return resMap;
                }

                String blacklist = PropertiesUtils.getProperty("blacklist");
                if (blacklist != null && blacklist.contains(phone)) {
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
                    resMap.put("code",Constant.FAIL_CODE);
                    resMap.put("msg",errMsg);
                    return resMap;
                }
            }else {
                resMap.put("code",Constant.FAIL_CODE);
                resMap.put("msg",relogin);
                return resMap;
            }
        }

        return null;
    }

//    public void doFilter(ServletRequest servletRequest,
//                         ServletResponse servletResponse, FilterChain filterChain)
//            throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        String requestUri = request.getRequestURI();
//        String ipAddr = parseRequest(request).get("ipAddr");
//        String xToken = SecurityCheck.getHeaderValue(request, "X-TOKEN");
//        String filterUrl = PropertiesUtils.getProperty("FILTER_URL");
//        //绿色通道检查
//        boolean greenWay =greenWayVerify(requestUri,filterUrl,xToken);
//        logger.debug("requestUri:"+requestUri);
//        if (greenWay) {//无需校验token
//            filterChain.doFilter(servletRequest, servletResponse);
//        } else {
//            String name = null;
//            String phone = null;
//            String userId = null;
//            Long date = null;
//            String permissions=null;
//            Map<String, String> parameters = new HashedMap();
//            boolean isHei = false;
//            boolean tokenValid= false;
//            if (xToken != null) {
//                String[] token = xToken.split("\\.");
//                if (token.length == 2) {//旧token验证逻辑
//                    String sign = token[0];
//                    String json = Base64Decode(token[1]);
//                    JSONObject jsonObject = (JSONObject) JSONObject.parse(json);
//                    name = jsonObject.getString("name");
//                    String password = jsonObject.getString("password");
//                    phone = jsonObject.getString("phone");
//                    userId = jsonObject.getString("userId");
//                    date = jsonObject.getLong("date");
//                    parameters.put("name", name);
//                    parameters.put("password", password);
//                    parameters.put("phone", phone);
//                    parameters.put("userId", userId);
//                    parameters.put("date", String.valueOf(date));
//                    tokenValid = checkSigner(parameters, sign);
//                }else if(token.length ==3){//新版token校验
//                    if(verifyTokenVersion(xToken)){
//                        String[] sArray = xToken.split(Constant.TOKEN_SPLIT);
//                        String paramJson = sArray[1];
//                        String sign = sArray[2];
//                        paramJson = Base64Decode(paramJson);
//                        Map<String,String> paramMap = parseJsonString(paramJson);
//
//                        VisitInfoHolder.setPermissions(paramMap.get("permissions"));
//                        VisitInfoHolder.setRoleCode(paramMap.get("roleCode"));
//                        name = paramMap.get("loginName");
//                        phone = paramMap.get("phoneNo");
//                        userId = paramMap.get("pkid");
//                        date = Long.parseLong(paramMap.get("login_time")!=null ? paramMap.get("login_time"):"0");
//                        tokenValid= SecurityCheck.checkSigner(paramMap,sign);
//                    }else{
//                        outPrintMsg(response,"{\"code\":0,\"msg\":\"请重新登录!\"}");
//                        return;
//                    }
//                }else{
//                    logger.debug("非法token![token:"+token+"][ip:"+ipAddr+"]");
//                    printError(response);
//                    return ;
//                }
//
//                String blacklist = PropertiesUtils.getProperty("blacklist");
//                if (blacklist.contains(phone)) {
//                    isHei = true;
//                }
//
//                if ("/foundation/version".equals(requestUri)) {
//                    loginInfoService.saveLoginInfo(name,phone,date);
//                }
//            }
//
//            VisitInfoHolder.setUserId(phone);
//            VisitInfoHolder.setUid(userId);
//
//            if (tokenValid) {
//                if (isHei) {
//                    printError(response);
//                } else {
//                    filterChain.doFilter(servletRequest, servletResponse);
//                }
//            }else {
//                outPrintMsg(response,"{\"code\":0,\"msg\":\"请重新登录!!!\"}");
//            }
//        }
//    }

    public static final String relogin ="登录信息已过期，请重新登录！";
    public static final String errMsg ="您的账号疑似非法爬虫，现已冻结，如有疑问，请联系标大大客服(0731-85076077)";

    private void printError(HttpServletResponse response) throws IOException {
        outPrintMsg(response,"{\"code\":0,\"msg\":\""+errMsg+"\"}");
    }

    private void outPrintMsg(HttpServletResponse response,String Msg) throws IOException {
        response.setCharacterEncoding(Constant.STR_ENCODING);
        response.setContentType("application/json; charset="+Constant.STR_ENCODING);
        PrintWriter out = response.getWriter();
        out.print(Msg);
    }

//    public void init(FilterConfig filterConfig) throws ServletException {
//        ServletContext sc = filterConfig.getServletContext();
//        XmlWebApplicationContext cxt = (XmlWebApplicationContext) WebApplicationContextUtils.getWebApplicationContext(sc);
//        if (cxt != null && cxt.getBean("loginInfoService") != null && loginInfoService == null) {
//            loginInfoService = (LoginInfoService) cxt.getBean("loginInfoService");
//        }
//    }
}
