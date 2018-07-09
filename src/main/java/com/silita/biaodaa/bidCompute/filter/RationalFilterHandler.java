package com.silita.biaodaa.bidCompute.filter;

import com.silita.biaodaa.bidCompute.condition.RationalBean;
import com.silita.biaodaa.dao.BidEvaluationMethodMapper;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.utils.DoubleUtils;
import com.silita.biaodaa.utils.Name;
import com.silita.biaodaa.utils.ProjectAnalysisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhushuai on 18/7/3.
 */
@Name
@Component
public class RationalFilterHandler extends BaseFilterHandler<RationalBean> {

    private static final Logger logger = LoggerFactory.getLogger(RationalFilterHandler.class);

    @Autowired
    BidEvaluationMethodMapper bidEvaluationMethodMapper;

    @Override
    String getFilterName() {
        return "信用等级";
    }

    // TODO: 18/7/3 处理资源数据
    @Override
    Double doHandler(Map resourceMap) {
        Map<String, Object> comMap = (Map<String, Object>) resourceMap.get("comInfo");
        Map<String, Object> param = new HashMap<>();
        param.put("list", comMap.get("srcUuidList"));

        // TODO: 开始计算
        Map<String, Object> reaMap = null;
        Double yearTotal = 0D;
        Double oneYearTotal = 0D;
        Double twoYearTotal = 0D;
        //获取最新年的等级
        param.put("near", ProjectAnalysisUtil.getStrNumber(this.config.getYear()));
        reaMap = bidEvaluationMethodMapper.queryCertGrade(param);
        if (null != reaMap) {
            try {
                yearTotal = this.getTotal(reaMap);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //获取前一年
        param.put("near", ProjectAnalysisUtil.getStrNumber(this.config.getLastOneYear()));
        reaMap = bidEvaluationMethodMapper.queryCertGrade(param);
        if (null != reaMap) {
            try {
                oneYearTotal = this.getTotal(reaMap);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //获取前两年
        param.put("near", ProjectAnalysisUtil.getStrNumber(this.config.getLastTwoYear()));
        reaMap = bidEvaluationMethodMapper.queryCertGrade(param);
        if (null != reaMap) {
            try {
                twoYearTotal = this.getTotal(reaMap);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Double total = DoubleUtils.add(yearTotal,oneYearTotal,twoYearTotal);
        resourceMap.put("total",total);
        return total;
    }

    // TODO:获取分值
    private Double getTotal(Map<String, Object> reaMap) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date d = sdf.parse(reaMap.get("near").toString());
        Map<String,Object> scoreMap = getScoreMap();
        Map<String,Object> socMap = (Map<String, Object>) scoreMap.get(sdf.format(d));
        Double total = (Double) socMap.get(reaMap.get("grade").toString());
        return total;
    }

    private Map<String,Object> getScoreMap(){
        Map<String,Object> yearMap = new HashMap<>();
        Map<String,Object> djMap = new HashMap<>();
        djMap.put("AA",this.config.getYearAAScore());
        djMap.put("A",this.config.getYearAScore());
        djMap.put("B",this.config.getYearBScore());
        djMap.put("C",this.config.getYearCScore());
        yearMap.put(ProjectAnalysisUtil.getStrNumber(this.config.getYear()),djMap);
        djMap = new HashMap<>();
        djMap.put("AA",this.config.getOneYearAAScore());
        djMap.put("A",this.config.getOneYearAScore());
        djMap.put("B",this.config.getOneYearBScore());
        djMap.put("C",this.config.getOneYearCScore());
        yearMap.put(ProjectAnalysisUtil.getStrNumber(this.config.getLastOneYear()),djMap);
        djMap = new HashMap<>();
        djMap.put("AA",this.config.getTwoYearAAScore());
        djMap.put("A",this.config.getTwoYearAScore());
        djMap.put("B",this.config.getTwoYearBScore());
        djMap.put("C",this.config.getTwoYearCScore());
        yearMap.put(ProjectAnalysisUtil.getStrNumber(this.config.getLastTwoYear()),djMap);
        return yearMap;
    }
}
