package com.silita.biaodaa.common.WeChat.util;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/** 
 * 证书信任管理器（用于https请求） 
 *  
 * @author liufeng 
 * @date 2013-08-08 
 */  
public class MyX509TrustManager implements X509TrustManager {  
  
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }  
  
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }  
  
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;  
    }  
}  