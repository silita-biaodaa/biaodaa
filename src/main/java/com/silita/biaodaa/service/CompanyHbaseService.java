package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.dao.TbGsCompanyMapper;
import com.silita.biaodaa.model.TbGsCompany;
import com.silita.biaodaa.utils.HBaseUtils;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * 工商数据
 * Created by zhushuai on 2019/6/28.
 */
@Service
public class CompanyHbaseService {

    private static Logger logger = LoggerFactory.getLogger(CompanyHbaseService.class);

    @Autowired
    TbGsCompanyMapper tbGsCompanyMapper;


    /**
     * 解析并保存工商
     *
     * @param param
     */
    public void saveGsCompany(Map<String, Object> param) {
        Cell[] cells = this.retuenCells(param);
        if (null == cells || cells.length <= 0) {
            return;
        }
        StringBuilder sbder = null;
        Map<String,Object> basicMap = new HashedMap();
        TbGsCompany gsCompany = new TbGsCompany();
        for (Cell cell : cells) {
            sbder = new StringBuilder();
            sbder.append(Bytes.toString(CellUtil.cloneQualifier(cell)));
            if ("企业名称".equals(sbder.toString())){
                basicMap.put("comName",Bytes.toString(CellUtil.cloneValue(cell)));
            }else if ("成立日期".equals(sbder.toString())){
                basicMap.put("regisDate",Bytes.toString(CellUtil.cloneValue(cell)));
            }else if ("法定代表人".equals(sbder.toString())){
             basicMap.put("legalPerson",Bytes.toString(CellUtil.cloneValue(cell)));
            }else if ("注册资本".equals(sbder.toString())){
                basicMap.put("regisCapital",Bytes.toString(CellUtil.cloneValue(cell)));
            }else if ("登记机关".equals(sbder.toString())){
                basicMap.put("regisAuthority",Bytes.toString(CellUtil.cloneValue(cell)));
            }else if ("登记状态".equals(sbder.toString())){
                basicMap.put("subsist",Bytes.toString(CellUtil.cloneValue(cell)));
            }else if ("类型".equals(sbder.toString())){
                basicMap.put("comType",Bytes.toString(CellUtil.cloneValue(cell)));
            }else if ("经营范围".equals(sbder.toString())){
                basicMap.put("comRange",Bytes.toString(CellUtil.cloneValue(cell)));
            }else if ("统一社会信用代码".equals(sbder.toString())){
                basicMap.put("creditCode",Bytes.toString(CellUtil.cloneValue(cell)));
            }else if ("营业期限自".equals(sbder.toString())){
                basicMap.put("businessStart",Bytes.toString(CellUtil.cloneValue(cell)));
            }else if ("营业期限至".equals(sbder.toString())){
                basicMap.put("businessEnd",Bytes.toString(CellUtil.cloneValue(cell)));
            }else if ("分支机构信息".equals(sbder.toString())){
               gsCompany.setBranch(Bytes.toString(CellUtil.cloneValue(cell)));
            }else if ("变更信息".equals(sbder.toString())){
                gsCompany.setChangRecord(Bytes.toString(CellUtil.cloneValue(cell)));
            }else if ("主要人员信息".equals(sbder.toString())){
                gsCompany.setPersonnel(Bytes.toString(CellUtil.cloneValue(cell)));
            }else if ("股东及出资信息".equals(sbder.toString())){
                gsCompany.setPartner(Bytes.toString(CellUtil.cloneValue(cell)));
            }else if ("行政处罚信息".equals(sbder.toString())){
                gsCompany.setPunish(Bytes.toString(CellUtil.cloneValue(cell)));
            }
            gsCompany.setBasic(JSONObject.toJSONString(basicMap));
        }
        if (null != gsCompany){
            gsCompany.setComId(MapUtils.getString(param,"comId"));
            gsCompany.setComName(MapUtils.getString(param,"comName"));
            if (tbGsCompanyMapper.queryCompanyExits(gsCompany.getComId()) > 0){
                tbGsCompanyMapper.updatedGsCompany(gsCompany);
                return;
            }
            tbGsCompanyMapper.insertGsCompany(gsCompany);
            basicMap = null;
            gsCompany = null;
        }
        //// TODO: 2019/6/28 年报
    }

    private Cell[] retuenCells(Map<String, Object> param) {
        String comId = MapUtils.getString(param, "comId");
        String ip = PropertiesUtils.getProperty("Hbase.ip");
        String port = PropertiesUtils.getProperty("Hbase.port");
        String master = PropertiesUtils.getProperty("Hbase.master");
        String hdfs = PropertiesUtils.getProperty("Hbase.hdfs");
        try {
            Connection connection = HBaseUtils.init(ip, port, master, hdfs);
            Table table = connection.getTable(TableName.valueOf("gsxt"));
            Get get = new Get(comId.getBytes()).setId(comId);
            Result result = table.get(get);
            return result.rawCells();
        } catch (IOException e) {
            logger.error("连接Hbase异常!", e);
            return null;
        }
    }
}
