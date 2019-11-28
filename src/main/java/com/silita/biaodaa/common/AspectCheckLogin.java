package com.silita.biaodaa.common;

import com.silita.biaodaa.service.LoginInfoService;
import com.silita.biaodaa.utils.MyStringUtils;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
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
    public void declearJoinPointExpression() {
    }

    /**
     * 验证token版本是否和当前server版本一致
     *
     * @param xToken
     * @return
     */
    private boolean verifyTokenVersion(String xToken) {
        String[] tokens = xToken.split(Constant.TOKEN_SPLIT);
        try {
            String version = Base64Decode(tokens[0]);
            String tokenVersion = PropertiesUtils.getProperty("token.version");
            if (version.equals(tokenVersion)) {
                return true;
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
        return false;
    }


    private boolean greenWayVerify(String requestUri, String filterUrl, String xToken) {
        boolean greenWay = false;
        if (requestUri.equals("/") || "biaodaaTestToken".equals(xToken)) {
            greenWay = true;
        } else {
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
     *
     * @param userId
     * @param channel
     * @param loginTime
     * @return
     */
    private boolean verifyLoginByChannel(String userId, String channel, Long loginTime) {
        boolean state = true;
        try {
            String hk = Constant.buildLoginChanelKey(userId, channel);
            logger.info("hk----------" + hk);
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
            } else {
                state = false;
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
        return state;
    }


    @Around(value = "declearJoinPointExpression()")
    public Object aroundMethod(ProceedingJoinPoint point) throws UnsupportedEncodingException {
        Object result = null;
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String calssName = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        String permissions = VisitInfoHolder.getPermissions();
        String roleCode = VisitInfoHolder.getRoleCode();
        logger.debug("AspectPermissions [calssName:" + calssName + "][methodName:" + methodName + "][Permissions:" + permissions + "][RoleCode:" + roleCode + "]");
        Map resMap = null;
        try {
            resMap = doFilter(req, point);
            if (resMap != null) {
                result = resMap;
            } else {
                //执行目标方法
                result = point.proceed();
                if (result instanceof Map) {
                    resMap = (Map) result;
                    //返回通知
                    logger.info("The method " + methodName + " end. result<" + resMap.get("code") + "---" + resMap.get("msg") + ">");
                } else if (result instanceof String) {
                    //返回通知
                    logger.info("The method " + methodName + " end. result<" + resMap + "--->");
                }
            }
        } catch (Throwable e) {
            logger.error(e, e);
            resMap = new HashMap();
            resMap.put("code", Constant.EXCEPTION_CODE);
            resMap.put("msg", "登录校验异常。" + e.getMessage());
            result = resMap;
        }
        //后置通知
        logger.debug("The method " + methodName + " end...");
        return result;
    }

    private Map doFilter(HttpServletRequest request, ProceedingJoinPoint point)
            throws IOException, ServletException {
        Map resMap = new HashMap();
        String requestUri = request.getRequestURI();
        String ipAddr = parseRequest(request).get("ipAddr");
        String xToken = SecurityCheck.getHeaderValue(request, "X-TOKEN");
        String filterUrl = PropertiesUtils.getProperty("FILTER_URL");
        String baseInfo = SecurityCheck.getHeaderValue(request, "baseInfo");
        logger.info("x-token:" + xToken);
        if ("/authorize/memberLogin".equals(requestUri) && MyStringUtils.isNotNull(xToken)) {
            logger.info("------------登录接口需要将xtoken设为空-------------------------------");
            xToken = null;
        }
        try {
            String name = null;
            String phone = null;
            String userId = null;
            Long date = null;
            boolean isHei = false;
            boolean tokenValid = false;
            if (MyStringUtils.isNotNull(xToken) && !xToken.equals("biaodaaTestToken")) {
                String[] token = xToken.split("\\.");
                if (token.length == 3) {
                    //新版token校验
                    if (verifyTokenVersion(xToken)) {
                        String[] sArray = xToken.split(Constant.TOKEN_SPLIT);
                        String paramJson = sArray[1];
                        String sign = sArray[2];
                        paramJson = Base64Decode(paramJson);
                        Map<String, String> paramMap = parseJsonString(paramJson);

                        VisitInfoHolder.setPermissions(paramMap.get("permissions"));
                        VisitInfoHolder.setRoleCode(paramMap.get("roleCode"));
                        name = paramMap.get("loginName");
                        phone = paramMap.get("phoneNo");
                        userId = paramMap.get("pkid");

                        String chanel = paramMap.get("channel");
                        date = Long.parseLong((paramMap.get("loginTime") != null && !paramMap.get("loginTime").equals("null")) ? paramMap.get("loginTime") : "0");
                        //单用户多渠道登录状态校验
                        if (!verifyLoginByChannel(userId, chanel, date)) {
                            resMap.put("code", Constant.ERR_VERIFY_USER_TOKEN);
                            resMap.put("msg", "多设备用户登录异常，请重新登录。");
                            return resMap;
                        }
                        //验证签名
                        tokenValid = SecurityCheck.checkSigner(paramMap, sign);
                    } else {
                        resMap.put("code", Constant.ERR_VERIFY_USER_TOKEN);
                        resMap.put("msg", relogin);
                        return resMap;
                    }
                } else {
                    logger.warn("非法token![token:" + token + "][ip:" + ipAddr + "]");
                    resMap.put("code", Constant.FAIL_CODE);
                    resMap.put("msg", errMsg);
                    return resMap;
                }

                VisitInfoHolder.setUserId(phone);
                VisitInfoHolder.setUid(userId);
            }
            if (StringUtils.isNotEmpty(baseInfo)) {
                logger.info("baseInfo:" + baseInfo);
                String[] baseInfos = baseInfo.split("\\|");
                VisitInfoHolder.setChannel(baseInfos[baseInfos.length - 1]);
            }

            logger.info("requestUri:" + requestUri);
            logger.info("aspect login:" + phone + "|" + userId);

            //是否疑似爬虫
            String blacklist = PropertiesUtils.getProperty("blacklist");
            if (null != blacklist && null != phone && blacklist.contains(phone)) {
                resMap.put("code", Constant.FAIL_CODE);
                resMap.put("msg", errMsg);
                return resMap;
            }
            //统计活跃用户
            if ("/foundation/version".equals(requestUri) && StringUtils.isNotEmpty(phone)) {
                loginInfoService.saveLoginInfo(name, phone, date);
            }
            //绿色通道检查
            boolean greenWay = greenWayVerify(requestUri, filterUrl, xToken);
            if (greenWay) {//无需校验token
                return null;
            } else {
                //是否疑似爬虫
                if (null != blacklist && null != phone && blacklist.contains(phone)) {
                    isHei = true;
                }
                if (tokenValid) {
                    if (isHei) {
                        resMap.put("code", Constant.FAIL_CODE);
                        resMap.put("msg", errMsg);
                        return resMap;
                    }
                } else {
                    resMap.put("code", Constant.ERR_VERIFY_USER_TOKEN);
                    resMap.put("msg", relogin);
                    return resMap;
                }
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
        return null;
    }


    public static final String relogin = "登录信息已过期，请重新登录！";
    public static final String errMsg = "您的账号疑似非法爬虫，现已冻结，如有疑问，请联系标大大客服(0731-85076077)";


    private void outPrintMsg(HttpServletResponse response, String Msg) throws IOException {
        response.setCharacterEncoding(Constant.STR_ENCODING);
        response.setContentType("application/json; charset=" + Constant.STR_ENCODING);
        PrintWriter out = response.getWriter();
        out.print(Msg);
    }

}
