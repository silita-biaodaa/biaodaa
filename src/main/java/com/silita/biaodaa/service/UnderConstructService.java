package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.TbUnderConstructMapper;
import com.silita.biaodaa.model.TbUnderConstruct;
import com.silita.biaodaa.utils.*;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * tb_under_construct Service
 */
@Service
public class UnderConstructService {

    @Autowired
    TbUnderConstructMapper tbUnderConstructMapper;

    private static DBCollection dbCollection = MongoUtils.init(PropertiesUtils.getProperty("mongodb.ip"), PropertiesUtils.getProperty("mongodb.host"), "mycol").getDB().getCollection("person");

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
        List<TbUnderConstruct> list = tbUnderConstructMapper.queryUnderListInnerid(param.get("innerid").toString());
        if (null != list && list.size() > 0) {
            for (TbUnderConstruct under : list) {
                this.setIdCard(under);
            }
        }
        return list;
    }

    public List getApiUnderConstruct(Map<String, Object> param) {
        String url = PropertiesUtils.getProperty("api.build");
        String opt = PropertiesUtils.getProperty("api.opt");
        String name = MapUtils.getString(param, "name");
        String idCard = MapUtils.getString(param, "idCard");
        String clictUrl = null;
        try {
            clictUrl = url + "?opt=" + opt + "&name=" + URLDecoder.decode(name,"UTF-8");
            if (!MyStringUtils.isNull(idCard)) {
                clictUrl = clictUrl + "&sfz=" + idCard;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = HttpUtils.sendGetUrl(clictUrl);
        if(MyStringUtils.isNull(result)){
            return new ArrayList();
        }
        Map resultMap = JsonUtils.json2Map(result);
        Boolean isSuccess = MapUtils.getBoolean(resultMap, "strSuccess");
        if (!isSuccess) {
            return new ArrayList();
        }
        List<Map> mapList = (List<Map>) resultMap.get("data");
        List<TbUnderConstruct> list = new ArrayList<>();
        TbUnderConstruct underConstruct;
        for (int i = 0; i < mapList.size(); i++) {
            underConstruct = new TbUnderConstruct();
            underConstruct.setName(MapUtils.getString(mapList.get(i), "姓名"));
            underConstruct.setDate(MapUtils.getString(mapList.get(i), "押证日期"));
            underConstruct.setCity(MapUtils.getString(mapList.get(i), "所在市州"));
            underConstruct.setCounty(MapUtils.getString(mapList.get(i), "所在县区"));
            underConstruct.setProName(MapUtils.getString(mapList.get(i), "工程名称"));
            underConstruct.setType(MapUtils.getString(mapList.get(i), "人员类别"));
            underConstruct.setIdCard(MapUtils.getString(mapList.get(i), "身份证"));
            underConstruct.setProOrg(MapUtils.getString(mapList.get(i), "建设单位"));
            underConstruct.setUnitOrg(MapUtils.getString(mapList.get(i), "单位名称"));
            if (tbUnderConstructMapper.queryUnderCount(underConstruct) <= 0) {
                underConstruct.setPkid(VisitInfoHolder.getUUID());
                this.setIdCard(underConstruct);
                tbUnderConstructMapper.insertUnderConstruct(underConstruct);
            }
            this.setIdCard(underConstruct);
            list.add(underConstruct);
        }
        return list;
    }

    /**
     * 设置身份证
     *
     * @param underConstruct
     */
    private void setIdCard(TbUnderConstruct underConstruct) {
        underConstruct.setIdCard(underConstruct.getIdCard().replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1*****$2"));
    }

    /**
     * 获取innerid
     * @param under
     */
    private void getInnerid(TbUnderConstruct under){
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("name", under.getName());
        basicDBObject.put("id_card", under.getIdCard());
        DBObject dbObject = dbCollection.findOne(basicDBObject);
        if (null != dbObject) {
            Map map = dbObject.toMap();
            TbUnderConstruct underConstruct = new TbUnderConstruct();
            underConstruct.setPkid(under.getPkid());
            underConstruct.setInnerid("00" + map.get("innerid").toString());
        }
    }
}
