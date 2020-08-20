package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.dao.TbPersonMapper;
import com.silita.biaodaa.dao.TbPersonQualificationMapper;
import com.silita.biaodaa.dao.TbUnderConstructMapper;
import com.silita.biaodaa.model.Page;
import com.silita.biaodaa.model.TbUnderConstruct;
import com.silita.biaodaa.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * tb_under_construct Service
 */
@Slf4j
@Service
public class UnderConstructService {

    @Autowired
    private TbUnderConstructMapper tbUnderConstructMapper;
    @Autowired
    private TbPersonQualificationMapper tbPersonQualificationMapper;
    @Autowired
    private TbPersonMapper tbPersonMapper;
    @Autowired
    private MyRedisTemplate myRedisTemplate;


    public PageInfo listUnderConstruct(Map<String, Object> param) {
        Integer pageNum = MapUtils.getInteger(param, "pageNo");
        Integer pageSize = MapUtils.getInteger(param, "pageSize");
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setCurrentPage(pageNum);

        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map<String, Object>> list = tbUnderConstructMapper.queryUnderConstructList(param);
        String idCard;
        for (int i = 0; i < list.size(); i++) {
            idCard = MapUtils.getString(list.get(i), "idCard");
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(idCard)) {
                list.get(i).put("idCard", idCard.replaceAll("(\\d{6})\\d{8}(\\w{4})", "$1***$2"));
            }
        }
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    public List getUnderConstruct(Map<String, Object> param) {
        List<TbUnderConstruct> list = new ArrayList<>();
        if (null != param.get("innerid")) {
            list = tbUnderConstructMapper.queryUnderListInnerid(param.get("innerid").toString());
        } else {
            //获取身份证id
            TbUnderConstruct underConstruct = tbUnderConstructMapper.queryUnderConstructDetail(param.get("pkid").toString());
//            list = tbUnderConstructMapper.queryUnderListIdCard(underConstruct.getIdCard());
            list.add(underConstruct);
        }
        if (null != list && list.size() > 0) {
            for (TbUnderConstruct under : list) {
                under.setIdCard(this.setIdCard(under));
                if (null != under.getInnerid()) {
                    under.setSex(tbPersonMapper.queryPersonSexInnerId(under.getInnerid()));
                }
            }
        }
        return list;
    }

    public List getApiUnderConstruct(Map<String, Object> param) {
        String idCard = MapUtils.getString(param, "idCard");
        Map<String, Object> sendParam = new HashedMap(4) {{
            put("idcard", idCard);
            put("method", "getSiteManager");
            put("pageSize", 1);
            put("pageIndex", 20);
        }};
        String url = PropertiesUtils.getProperty("api.build");
        Map<String, Object> token = setToken(url);
        String relUrl = MapUtils.getString(token, "url");
        String clickUrl = relUrl.replace("CARD", idCard);
        String result = HttpUtils.sendProxyUrl(clickUrl, sendParam, MapUtils.getString(token, "cookie").replace(":", "="));
        log.info(result);
        if (MyStringUtils.isNull(result)) {
            return new ArrayList();
        }
        Map resultMap = JsonUtils.json2Map(result);
        Boolean isSuccess = MapUtils.getBoolean(resultMap, "success");
        if (!isSuccess) {
            return new ArrayList();
        }
        Map<String, Object> data = (Map<String, Object>) resultMap.get("data");
        List<Map> mapList = (List<Map>) data.get("list");
        List<TbUnderConstruct> list = new ArrayList<>();
        TbUnderConstruct underConstruct;
        for (int i = 0; i < mapList.size(); i++) {
            if (!"1".equals(mapList.get(i).get("statusnum").toString())) {
                continue;
            }
            underConstruct = new TbUnderConstruct();
            underConstruct.setName(MapUtils.getString(mapList.get(i), "personname"));
            underConstruct.setDate(MapUtils.getString(mapList.get(i), "bdate"));
            underConstruct.setProName(MapUtils.getString(mapList.get(i), "prjname"));
            underConstruct.setType(MapUtils.getString(mapList.get(i), "dutytypename"));
            underConstruct.setIdCard(MapUtils.getString(mapList.get(i), "idcard"));
            underConstruct.setUnitOrg(MapUtils.getString(mapList.get(i), "corpname"));
            if (tbUnderConstructMapper.queryUnderCount(underConstruct) <= 0) {
                underConstruct.setPkid(VisitInfoHolder.getUUID());
//                this.getInnerid(underConstruct);
                tbUnderConstructMapper.insertUnderConstruct(underConstruct);
            } else {
                underConstruct.setPkid(tbUnderConstructMapper.queryUnderPkid(underConstruct));
            }
//            underConstruct.setIdCard(this.setIdCard(underConstruct));
            list.add(underConstruct);
        }
        return list;
    }

    /**
     * 设置身份证
     *
     * @param underConstruct
     */
    private String setIdCard(TbUnderConstruct underConstruct) {
        String idCard = underConstruct.getIdCard().replaceAll("(\\d{10})\\d{6}(\\w{2})", "$1******$2");
        return idCard;
    }

    /**
     * 获取innerid
     *
     * @param under
     */
    private void getInnerid(TbUnderConstruct under) {
        String idCard = setIdCard(under);
        Map<String, Object> param = new HashMap<String, Object>() {{
            put("name", under.getName());
            put("idCard", idCard);
            put("tableCode", RouteUtils.HUNAN_SOURCE);
        }};
        under.setInnerid(tbPersonQualificationMapper.queryPersonInnerid(param));
    }

    /**
     * 设置在建的token
     *
     * @param url
     * @return
     */
    private Map<String, Object> setToken(String url) {
        String token = myRedisTemplate.getString("build_token");
        if (org.apache.commons.lang3.StringUtils.isEmpty(token)) {
            return new HashedMap(1) {{
                put("url", url);
            }};
        }
        Map<String, Object> map = JSONObject.parseObject(token);
        String ver = MapUtils.getString(map, "verifyid");
        String modelX = MapUtils.getString(map, "moveX");
        String resUrl = url.replace("token", ver).replace("56", modelX);
        return new HashedMap(2) {{
            put("cookie", MapUtils.getString(map, "cookies"));
            put("url", resUrl);
        }};
    }
}
