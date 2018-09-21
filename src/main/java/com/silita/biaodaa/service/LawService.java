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
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
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
        String start = MapUtils.getString(param, "start");
        String end = MapUtils.getString(param, "end");
        String comName = MapUtils.getString(param, "comName");
        PaginationAndSort pageSort = new PaginationAndSort(MapUtils.getInteger(param, "pageNo"), MapUtils.getInteger(param, "pageSize"), sort);
        List<QuerysModel> querys = new ArrayList();
        List<QuerysModel> querys1 = new ArrayList();
        if (null != comName) {
            List<QuerysModel> comQuerys = getCondition(comName, "must");
            querys.addAll(comQuerys);
        }
        if (null != keyWord) {
            List<QuerysModel> keyQuerys = getCondition(keyWord, "must");
            querys.addAll(keyQuerys);
        }
        if (null != start && null != end) {
            Long[] yearLog = getLong(start, end);
            querys1.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_RANGE, "date", yearLog[0] + ConstantUtil.SPLIT_WORDS + yearLog[1]));
        }
        SearchRequestBuilder searchRequestBuilder = null;
        BoolQueryBuilder boolQueryBuilder = null;
        BoolQueryBuilder boolQueryBuilder1 = null;
        if ((null != querys && querys.size() > 0) && (null == querys1 || querys1.size() <= 0)) {
            boolQueryBuilder = this.buildCondition(querys);
            searchRequestBuilder = this.getBuild(boolQueryBuilder, pageSort);
        } else if (null != querys1 && querys1.size() > 0 && (null == querys || querys.size() <= 0)) {
            boolQueryBuilder1 = this.buildCondition(querys1);
            searchRequestBuilder = this.getBuild(boolQueryBuilder1, pageSort);
        } else if ((null != querys && querys.size() > 0) && (null != querys1 && querys1.size() > 0)) {
            boolQueryBuilder = this.buildCondition(querys);
            boolQueryBuilder1 = this.buildCondition(querys1);
            searchRequestBuilder = this.getBuild(boolQueryBuilder.must(boolQueryBuilder1), pageSort);
        } else {
            searchRequestBuilder = this.getBuild(null, pageSort);
        }

        SearchResponse response = nativeElasticSearchUtils.builderToSearchResponse(searchRequestBuilder);
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


    public Law getLawDetal(Map<String, Object> param) {
        String id = MapUtils.getString(param, "id");
        List<QuerysModel> querys = new ArrayList();
        querys.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_TERMS, "_id", id));
        SearchRequestBuilder requestBuilder = nativeElasticSearchUtils.baseComplexQuery(client, "alias_biaodaa", "law", querys, null, ConstantUtil.CONDITION_MUST, null);
        SearchResponse response = nativeElasticSearchUtils.builderToSearchResponse(requestBuilder);
        if (null == response && response.getHits().getTotalHits() <= 0) {
            return null;
        }
        String result = null;
        JSONObject jsonObject = null;
        result = response.getHits().getAt(0).getSourceAsString();
        Law law = new Law();
        law.setId(response.getHits().getAt(0).getId());
        jsonObject = JSON.parseObject(result);
        law.setDateStr((MyDateUtils.longDateToStr(jsonObject.getLong("date"), "yyyy-MM-dd")));
        law.setNumber(jsonObject.getString("number"));
        law.setCaseNo(jsonObject.getString("case_no"));
        law.setCourt(jsonObject.getString("court"));
        law.setTitle(jsonObject.getString("title"));
        law.setUrl(jsonObject.getString("url"));
        law.setContent(jsonObject.getString("content"));
        return law;
    }

    public Long[] getLong(String start, String end) {
        Long[] lon = new Long[2];
        start = start + "-01-01";
        end = end + "-12-31";
        lon[0] = MyDateUtils.strToDate(start, "yyyy-MM-dd").getTime();
        lon[1] = MyDateUtils.strToDate(end, "yyyy-MM-dd").getTime();
        return lon;
    }

    private List<QuerysModel> getCondition(String condition, String conditionType) {
        List<QuerysModel> querys = new ArrayList();
        if ("must".equals(conditionType)) {
            querys.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_MATCH, "content", condition));
            querys.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_MATCH, "title", condition));
        } else if ("mustNot".equals(conditionType)) {
            querys.add(new QuerysModel(ConstantUtil.CONDITION_MUST_NOT, ConstantUtil.MATCHING_MATCH, "content", condition));
            querys.add(new QuerysModel(ConstantUtil.CONDITION_MUST_NOT, ConstantUtil.MATCHING_MATCH, "title", condition));
        }
        return querys;
    }

    private BoolQueryBuilder buildCondition(List<QuerysModel> querysModels) {
        BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder;
        for (QuerysModel querysModel : querysModels) {
            queryBuilder = nativeElasticSearchUtils.buildQueryBuilder(querysModel);
            if ("match".equals(querysModel.getQueryType())) {
                queryBuilder = QueryBuilders.matchQuery(querysModel.getQueryKey(), querysModel.getQueryValues()).minimumShouldMatch("100%");
            }
            if ("must".equals(querysModel.getMatchingType())) {
                boolBuilder.must(queryBuilder);
            } else if ("filter".equals(querysModel.getMatchingType())) {
                boolBuilder.filter(queryBuilder);
            } else if ("should".equals(querysModel.getMatchingType())) {
                boolBuilder.should(queryBuilder);
            } else if ("mustNot".equals(querysModel.getMatchingType())) {
                boolBuilder.mustNot(queryBuilder);
            }
        }
        return boolBuilder;
    }


    private SearchRequestBuilder getBuild(BoolQueryBuilder boolBuilder, PaginationAndSort paginationAndSort) {
        SearchRequestBuilder builder = client.prepareSearch(new String[]{"alias_biaodaa"}).setTypes(new String[]{"law"});
        builder = builder.setQuery(boolBuilder);
        if (null != paginationAndSort) {
            builder = builder.setFrom(paginationAndSort.getStart()).setSize(paginationAndSort.getPageSize());
            Map<String, SortOrder> sort = paginationAndSort.getSort();
            Map.Entry entry;
            if (sort != null && sort.size() > 0) {
                for (Iterator var19 = sort.entrySet().iterator(); var19.hasNext(); builder = builder.addSort((String) entry.getKey(), (SortOrder) entry.getValue())) {
                    entry = (Map.Entry) var19.next();
                }
            }
        }
        return builder;
    }
}
