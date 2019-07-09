package com.silita.biaodaa.service;

import com.silita.biaodaa.utils.CommonUtil;
import com.silita.biaodaa.utils.PushMessageUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhushuai on 2019/7/9.
 */
public class PushTest{

    public void pushMessage(Map<String, Object> param, List<String> users) {
        String comName = MapUtils.getString(param, "comName");
        for (String user : users) {
            PushMessageUtils.pushMessage(user, param, "企业数据更新已完成", comName + "的工商信息已更新完成！", "company");
        }
    }
    @org.junit.Test
    public void test(){
        List<String> users = new ArrayList<>();
        users.add("fefc9ce907e346768773cf34efe601df");
        Map<String,Object> param = new HashedMap();
        param.put("comName","湖南东方红建设集团有限公司");
        param.put("comId","44b2a270d4088a997366d5febdf24b21");
        pushMessage(param,users);
    }

    @org.junit.Test
    public void test1(){
        System.out.println( CommonUtil.getUUID());
    }

}
