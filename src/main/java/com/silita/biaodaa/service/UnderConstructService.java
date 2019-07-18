package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.dao.TbPersonMapper;
import com.silita.biaodaa.dao.TbPersonQualificationMapper;
import com.silita.biaodaa.dao.TbUnderConstructMapper;
import com.silita.biaodaa.model.Page;
import com.silita.biaodaa.model.TbUnderConstruct;
import com.silita.biaodaa.utils.*;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * tb_under_construct Service
 */
@Service
public class UnderConstructService {

    @Autowired
    TbUnderConstructMapper tbUnderConstructMapper;
    @Autowired
    TbPersonQualificationMapper tbPersonQualificationMapper;
    @Autowired
    TbPersonMapper tbPersonMapper;


    public PageInfo listUnderConstruct(Map<String, Object> param) {
        Integer pageNum = MapUtils.getInteger(param, "pageNo");
        Integer pageSize = MapUtils.getInteger(param, "pageSize");
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setCurrentPage(pageNum);

        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map<String, Object>> list = tbUnderConstructMapper.queryUnderConstructList(param);
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
        String url = PropertiesUtils.getProperty("api.build");
        String idCard = MapUtils.getString(param, "idCard");
        String clickUrl = url.replace("CARD", idCard);
        String result = HttpUtils.sendProxyGetUrl(clickUrl);
        if (MyStringUtils.isNull(result)) {
            return new ArrayList();
        }
        Map resultMap = JsonUtils.json2Map(result);
        Boolean isSuccess = MapUtils.getBoolean(resultMap, "success");
        if (!isSuccess) {
            return new ArrayList();
        }
        Map<String,Object> data = (Map<String, Object>) resultMap.get("data");
        List<Map> mapList = (List<Map>) data.get("list");
        List<TbUnderConstruct> list = new ArrayList<>();
        TbUnderConstruct underConstruct;
        for (int i = 0; i < mapList.size(); i++) {
            if (!"1".equals(mapList.get(i).get("statusnum").toString())){
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
            }else {
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
}
