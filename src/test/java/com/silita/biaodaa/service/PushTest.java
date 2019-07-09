package com.silita.biaodaa.service;

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
        users.add("86f5832250234cadaf66f35de8bb0579");
        Map<String,Object> param = new HashedMap();
        param.put("comName","湖南省第五工程有限公司");
        param.put("comId","5d86f82e66452e2db067e42ca327c629");
        pushMessage(param,users);
    }

}
