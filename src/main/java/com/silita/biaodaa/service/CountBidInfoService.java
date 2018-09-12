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

    private static MyDateUtils dateUtils = new MyDateUtils();

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    TbCountBidInfoMapper tbCountBidInfoMapper;
    @Autowired
    PlatformNoticeService platformNoticeService;
    @Autowired
    TbPlatformNoticeMapper tbPlatformNoticeMapper;

    public void timerCount() {
        //获取当前日期
        Date endDate = dateUtils.getBeforeToday();
        Date startDate = dateUtils.getDayBefore(endDate, 1);
        List<Date> dateList = dateUtils.getDateList(startDate, endDate);
        Map<String, Object> param = new HashMap<>();
        param.put("countNum", 6);
        param.put("couNum", 3);
        if (null != dateList && dateList.size() > 0) {
            for (Date de : dateList) {
                countCompany(sdf.format(de), sdf.format(dateUtils.getLastDate(de)), 1);
                param.put("statDate", sdf.format(de));
                platformNoticeService.insert(getContentTitle(param));
//                System.out.println("start="+sdf.format(de)+"----end"+sdf.format(dateUtils.getLastDate(de)));
            }
        }
    }

    private void countCompany(String startDate, String endDate, Integer type) {
        Map<String, Object> param = new HashMap<>();
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("type", type);
        List<Map<String, Object>> bidCountList = tbCountBidInfoMapper.queryZhongbiaoCount(param);
        if (null != bidCountList && bidCountList.size() > 0) {
            tbCountBidInfoMapper.delZhongBiaoCountByDate(startDate);
            tbCountBidInfoMapper.batchInsertCountBinInfo(bidCountList);
        }
    }


    public Map<String, Object> listCountBid(Map<String,Object> valueMap) {
        Map<String, Object> resultMap = new HashMap<>();
        if (MyStringUtils.isNull(valueMap.get("statDate"))) {
            valueMap.put("statDate",sdf.format(dateUtils.getToday()));
        }
        String ressDate = MapUtils.getString(valueMap,"statDate");
        if(null != valueMap.get("ressDate")){
            ressDate = MapUtils.getString(valueMap,"ressDate");
        }
        Map<String, Object> param = new HashMap<>();
        param.put("statDate", valueMap.get("statDate"));
        if(null != valueMap.get("endDate")){
            param.put("endDate", valueMap.get("endDate"));
        }
        param.put("count", valueMap.get("count"));
        List<Map<String, Object>> countList = tbCountBidInfoMapper.queryCountBidList(param);
        resultMap = tbPlatformNoticeMapper.queryPlatformInfoByStatDate(ressDate);
        if (MapUtils.isEmpty(resultMap)) {
            resultMap = new HashMap<>();
        }
        resultMap.put("list", countList);
        return resultMap;
    }

    public Map<String, Object> getContentTitle(Map<String, Object> valueMap) {
        String statDate = MapUtils.getString(valueMap, "statDate");
        int count = tbCountBidInfoMapper.queryCountBidByStat(valueMap);
        if (count <= 0) {
            return null;
        }
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        //中标企业和中标总数量
        Map<String, Object> result = null;
        param.put("count", 1);
        param.put("statDate", statDate);
        if (null != valueMap.get("endDate")) {
            param.put("endDate", valueMap.get("endDate"));
        }
        result = tbCountBidInfoMapper.queryCountBidNum(param);
        if (MapUtils.isEmpty(result)) {
            return resultMap;
        }
        String companyCount = result.get("comNameCount").toString();
        String zhongCount = result.get("sumNum").toString();
        //中标countNum个以上的企业个数
        param.put("count", valueMap.get("countNum"));
        result = tbCountBidInfoMapper.queryCountBidNum(param);
        String sixCount = result.get("comNameCount").toString();
        //中标countNum个以上的企业个数
        param.put("count", valueMap.get("couNum"));
        result = tbCountBidInfoMapper.queryCountBidNum(param);
        String threeCount = result.get("comNameCount").toString();

        String content = PropertiesUtils.getProperty("com.content");
        //title
        String title = PropertiesUtils.getProperty("notice.title");
        if(null != valueMap.get("conTitle") && null != valueMap.get("releaseTime")){
            title = title.replace("{date}", valueMap.get("conTitle").toString());
            content = content.replace("{date}", valueMap.get("conTitle").toString()).replace("{comCount}", companyCount).replace("{num}", zhongCount)
                    .replace("{sixCount}", sixCount).replace("{threCount}", threeCount).replace("{countNum}", valueMap.get("countNum").toString()).replace("{couNum}", valueMap.get("couNum").toString());
            resultMap.put("title", title);
            resultMap.put("statDate", valueMap.get("releaseTime"));
            resultMap.put("remark", content);
            return resultMap;
        }
        title = title.replace("{date}", dateUtils.getTime(statDate, "yyyy-MM-dd", "yyyy年MM月"));
        resultMap.put("title", title);
        resultMap.put("statDate", statDate);
        resultMap.put("remark", content);
        return resultMap;
    }

    public void countDates(Map<String, Object> param) {
        String startDate = MapUtils.getString(param, "startDate");
        String endDate = MapUtils.getString(param, "endDate");
        String title = MyDateUtils.getTime(startDate, "yyyy-MM-dd", "yyyy年")
                + MyDateUtils.getTime(startDate, "yyyy-MM-dd", "M") + "-" + MyDateUtils.getTime(endDate, "yyyy-MM-dd", "M月");
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("statDate", startDate);
        valueMap.put("endDate", endDate);
        valueMap.put("conTitle", title);
        valueMap.put("releaseTime", startDate + "~" + endDate);
        valueMap.put("countNum",20);
        valueMap.put("couNum",10);
        platformNoticeService.insert(getContentTitle(valueMap));
    }

}
