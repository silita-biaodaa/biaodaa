package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.elastic.common.ConstantUtil;
import com.silita.biaodaa.elastic.common.NativeElasticSearchUtils;
import com.silita.biaodaa.elastic.model.PaginationAndSort;
import com.silita.biaodaa.elastic.model.QuerysModel;
import com.silita.biaodaa.es.ElasticseachService;
import com.silita.biaodaa.es.InitElasticseach;
import com.silita.biaodaa.model.es.Law;
import com.silita.biaodaa.utils.MyDateUtils;
import org.apache.commons.collections.MapUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LawService {

    @Autowired
    NativeElasticSearchUtils nativeElasticSearchUtils;
    @Autowired
    ElasticseachService elasticseachService;

    private static TransportClient client = InitElasticseach.initLawClient();

    public Map<String, Object> getLawList(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map sort = new HashMap<String, String>();
        sort.put("date", SortOrder.DESC);
        String keyWord = MapUtils.getString(param, "keyWord");
        String year = MapUtils.getString(param, "year");
        PaginationAndSort pageSort = new PaginationAndSort(MapUtils.getInteger(param, "pageNo"), MapUtils.getInteger(param, "pageSize"), sort);
        List<QuerysModel> querys = new ArrayList();
        if (null != keyWord) {
            querys.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_MATCH, "title", keyWord));
            querys.add(new QuerysModel(ConstantUtil.CONDITION_SHOULD, ConstantUtil.MATCHING_MATCH_PHRASE, "title", keyWord));
            querys.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_MATCH, "content", keyWord));
            querys.add(new QuerysModel(ConstantUtil.CONDITION_SHOULD, ConstantUtil.MATCHING_MATCH_PHRASE, "content", keyWord));
        }
        List<QuerysModel> querys1 = new ArrayList();
        if (null != year) {
            Long[] lonYear = getYear(year);
            querys1.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_RANGE, "date", lonYear[0] + ConstantUtil.SPLIT_WORDS + lonYear[1]));
        }
        SearchRequestBuilder requestBuilder = nativeElasticSearchUtils.baseComplexQuery(client, "alias_biaodaa", "law", querys1, querys, ConstantUtil.CONDITION_MUST, pageSort);
        SearchResponse response = nativeElasticSearchUtils.builderToSearchResponse(requestBuilder);
        if (null == response && response.getHits().getTotalHits() <= 0) {
            return null;
        }
        Integer total = Integer.valueOf(Long.toString(response.getHits().getTotalHits()));
        Integer pages = elasticseachService.getPage(total, MapUtils.getInteger(param, "pageSize"));
        resultMap.put("total", total);
        resultMap.put("pages", pages);
        resultMap.put("pageNum", MapUtils.getInteger(param, "pageNo"));
        resultMap.put("pageSize", MapUtils.getInteger(param, "pageSize"));
        String result = null;
        JSONObject jsonObject = null;
        List<Law> lawList = new ArrayList<>();
        Law law = null;
        for (SearchHit hit : response.getHits()) {
            result = hit.getSourceAsString();
            law = new Law();
            law.setId(hit.getId());
            jsonObject = JSON.parseObject(result);
            law.setDateStr((MyDateUtils.longDateToStr(jsonObject.getLong("date"), "yyyy-MM-dd")));
            law.setNumber(jsonObject.getString("number"));
            law.setCaseNo(jsonObject.getString("case_no"));
            law.setCourt(jsonObject.getString("court"));
            law.setTitle(jsonObject.getString("title"));
            law.setUrl(jsonObject.getString("url"));
            lawList.add(law);
        }
        resultMap.put("data", lawList);
        return resultMap;
    }

    private Long[] getYear(String year) {
        Long[] str = new Long[2];
        if (year.contains("以下")) {
            str = getLong(year.replace("以下", "").trim());
            str[0] = 0L;
            return str;
        }
        str = getLong(year);
        return str;
    }

    public Long[] getLong(String year) {
        Long[] lon = new Long[2];
        String begin = year + "-01-01";
        String end = year + "-12-31";
        lon[0] = MyDateUtils.strToDate(begin, "yyyy-MM-dd").getTime();
        lon[1] = MyDateUtils.strToDate(end, "yyyy-MM-dd").getTime();
        return lon;
    }
}
