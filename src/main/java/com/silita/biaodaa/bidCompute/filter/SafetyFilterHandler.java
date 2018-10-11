package com.silita.biaodaa.bidCompute.filter;

import com.silita.biaodaa.bidCompute.condition.PrizeBean;
import com.silita.biaodaa.bidCompute.condition.SafetyBean;
import com.silita.biaodaa.dao.BidEvaluationMethodMapper;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.utils.DoubleUtils;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.Name;
import com.silita.biaodaa.utils.ProjectAnalysisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhushuai on 18/7/3.
 */
@Name
@Component
public class SafetyFilterHandler extends BaseFilterHandler<SafetyBean> {

    private static final Logger logger = LoggerFactory.getLogger(SafetyFilterHandler.class);

    @Autowired
    BidEvaluationMethodMapper bidEvaluationMethodMapper;

    @Override
    String getFilterName() {
        return "安全认证";
    }

    // 18/7/3 处理资源数据
    @Override
    Double doHandler(Map resourceMap) {
        Map<String,Object> comMap = (Map<String, Object>) resourceMap.get("comInfo");
        Map<String,Object> param = new HashMap<>();
        param.put("list",comMap.get("srcUuidList"));
        List<Map<String,Object>> safetyList = bidEvaluationMethodMapper.quaryCertAqrz(param);
        Double total = 0D;
        if(null != safetyList && safetyList.size() > 0){
            Map<String,Object> scoreMap = this.getSafetyScore();
            // 去重
            if(safetyList.size() > 1){
                this.getDeWeight(safetyList,scoreMap);
            }
            if(null != resourceMap.get("total")){
                total = (Double) resourceMap.get("total");
            }
            Map<String,Object> safetyMap = safetyList.get(0);
            Double d = (Double) scoreMap.get(safetyMap.get("mateName").toString());
            if(d > 0){
                resourceMap.put("safety",safetyMap);
            }
            total = DoubleUtils.add(total,d,0D);
            resourceMap.put("total",total);
        }
        return total;
    }

    // 存储分值
    private Map<String,Object> getSafetyScore(){
        Map<String,Object> scoreMap = new HashMap<>();
        scoreMap.put("省级优秀",this.config.getProvinceExcell());
        scoreMap.put("省级合格",this.config.getProvinceQual());
        scoreMap.put("市级优秀",this.config.getCityExcell());
        scoreMap.put("市级合格",this.config.getCityQual());
        return  scoreMap;
    }

    // 去重
    private void getDeWeight(List<Map<String,Object>> safetyList,Map<String,Object> scoreMap){
        List<Map<String,Object>> screList = new ArrayList<>();
        Map<String,Object> sMap = null;
        for(Map<String,Object> map : safetyList){
            sMap = new HashMap<>();
            map.put("mateName",map.get("mateName").toString());
            map.put("score",scoreMap.get(map.get("mateName").toString()));
            screList.add(sMap);
        }

        //排序
        Collections.sort(screList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Double d1 = (Double) o1.get("score");
                Double d2 = (Double) o2.get("score");
                return d2.compareTo(d1);
            }
        });

        Map<String,Object> scoMap = screList.get(0);
        Iterator<Map<String,Object>> it = safetyList.iterator();
        while (it.hasNext()){
            Map<String,Object> safMap = it.next();
            if(!scoMap.get("mateName").toString().equals(safMap.get("mateName").toString())){
                it.remove();
            }
        }
    }
}
