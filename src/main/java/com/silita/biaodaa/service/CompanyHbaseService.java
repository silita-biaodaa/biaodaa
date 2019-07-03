package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.utils.HBaseUtils;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 工商数据
 * Created by zhushuai on 2019/6/28.
 */
@Service
public class CompanyHbaseService {

    private static Logger logger = LoggerFactory.getLogger(CompanyHbaseService.class);

    /**
     * 解析并保存工商
     *
     * @param param
     */
    public Map<String, Object> getGsCompany(Map<String, Object> param) {
        Cell[] cells = this.returnCells(param);
        if (null == cells || cells.length <= 0) {
            return null;
        }
        Map<String, Object> resultMap = new HashedMap();
        StringBuilder sbder = null;
        Map<String, Object> basicMap = new HashedMap();
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
            } else if ("住所".equals(sbder.toString())) {
                basicMap.put("comAddress", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("经营范围".equals(sbder.toString())) {
                basicMap.put("comRange", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("统一社会信用代码".equals(sbder.toString())) {
                basicMap.put("creditCode", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("营业期限自".equals(sbder.toString())) {
                basicMap.put("businessStart", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("营业期限至".equals(sbder.toString())) {
                basicMap.put("businessEnd", Bytes.toString(CellUtil.cloneValue(cell)));
            } else if ("分支机构信息".equals(sbder.toString())) {
                resultMap.put("branch", JSONObject.parse(Bytes.toString(CellUtil.cloneValue(cell))));
            } else if ("变更信息".equals(sbder.toString())) {
                resultMap.put("changeRecord", JSONObject.parse(Bytes.toString(CellUtil.cloneValue(cell))));
            } else if ("主要人员信息".equals(sbder.toString())) {
                resultMap.put("personnel", JSONObject.parse(Bytes.toString(CellUtil.cloneValue(cell))));
            } else if ("股东及出资信息".equals(sbder.toString())) {
                resultMap.put("partner", JSONObject.parse(Bytes.toString(CellUtil.cloneValue(cell))));
            } else if ("行政处罚信息".equals(sbder.toString())) {
                resultMap.put("punish", JSONObject.parse(Bytes.toString(CellUtil.cloneValue(cell))));
            }
        }
        resultMap.put("basic", basicMap);
        return resultMap;
    }

    /**
     * 保存企业年报
     *
     * @param param
     */
    public Map<String, Object> getCompanyReport(Map<String, Object> param) {
        List<Result> results = returnReportCells(param);
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        for (Result result : results) {
            Map<String, Object> reportMap = new HashedMap();
            Cell[] cells = result.rawCells();
            for (Cell cell : cells) {
                if ("year".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    reportMap.put("year", Integer.valueOf(Bytes.toString(CellUtil.cloneValue(cell))));
                } else if ("企业名称".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    reportMap.put("comName", Bytes.toString(CellUtil.cloneValue(cell)));
                } else if ("基本信息".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    reportMap.put("basic", ((Map<String,Object>)JSONObject.parse(Bytes.toString(CellUtil.cloneValue(cell)))));
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
                }else if ("对外提供保证担保信息".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))){
                    reportMap.put("externalGuarantee ", JSONObject.parse(Bytes.toString(CellUtil.cloneValue(cell))));
                }
            }
            resultMapList.add(reportMap);
        }
        if (null != resultMapList && resultMapList.size() > 0) {
            String year = MapUtils.getString(param, "years");
            for (Map<String, Object> map : resultMapList) {
                if (year.equals(map.get("year").toString())) {
                    return map;
                }
            }
        }
        return null;
    }

    public List<Map<String, Object>> getCompanyReportYear(Map<String, Object> param) {
        List<Map<String, Object>> years = new ArrayList<>();
        List<Result> results = returnReportCells(param);
        for (Result result : results) {
            Cell[] cells = result.rawCells();
            Map<String, Object> reportMap = new HashedMap();
            reportMap.put("comId", MapUtils.getString(param, "comId"));
            for (Cell cell : cells) {
                if ("year".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))) {
                    reportMap.put("years", Integer.valueOf(Bytes.toString(CellUtil.cloneValue(cell))));
                    years.add(reportMap);
                }
            }
        }

        Collections.sort(years, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                int o1ye = MapUtils.getInteger(o1, "years");
                int o2ye = MapUtils.getInteger(o2, "years");
                return o2ye - o1ye;
            }
        });
        return years;
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

    private static List<Result> returnReportCells(Map<String, Object> param) {
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
            FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
            SubstringComparator comp = new SubstringComparator(comId);
            filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes("basic"), Bytes.toBytes("com_id"), CompareOperator.EQUAL, comp));
            scan.setFilter(filterList);
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
