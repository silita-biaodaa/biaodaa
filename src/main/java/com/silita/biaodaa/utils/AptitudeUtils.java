package com.silita.biaodaa.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dh on 2018/5/23.
 */
public class AptitudeUtils {

    /**
     * 解析三级资质（**及以上）的查询队列
     * @param threeCode
     * @return
     */
    public static Map<String ,List> parseThreeAptCode(String threeCode){
        Map<String ,List> resultMap = new HashMap<String ,List>(2);
        List hasAptList = null;
        List notHasAptList = null;
        if(MyStringUtils.isNotNull(threeCode)){
            if(threeCode.split("/").length==2) {
                hasAptList = new ArrayList(4);
                notHasAptList = new ArrayList(3);
                String uuid = threeCode.split("/")[0];
                String rank = threeCode.split("/")[1];
                switch(rank){
                    case "11":hasAptList.add(uuid+"/0");hasAptList.add(uuid+"/1");notHasAptList.add(uuid+"/2");notHasAptList.add(uuid+"/3");break;
                    case "21":hasAptList.add(uuid+"/0");hasAptList.add(uuid+"/1");hasAptList.add(uuid+"/2");notHasAptList.add(uuid+"/3");break;
                    case "31":hasAptList.add(uuid+"/0");hasAptList.add(uuid+"/1");hasAptList.add(uuid+"/2");hasAptList.add(uuid+"/3");break;
                    case "0":hasAptList.add(uuid+"/0");notHasAptList.add(uuid+"/1");notHasAptList.add(uuid+"/2");notHasAptList.add(uuid+"/3");break;
                    case "1":notHasAptList.add(uuid+"/0");hasAptList.add(uuid+"/1");notHasAptList.add(uuid+"/2");notHasAptList.add(uuid+"/3");break;
                    case "2":notHasAptList.add(uuid+"/0");notHasAptList.add(uuid+"/1");hasAptList.add(uuid+"/2");notHasAptList.add(uuid+"/3");break;
                    case "3":notHasAptList.add(uuid+"/0");notHasAptList.add(uuid+"/1");notHasAptList.add(uuid+"/2");hasAptList.add(uuid+"/3");break;
                    default:hasAptList.add(threeCode);
                }
            }else{
                hasAptList.add(threeCode);
            }
        }
        resultMap.put("hasAptList",hasAptList);
        resultMap.put("notHasAptList",notHasAptList);
        return resultMap;
    }

}
