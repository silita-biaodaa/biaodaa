package com.silita.biaodaa.bidCompute.filter;/**
 * Created by zhangxiahui on 16/9/13.
 */

import com.silita.biaodaa.bidCompute.BlockConfig;
import com.silita.biaodaa.bidCompute.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangxiahui
 * @version 1.0
 * @date 2016/09/13 下午3:20
 */
public abstract class BaseFilterHandler<T extends BlockConfig> implements Handler<T> {

    private static Logger logger = LoggerFactory.getLogger(BaseFilterHandler.class);

    public T config;

    protected Handler successor;



    @Override
    public void init(T config) {
        this.config = config;
    }

    @Override
    public void setSuccessor(Handler successor){
        this.successor = successor;
    }

    @Override
    public Map<String, Object> handlerRequest(Map resourceMap) {
        logger.info("==进入["+getFilterName()+"]过滤器===");
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("code",0);
        resultMap.put("msg","["+getFilterName()+"]过滤器,不满足条件.");
        //Double score = resourceMap.get("score");
        Double score = doHandler(resourceMap);

        if(successor==null){
            resultMap.put("code",1);
            return resultMap;
        }else{
            return successor.handlerRequest(resourceMap);
        }

    }

    abstract String getFilterName();

    abstract Double doHandler(Map resourceMap);
}
