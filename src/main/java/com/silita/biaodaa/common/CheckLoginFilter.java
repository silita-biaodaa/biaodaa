package com.silita.biaodaa.common;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

/**
 * Created by zhangxiahui on 17/7/26.
 */
public class CheckLoginFilter implements Filter {

    private static Logger LOGGER = LoggerFactory.getLogger(CheckLoginFilter.class);

    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestUri = request.getRequestURI();
        String xToken = null ;
        xToken = SecurityCheck.getHeaderValue(request,"X-TOKEN");


        String sign = null;
        String name = null;
        String pwd = null;
        String showName = null;
        String userId = null;
        Map<String, String> parameters = new HashedMap();
        if(xToken!=null){
            String [] token = xToken.split("\\.");
            if(token.length==2){
                sign = token[0];
                String json = new String(Base64.getDecoder().decode(token[1]),"utf-8");
                JSONObject jsonObject = (JSONObject) JSONObject.parse(json);
                name = jsonObject.getString("name");
                pwd = jsonObject.getString("pwd");
                showName = jsonObject.getString("showName");
                userId = jsonObject.getString("userId");
                parameters.put("name", name);
                parameters.put("pwd", pwd);
                parameters.put("showName", showName);
                parameters.put("userId",userId);
            }
        }
        VisitInfoHolder.setUserId(showName);
        VisitInfoHolder.setUid(userId);

        //FILTER_URL
        String filterUrl = PropertiesUtils.getProperty("FILTER_URL");
        boolean boo = false;
        if(filterUrl!=null){
            String[] urls  = filterUrl.split(",");
            for(String url : urls){
                if(requestUri.contains(url)){
                    boo = true;
                    break;
                }
            }
        }
        if(requestUri.equals("/")){
            boo = true;
        }
        if(boo){
            filterChain.doFilter(servletRequest, servletResponse);
        }else if("biaodaaTestToken".equals(xToken)){
            LOGGER.debug("进入登录系统Filter,开发者进入系统");
            filterChain.doFilter(servletRequest, servletResponse);
        }else if(SecurityCheck.checkSigner(parameters,sign)){//如果有签名先校验签名
            filterChain.doFilter(servletRequest, servletResponse);
        }else{
            //LOGGER.info("进入登录系统Filter,["+requestUri+"]["+xToken+"]无权限");
            request.setAttribute("msg", "您登录未授权");
            request.getRequestDispatcher("/fail.jsp").forward(request, response);
        }

    }

    public void destroy() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }
}
