package com.silita.biaodaa.bidCompute.filter;

import com.silita.biaodaa.bidCompute.condition.UndesirableBean;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.utils.DoubleUtils;
import com.silita.biaodaa.utils.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by zhushuai on 18/7/3.
 */
@Name
@Component
public class UndesirableFilterHandler extends BaseFilterHandler<UndesirableBean> {

    private static final Logger logger = LoggerFactory.getLogger(UndesirableFilterHandler.class);

    @Autowired
    TbCompanyMapper tbCompanyMapper;

    @Override
    String getFilterName() {
        return "不良行为";
    }

    // 18/7/3 处理资源数据
    @Override
    Double doHandler(Map resourceMap) {
        Map<String,Object> comMap = (Map<String, Object>) resourceMap.get("comInfo");
        Map<String,Object> param = new HashMap<>();
        param.put("list",comMap.get("srcUuidList"));
        Double total = 0D;
        List<Map<String,Object>> undesList = tbCompanyMapper.getUndesirable(param);
        if(null != undesList && undesList.size() > 0){
            List<Map<String,Object>> undes = new ArrayList<>();
            for(Map<String,Object> map : undesList){
                if("一般".equals(map.get("nature").toString())){
                    if(this.config.getCommScore() > 0){
                        undes.add(map);
                    }
                }else if ("严重".equals(map.get("nature").toString())){
                    if(this.config.getSevScore() > 0){
                        undes.add(map);
                    }
                }
            }
            if(null != resourceMap.get("total")){
                total = (Double) resourceMap.get("total");
            }
            if(null != undes && undes.size() > 0){
                total = DoubleUtils.add(total,this.getTotal(undes),0D);
            }
            resourceMap.put("total",total);
            resourceMap.put("undesList",undes);
        }
        return total;
    }

    // 获取总数分值
    private Double getTotal(List<Map<String,Object>> undesList){
        Double total = 0D;
        Double comTotal = 0D;
        Double sevTotal = 0D;
        Integer comCount = 0;
        Integer sveCount = 0;
        for (Map<String,Object> undes : undesList){
            if("一般".equals(undes.get("nature").toString())){
                comCount++;
            }else{
                sveCount++;
            }
        }
        comTotal = DoubleUtils.mul(this.config.getCommScore(),Double.valueOf(comCount.toString()));
        sevTotal = DoubleUtils.mul(this.config.getSevScore(),Double.valueOf(sveCount.toString()));
        total = DoubleUtils.add(comTotal,sevTotal,0D);
        return total;
    }
}
