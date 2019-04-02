package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.dao.TbCountBidInfoMapper;
import com.silita.biaodaa.dao.TbPlatformNoticeMapper;
import com.silita.biaodaa.model.es.CompanyEs;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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
    @Autowired
    TbCompanyMapper tbCompanyMapper;
    @Autowired
    ReputationComputerService reputationComputerService;

    public void timerCount() {
        String date = MyDateUtils.getDate("yyyy-MM-dd");
        tbCountBidInfoMapper.delZhongBiaoCountByDate(date);
        //查询公司
        Map<String, Object> param = new HashedMap() {{
            put("regisAddress", "湖南省");
            put("pageSize", 1000);
        }};
        int total = tbCompanyMapper.queryCompanyAddressCount(param);
        if (total <= 0) {
            return;
        }
        Integer pages = getPage(total, 1000);
        for (int i = 1; i <= pages; i++) {
            param.put("page", (i - 1) * 1000);
            List<CompanyEs> list = tbCompanyMapper.queryCompanyEsList(param);
            for (CompanyEs es : list) {
                //建筑工程
                this.countCompany(es, "建筑工程", 2, date);
                //市政工程
                this.countCompany(es, "市政", 3, date);
            }
        }
        this.addPlatform(date);
    }

    private void countCompany(CompanyEs es, String projType, Integer type, String date) {
        Map<String, Object> param = new HashedMap() {{
            put("comName", es.getComName());
            put("statDate", date);
            put("comId", es.getComId());
            put("projType", projType);
        }};
        Map<String, Object> result = reputationComputerService.computer(param);
        param.put("score", MapUtils.getDouble(result, "score"));
        int count = 0;
        if (null != result.get("gjhj")) {
            count = count + ((List) result.get("gjhj")).size();
        }
        Map<String, Object> sjhj = (Map<String, Object>) result.get("sjhj");
        if (null != sjhj.get("reviewProject")) {
            count = count + ((List) sjhj.get("reviewProject")).size();
        }
        if (null != sjhj.get("aqrz")) {
            count++;
        }
        if (null != sjhj.get("reviewCompany")) {
            count++;
        }
        if (null != sjhj.get("awards")) {
            count = count + ((List) sjhj.get("awards")).size();
        }
        param.put("num", count);
        param.put("type", type);
        tbCountBidInfoMapper.insertCountBinInfo(param);
    }


    public Map<String, Object> listCountBid(Map<String, Object> valueMap) {
        Map<String, Object> resultMap = new HashMap<>();
        if (MyStringUtils.isNull(valueMap.get("statDate"))) {
            valueMap.put("statDate", sdf.format(dateUtils.getToday()));
        }
        String ressDate = MapUtils.getString(valueMap, "statDate");
        if (null != valueMap.get("ressDate")) {
            ressDate = MapUtils.getString(valueMap, "ressDate");
        }
        Map<String, Object> param = new HashMap<>();
        param.put("statDate", valueMap.get("statDate"));
        if (null != valueMap.get("endDate")) {
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

    public Map<String,Object> listCountBidReq(Map<String, Object> valueMap){
        List<Map<String, Object>> countList = tbCountBidInfoMapper.queryCountBidReq(valueMap);
        Map<String, Object> resultMap = tbPlatformNoticeMapper.queryPlatformInfoByParam(valueMap);
        if (MapUtils.isEmpty(resultMap)){
            resultMap = new HashedMap();
        }
        resultMap.put("list",countList);
        return resultMap;
    }

    public void addPlatform(String date) {
        Map<String, Object> param = new HashedMap();
        param.put("date", date);
        param.put("type", 2);
        param.put("statDate",date);
        List<String> list = tbCountBidInfoMapper.queryRequCompany(param);
        String title = "湖南省2019年建筑工程信誉计分排行榜";
        String content = "根据湖南省住房和城乡建设厅发布的湘建监督[2018]238号文中的信誉计分规则，进行排名统计；其中" + list.get(0) + "、" + list.get(1) + "、" + list.get(2) + "分列信誉计分排行榜前三名。";
        param.put("title",title);
        param.put("remark",content);
        platformNoticeService.insert(param);
        param.put("type", 3);
        list = tbCountBidInfoMapper.queryRequCompany(param);
        title = "湖南省2019年市政工程信誉计分排行榜";
        content = "根据湖南省住房和城乡建设厅发布的湘建监督[2018]238号文中的信誉计分规则，进行排名统计；其中" + list.get(0) + "、" + list.get(1) + "、" + list.get(2) + "分列信誉计分排行榜前三名。";
        param.put("title",title);
        param.put("remark",content);
        platformNoticeService.insert(param);
    }

    private static Integer getPage(Integer total, Integer pageSize) {
        Integer pages = 0;
        if (total % pageSize == 0) {
            pages = total / pageSize;
        } else {
            pages = (total / pageSize) + 1;
        }
        return pages;
    }

}
