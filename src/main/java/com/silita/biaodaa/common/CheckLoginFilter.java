package com.silita.biaodaa.common;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.service.LoginInfoService;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.Map;

/**
 * Created by zhangxiahui on 17/7/26.
 */
public class CheckLoginFilter implements Filter {

    LoginInfoService loginInfoService;

    private static Logger LOGGER = LoggerFactory.getLogger(CheckLoginFilter.class);

    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestUri = request.getRequestURI();
        String xToken = null;
        xToken = SecurityCheck.getHeaderValue(request, "X-TOKEN");


        String sign = null;
        String name = null;
        String password = null;
        String phone = null;
        String userId = null;
        Map<String, String> parameters = new HashedMap();
        boolean isHei = false;
        if (xToken != null) {
            String[] token = xToken.split("\\.");
            if (token.length == 2) {
                sign = token[0];
                String json = new String(Base64.getDecoder().decode(token[1]), "utf-8");
                JSONObject jsonObject = (JSONObject) JSONObject.parse(json);
                name = jsonObject.getString("name");
                password = jsonObject.getString("password");
                phone = jsonObject.getString("phone");
                userId = jsonObject.getString("userId");
                parameters.put("name", name);
                parameters.put("password", password);
                parameters.put("phone", phone);
                String blacklist = PropertiesUtils.getProperty("blacklist");
                if (blacklist.contains(phone)) {
                    isHei = true;
                }
                parameters.put("userId", userId);
                parameters.put("date", jsonObject.getString("date"));
                if ("/foundation/version".equals(requestUri)) {
                    loginInfoService.saveLoginInfo(jsonObject);
                }
            }
        }
        VisitInfoHolder.setUserId(phone);
        VisitInfoHolder.setUid(userId);


        //FILTER_URL
        String filterUrl = PropertiesUtils.getProperty("FILTER_URL");
        boolean boo = false;
        if (filterUrl != null) {
            String[] urls = filterUrl.split(",");
            for (String url : urls) {
                if (requestUri.contains(url)) {
                    boo = true;
                    break;
                }
            }
        }
        if (requestUri.equals("/")) {
            boo = true;
        }
        if (boo) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else if ("biaodaaTestToken".equals(xToken)) {
            LOGGER.debug("进入登录系统Filter,开发者进入系统");
            filterChain.doFilter(servletRequest, servletResponse);
        } else if (SecurityCheck.checkSigner(parameters, sign)) {//如果有签名先校验签名
            if (isHei) {
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter out = response.getWriter();
                out.print("{\"code\":0,\"msg\":\"您的账号疑似非法爬虫，现已冻结，如有疑问，请联系标大大客服(0731-85076077)\"}");
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } else {
            //LOGGER.info("进入登录系统Filter,["+requestUri+"]["+xToken+"]无权限");
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = response.getWriter();
            out.print("{\"code\":0,\"msg\":\"没权限\"}");
        }

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
