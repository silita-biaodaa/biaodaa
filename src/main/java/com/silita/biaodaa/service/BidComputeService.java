package com.silita.biaodaa.service;

import com.silita.biaodaa.bidCompute.BlockConfig;
import com.silita.biaodaa.bidCompute.Handler;
import com.silita.biaodaa.utils.ApplicationContextHolder;
import com.silita.biaodaa.utils.ClassUtils;
import com.silita.biaodaa.utils.NameClassFinder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangxiahui on 18/7/3.
 */
@Service
public class BidComputeService {

    private static final Logger logger = LoggerFactory.getLogger(BidComputeService.class);

    public <C extends BlockConfig, T extends Handler<C>> Map<String, Object> computeHandler(Integer comId, List<Map<String, Object>> conditionBeans) throws Exception {

        // TODO: 18/7/3 通过企业ID获取所有该企业的获奖情况、安全认证、不良记录
        Map<String, Object> inResourceMap = new HashMap<>();



        Map<String, Object> resultMap = new HashMap<>();
        Integer re_code = 0;







        List<T> filterList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(conditionBeans)) {
            for (Map<String, Object> map : conditionBeans) {
                T filter = doHandler(map, "FilterHandler");
                filterList.add(filter);
            }
        }
        if (CollectionUtils.isNotEmpty(filterList)) {
            T beginFilter = filterList.get(0);
            for (int i = 1; i < filterList.size(); i++) {
                filterList.get(i - 1).setSuccessor(filterList.get(i));
            }
            resultMap = beginFilter.handlerRequest(inResourceMap);
            re_code = MapUtils.getInteger(resultMap, "code");
            if (re_code != null && re_code == 0) {
                //outResourceMap.put("msg", MapUtils.getString(resultMap, "msg"));
                //return outResourceMap;
            }
        }
        return null;

    }

    public <C extends BlockConfig, T extends Handler<C>> T doHandler(Map<String, Object> map, String className) {
        String benefitCode = MapUtils.getString(map, "code");
        Class<C> configClass = NameClassFinder.getInstance().get(benefitCode + "Bean");
        C config = ClassUtils.newInstance(configClass);
        config.init(map);

        Class<T> handlerClass = NameClassFinder.getInstance().get(benefitCode + className);
        T handler = ClassUtils.newInstance(handlerClass);
        ApplicationContextHolder.getApplicationContext().getAutowireCapableBeanFactory().autowireBean(handler);
        handler.init(config);
        return handler;
    }


}
