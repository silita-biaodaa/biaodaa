package com.silita.biaodaa.es;

import com.alibaba.fastjson.JSON;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.elastic.common.ConstantUtil;
import com.silita.biaodaa.elastic.common.NativeElasticSearchUtils;
import com.silita.biaodaa.elastic.model.PaginationAndSort;
import com.silita.biaodaa.elastic.model.QuerysModel;
import com.silita.biaodaa.model.CompanyEs;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.MapUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ElasticseachService {


    private static String ip = PropertiesUtils.getProperty("ELASTICSEARCH_IP");

    private static String host = PropertiesUtils.getProperty("ELASTICSEARCH_HOST");

    private static String cluster = PropertiesUtils.getProperty("ELASTICSEARCH_CLUSTER");

    @Autowired
    TbCompanyMapper tbCompanyMapper;

    @Autowired
    NativeElasticSearchUtils nativeElasticSearchUtils;

    private TransportClient transportClient;

    public void bastchAddCompany() {
        transportClient = nativeElasticSearchUtils.initClient(ip, cluster, Integer.parseInt(host));
        List<CompanyEs> comList = tbCompanyMapper.queryCompanyEsList();
        nativeElasticSearchUtils.createIndexAndCreateMapping(transportClient,CompanyEs.class);
        nativeElasticSearchUtils.batchInsertDate(transportClient, "company", "comes", comList);
    }

    public List<Map<String,Object>> quary(Map<String,Object> param) {
        transportClient = nativeElasticSearchUtils.initClient(ip, cluster, Integer.parseInt(host));
        Map sort = new HashMap<String, String>();
        sort.put("px", SortOrder.DESC);
        String comName = MapUtils.getString(param,"name");
        PaginationAndSort pageSort = new PaginationAndSort(1, MapUtils.getInteger(param,"count"), sort);
        List<QuerysModel> querys = new ArrayList();
        querys.add(new QuerysModel(ConstantUtil.CONDITION_SHOULD, ConstantUtil.MATCHING_WILDCARD, "comName", comName));
        querys.add(new QuerysModel(ConstantUtil.CONDITION_SHOULD, ConstantUtil.MATCHING_WILDCARD, "comNamePy", comName));
        SearchResponse response = nativeElasticSearchUtils.complexQuery(transportClient, "company", "comes", querys, pageSort);
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map = null;
        for (SearchHit hit : response.getHits()) {
            map = new HashMap<>();
            map = (Map<String, Object>) JSON.parse(hit.getSourceAsString());
            map.put("com_name",map.get("comName"));
            list.add(map);
        }
        return  list;
    }

}
