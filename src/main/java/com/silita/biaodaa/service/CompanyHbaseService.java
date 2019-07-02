package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.dao.TbCompanyReportMapper;
import com.silita.biaodaa.dao.TbGsCompanyMapper;
import com.silita.biaodaa.model.TbCompanyReport;
import com.silita.biaodaa.model.TbGsCompany;
import com.silita.biaodaa.utils.HBaseUtils;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueExcludeFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    @Autowired
    TbCompanyReportMapper tbCompanyReportMapper;


    /**
     * 解析并保存工商
     *
     * @param param
     */
    public void saveGsCompany(Map<String, Object> param) {
        Cell[] cells = this.returnCells(param);
        if (null == cells || cells.length <= 0) {
            return;
        }
        StringBuilder sbder = null;
        Map<String, Object> basicMap = new HashedMap();
        TbGsCompany gsCompany = new TbGsCompany();
        for (Cell cell : cells) {
            sbder = new StringBuilder();
            sbder.append(Bytes.toString(CellUtil.cloneQualifier(cell)));
            if ("企业名称".equals(sbder.toString())) {
                basicMap.put("comName", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("成立日期".equals(sbder.toString())) {
                basicMap.put("regisDate", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("法定代表人".equals(sbder.toString())) {
                basicMap.put("legalPerson", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("注册资本".equals(sbder.toString())) {
                basicMap.put("regisCapital", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("登记机关".equals(sbder.toString())) {
                basicMap.put("regisAuthority", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("登记状态".equals(sbder.toString())) {
                basicMap.put("subsist", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("类型".equals(sbder.toString())) {
                basicMap.put("comType", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("经营范围".equals(sbder.toString())) {
                basicMap.put("comRange", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("统一社会信用代码".equals(sbder.toString())) {
                basicMap.put("creditCode", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("营业期限自".equals(sbder.toString())) {
                basicMap.put("businessStart", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("营业期限至".equals(sbder.toString())) {
                basicMap.put("businessEnd", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("分支机构信息".equals(sbder.toString())) {
                gsCompany.setBranch(Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("变更信息".equals(sbder.toString())) {
                gsCompany.setChangeRecord(Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("主要人员信息".equals(sbder.toString())) {
                gsCompany.setPersonnel(Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("股东及出资信息".equals(sbder.toString())) {
                gsCompany.setPartner(Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("行政处罚信息".equals(sbder.toString())) {
                gsCompany.setPunish(Bytes.toString(CellUtil.cloneValue(cell)));
            }
            gsCompany.setBasic(JSONObject.toJSONString(basicMap));
        }
        if (null != gsCompany) {
            gsCompany.setComId(MapUtils.getString(param, "comId"));
            gsCompany.setComName(MapUtils.getString(param, "comName"));
            if (tbGsCompanyMapper.queryCompanyExits(gsCompany.getComId()) > 0) {
                tbGsCompanyMapper.updatedGsCompany(gsCompany);
                return;
            }
            tbGsCompanyMapper.insertGsCompany(gsCompany);
            basicMap = null;
            gsCompany = null;
        }
    }

    /**
     * 保存企业年报
     *
     * @param param
     */
    public void saveReport(Map<String, Object> param) {
        List<Result> results = returnReportCells(param);
        TbCompanyReport report = null;
        for (Result result : results) {
            Cell[] cells = result.rawCells();
            report = new TbCompanyReport();
            report.setComId(MapUtils.getString(param, "comId"));
            Map<String, Object> reportMap = new HashedMap();
            for (Cell cell : cells) {
                if ("year".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    report.setYear(Integer.valueOf(Bytes.toString(CellUtil.cloneValue(cell))));
                } else if ("企业名称".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    report.setComName(Bytes.toString(CellUtil.cloneValue(cell)));
                } else if ("基本信息".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    reportMap.put("basic", JSONObject.parse(Bytes.toString(CellUtil.cloneValue(cell))));
                } else if ("股东及出资信息".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    reportMap.put("partner", JSONObject.parse(Bytes.toString(CellUtil.cloneValue(cell))));
                } else if ("网站或网店信息".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    reportMap.put("website", JSONObject.parse(Bytes.toString(CellUtil.cloneValue(cell))));
                } else if ("社保信息".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    reportMap.put("socialSecurity", JSONObject.parse(Bytes.toString(CellUtil.cloneValue(cell))));
                } else if ("对外投资信息".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    reportMap.put("invest", JSONObject.parse(Bytes.toString(CellUtil.cloneValue(cell))));
                } else if ("企业资产状况信息".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    reportMap.put("amount", JSONObject.parse(Bytes.toString(CellUtil.cloneValue(cell))));
                }
            }
            if (MapUtils.isNotEmpty(reportMap)) {
                report.setReport(JSONObject.toJSONString(reportMap));
                if (tbCompanyReportMapper.queryCompanyCount(report) > 0) {
                    tbCompanyReportMapper.updateCompanyReport(report);
                    continue;
                }
                tbCompanyReportMapper.insertCompanyReport(report);
            }
            reportMap = null;
            report = null;
        }
    }

    private Cell[] returnCells(Map<String, Object> param) {
        String comId = MapUtils.getString(param, "comId");
        String ip = PropertiesUtils.getProperty("Hbase.ip");
        String port = PropertiesUtils.getProperty("Hbase.port");
        String master = PropertiesUtils.getProperty("Hbase.master");
        String hdfs = PropertiesUtils.getProperty("Hbase.hdfs");
        Connection connection = null;
        try {
            connection = HBaseUtils.init(ip, port, master, hdfs);
            Table table = connection.getTable(TableName.valueOf("gsxt"));
            Get get = new Get(comId.getBytes()).setId(comId);
            Result result = table.get(get);
            return result.rawCells();
        } catch (IOException e) {
            logger.error("连接Hbase异常!", e);
            return null;
        } finally {
            HBaseUtils.close(connection);
        }
    }

    private List<Result> returnReportCells(Map<String, Object> param) {
        List<Result> results = new ArrayList<>();
        String comId = MapUtils.getString(param, "comId");
        String ip = PropertiesUtils.getProperty("Hbase.ip");
        String port = PropertiesUtils.getProperty("Hbase.port");
        String master = PropertiesUtils.getProperty("Hbase.master");
        String hdfs = PropertiesUtils.getProperty("Hbase.hdfs");
        Connection connection = null;
        try {
            connection = HBaseUtils.init(ip, port, master, hdfs);
            Scan scan = new Scan();
            Filter filter = new SingleColumnValueExcludeFilter(Bytes.toBytes("basic"), Bytes.toBytes("com_id"), CompareOperator.EQUAL, Bytes.toBytes(comId));
            scan.setFilter(filter);
            Table table = connection.getTable(TableName.valueOf("report"));
            ResultScanner resultScanner = table.getScanner(scan);
            if (null != resultScanner.next()) {
                for (Result result : resultScanner) {
                    results.add(result);
                }
            }
        } catch (IOException e) {
            logger.error("连接Hbase异常!", e);
            return null;
        } finally {
            HBaseUtils.close(connection);
        }
        return results;
    }

}
