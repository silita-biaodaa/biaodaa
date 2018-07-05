package com.silita.biaodaa.service;

import com.silita.biaodaa.bidCompute.BlockConfig;
import com.silita.biaodaa.bidCompute.Handler;
import com.silita.biaodaa.dao.BidEvaluationMethodMapper;
import com.silita.biaodaa.utils.ApplicationContextHolder;
import com.silita.biaodaa.utils.ClassUtils;
import com.silita.biaodaa.utils.NameClassFinder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    BidEvaluationMethodMapper bidEvaluationMethodMapper;

    private static final Logger logger = LoggerFactory.getLogger(BidComputeService.class);

    public <C extends BlockConfig, T extends Handler<C>> Map<String, Object> computeHandler(Map<String,Object> comMap, List<Map<String, Object>> conditionBeans) throws Exception {

        Map<String, Object> inResourceMap = new HashMap<>();

        inResourceMap.put("comInfo",comMap);

        Map<String, Object> resultMap = new HashMap<>();
//        Integer re_code = 0;

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
            return resultMap;
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
