package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbCountBidInfoMapper;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.MyStringUtils;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计
 */
@Service
//@Transactional
public class CountBidInfoService {

    private  static MyDateUtils dateUtils = new MyDateUtils();

    @Autowired
    TbCountBidInfoMapper tbCountBidInfoMapper;

    public void timerCount(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日期
        Date endDate = dateUtils.getToday();
        Date startDate  = dateUtils.getDayBefore(1);
        List<Date> dateList =  dateUtils.getDateList(startDate,endDate);
        if(null != dateList && dateList.size() > 0){
            for (Date de : dateList){
                countCompany(sdf.format(de),sdf.format(dateUtils.getLastDate(de)),1);
//                System.out.println("start="+sdf.format(de)+"----end"+sdf.format(dateUtils.getLastDate(de)));
            }
        }
    }

    private void countCompany(String startDate,String endDate,Integer type){
        Map<String,Object> param = new HashMap<>();
        param.put("startDate",startDate);
        param.put("endDate",endDate);
        param.put("type",type);
        List<Map<String,Object>> bidCountList = tbCountBidInfoMapper.queryZhongbiaoCount(param);
        if(null != bidCountList && bidCountList.size() > 0){
            tbCountBidInfoMapper.delZhongBiaoCountByDate(startDate);
            tbCountBidInfoMapper.batchInsertCountBinInfo(bidCountList);
        }
    }


    public Map<String,Object> listCountBid(String statDate){
        Map<String,Object> resultMap = new HashMap<>();
        if(MyStringUtils.isNull(statDate)){
            statDate =dateUtils.getTime("yyyy-MM-dd");
        }
        Map<String,Object> param = new HashMap<>();
        param.put("statDate",statDate);
        List<Map<String,Object>> countList = tbCountBidInfoMapper.queryCountBidList(param);
        resultMap.put("list",countList);
        //中标企业和中标总数量
        Map<String,Object> result = null;
        param.put("count",1);
        result = tbCountBidInfoMapper.queryCountBidNum(param);
        if(MapUtils.isEmpty(result)){
            return resultMap;
        }
        String province = "湖南省";
        String companyCount = result.get("comNameCount").toString();
        String zhongCount = result.get("sumNum").toString();
        //中标6个以上的企业个数
        param.put("count",6);
        result  = tbCountBidInfoMapper.queryCountBidNum(param);
        String numCount = result.get("sumNum").toString();
        //中标3个以上的企业个数
        param.put("count",3);
        result  = tbCountBidInfoMapper.queryCountBidNum(param);
        String numtCount = result.get("sumNum").toString();

        String content = PropertiesUtils.getProperty("com.content");
        content = content.replace("{province}",province).replace("{companyCount}",companyCount).replace("{zhongCount}",zhongCount)
                .replace("{num}","6").replace("{numCount}",numCount).replace("{numt}","3")
                .replace("{numtCount}",numtCount);
        resultMap.put("content",content);
        return resultMap;
    }

}
