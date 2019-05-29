package com.silita.biaodaa.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by zhangxiahui on 17/7/26.
 */
public class VisitInfoHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(VisitInfoHolder.class);

    protected static final ThreadLocal<String> userId = new ThreadLocal<>();

    protected static final  ThreadLocal<String> uid = new ThreadLocal<>();

    protected static final ThreadLocal<String> role_code = new ThreadLocal<>();

    protected static final ThreadLocal<String> permissions = new ThreadLocal<>();

    protected static final ThreadLocal<String> channel = new ThreadLocal<>();


    public static void setRoleCode(String roleCode){
        VisitInfoHolder.role_code.set(roleCode);
    }

    public static void setPermissions(String permissions){
        VisitInfoHolder.permissions.set(permissions);
    }

    public static void setChannel(String channel){
        VisitInfoHolder.channel.set(channel);
    }

    public static String getRoleCode(){
        return VisitInfoHolder.role_code.get();
    }

    public static String getPermissions(){
        return VisitInfoHolder.permissions.get();
    }

    public static String getChannel(){
        return VisitInfoHolder.channel.get();
    }

    public static String getUserId() {
        return VisitInfoHolder.userId.get();
    }

    public static void setUserId(String userId) {
        VisitInfoHolder.userId.set(userId);
    }

    public static String getUid() {
        return VisitInfoHolder.uid.get();
    }

    public static void setUid(String uid) {
        VisitInfoHolder.uid.set(uid);
    }

    public synchronized static String getUUID() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return uuid;
    }


}
