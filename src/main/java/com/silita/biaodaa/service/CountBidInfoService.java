package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbCountBidInfoMapper;
import com.silita.biaodaa.dao.TbPlatformNoticeMapper;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.MyStringUtils;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    TbCountBidInfoMapper tbCountBidInfoMapper;
    @Autowired
    PlatformNoticeService platformNoticeService;
    @Autowired
    TbPlatformNoticeMapper tbPlatformNoticeMapper;

    public void timerCount(){
        //获取当前日期
        Date endDate = dateUtils.getToday();
        Date startDate  = dateUtils.getDayBefore(1);
        List<Date> dateList =  dateUtils.getDateList(startDate,endDate);
        if(null != dateList && dateList.size() > 0){
            for (Date de : dateList){
                countCompany(sdf.format(de),sdf.format(dateUtils.getLastDate(de)),1);
                platformNoticeService.insert(getContentTitle(sdf.format(de)));
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
            statDate = sdf.format(dateUtils.getToday());
        }
        Map<String,Object> param = new HashMap<>();
        param.put("statDate",statDate);
        List<Map<String,Object>> countList = tbCountBidInfoMapper.queryCountBidList(param);
        resultMap = tbPlatformNoticeMapper.queryPlatformInfoByStatDate(statDate);
        resultMap.put("list",countList);
        return resultMap;
    }

    public Map<String,Object> getContentTitle(String statDate){
        int count = tbCountBidInfoMapper.queryCountBidByStat(statDate);
        if(count <= 0){
            return null;
        }
        Map<String,Object> resultMap = new HashMap<>();
        Map<String,Object> param = new HashMap<>();
        //中标企业和中标总数量
        Map<String,Object> result = null;
        param.put("count",1);
        param.put("statDate",statDate);
        result = tbCountBidInfoMapper.queryCountBidNum(param);
        if(MapUtils.isEmpty(result)){
            return resultMap;
        }
        String companyCount = result.get("comNameCount").toString();
        String zhongCount = result.get("sumNum").toString();
        //中标6个以上的企业个数
        param.put("count",6);
        result  = tbCountBidInfoMapper.queryCountBidNum(param);
        String sixCount = result.get("comNameCount").toString();
        //中标3个以上的企业个数
        param.put("count",3);
        result  = tbCountBidInfoMapper.queryCountBidNum(param);
        String threeCount = result.get("comNameCount").toString();

        String content = PropertiesUtils.getProperty("com.content");
        content = content.replace("{date}",dateUtils.getTime(statDate,"yyyy-MM-dd","yyyy年MM月")).replace("{comCount}",companyCount).replace("{num}",zhongCount)
                .replace("{sixCount}",sixCount).replace("{threCount}",threeCount);
        resultMap.put("remark",content);

        //title
        String title = PropertiesUtils.getProperty("notice.title");
        title = title.replace("{date}",dateUtils.getTime(statDate,"yyyy-MM-dd","yyyy年MM月"));
        resultMap.put("title",title);
        resultMap.put("statDate",statDate);
        return resultMap;
    }

}
