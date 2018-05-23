package com.silita.biaodaa.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 2018/5/23.
 */
public class AptitudeUtils {

    /**
     * 解析三级资质（**及以上）的查询队列
     * @param threeCode
     * @return
     */
    public static List parseThreeAptCode(String threeCode){
        List aptList = null;
        if(MyStringUtils.isNotNull(threeCode)){
            aptList = new ArrayList(4);
            if(threeCode.split("/").length==2) {
                String uuid = threeCode.split("/")[0];
                String rank = threeCode.split("/")[1];
                switch(rank){
                    case "11":aptList.add(uuid+"/0");aptList.add(uuid+"/1");break;
                    case "21":aptList.add(uuid+"/0");aptList.add(uuid+"/1");aptList.add(uuid+"/2");break;
                    case "31":aptList.add(uuid+"/0");aptList.add(uuid+"/1");aptList.add(uuid+"/2");aptList.add(uuid+"/3");break;
                    default:aptList.add(threeCode);
                }
            }else{
                aptList.add(threeCode);
            }
        }
        return aptList;
    }

}
