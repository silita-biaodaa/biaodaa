package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.elastic.common.ConstantUtil;
import com.silita.biaodaa.elastic.common.NativeElasticSearchUtils;
import com.silita.biaodaa.elastic.model.PaginationAndSort;
import com.silita.biaodaa.elastic.model.QuerysModel;
import com.silita.biaodaa.es.ElasticseachService;
import com.silita.biaodaa.es.InitElasticseach;
import com.silita.biaodaa.model.TbCompanyInfo;
import com.silita.biaodaa.model.es.CompanyEs;
import com.silita.biaodaa.model.es.CompanyLawEs;
import com.silita.biaodaa.model.es.Law;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.MyStringUtils;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LawService {

    @Autowired
    NativeElasticSearchUtils nativeElasticSearchUtils;
    @Autowired
    ElasticseachService elasticseachService;
    @Autowired
    TbCompanyMapper tbCompanyMapper;

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
        if (MyStringUtils.isNotNull(comName)) {
            List<QuerysModel> comQuerys = getCondition(comName, "must");
            querys.addAll(comQuerys);
        }
        if (MyStringUtils.isNotNull(keyWord)) {
            List<QuerysModel> keyQuerys = getCondition(keyWord, "must");
            querys.addAll(keyQuerys);
        }
        if (null != start && null != end) {
            Long[] yearLog = getLong(start, end);
            querys1.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_RANGE, "date", yearLog[0] + ConstantUtil.SPLIT_WORDS + yearLog[1]));
        }

        SearchResponse response = nativeElasticSearchUtils.complexQuery(client, "biaodaa", "law", querys1, querys, ConstantUtil.CONDITION_MUST, pageSort);
        if (null == response && response.getHits().getTotalHits() <= 0) {
            return null;
        }
        Integer total = Integer.valueOf(Long.toString(response.getHits().getTotalHits()));
        if (MyStringUtils.isNull(keyWord) && MyStringUtils.isNull(comName)) {
            total = getAll(client, "alias_biaodaa", "law");
        }
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
            law.setDate(jsonObject.getLong("date"));
            law.setKeyword(jsonObject.getString("keyWord"));
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
            querys.add(new QuerysModel(ConstantUtil.CONDITION_SHOULD, ConstantUtil.MATCHING_WILDCARD, "content", "*" + condition + "*"));
            querys.add(new QuerysModel(ConstantUtil.CONDITION_SHOULD, ConstantUtil.MATCHING_WILDCARD, "title", "*" + condition + "*"));
        } else if ("mustNot".equals(conditionType)) {
            querys.add(new QuerysModel(ConstantUtil.CONDITION_SHOULD, ConstantUtil.MATCHING_MATCH, "content", "*" + condition + "*"));
            querys.add(new QuerysModel(ConstantUtil.CONDITION_SHOULD, ConstantUtil.MATCHING_MATCH, "title", "*" + condition + "*"));
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

    private Integer getAll(TransportClient client, String indexName, String type) {
        SearchRequestBuilder builder = client.prepareSearch(indexName).setTypes(type);
        SearchResponse response = builder.setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
        Long hits = response.getHits().getTotalHits();
        Integer count = Integer.valueOf(Long.toString(hits));
        return count;
    }

    public void countCompanyLaw() {
        Integer count = tbCompanyMapper.queryCompanyCount();
        List<CompanyEs> comList = new ArrayList<>();
        if (count > 0) {
            Map<String, Object> param = new HashMap<>();
            param.put("pageSize", 1000);
            //获取pages
            Integer pages = elasticseachService.getPage(count, 1000);
            List<CompanyLawEs> list;
            CompanyLawEs companyLawEs;
            for (int i = 1; i <= pages; i++) {
                param.put("page", (i - 1) * 1000);
                comList = tbCompanyMapper.queryCompanyEsList(param);
                if (null != comList && comList.size() > 0) {
                    for (CompanyEs companyEs : comList) {
                        list = new ArrayList<>();
                        companyLawEs = getCompanyLawCount(companyEs.getComName());
                        companyLawEs.setComId(companyEs.getComId());
                        list.add(companyLawEs);
                        nativeElasticSearchUtils.batchInsertDate(client,"biaodaa", "companyforlaw", list);
                    }
                }
            }
        }
    }

    public CompanyLawEs getCompanyLawCount(String comName) {
        CompanyLawEs companyLawEs = new CompanyLawEs();
        companyLawEs.setBriCount(0);
        companyLawEs.setComName(comName);
        companyLawEs.setJudCount(0);
        List<QuerysModel> querys = new ArrayList();
        querys.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_WILDCARD, "content", "*" + comName + "*"));
        querys.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_WILDCARD, "content", "*行贿*"));
        SearchResponse response = nativeElasticSearchUtils.complexQuery(client, "biaodaa", "law", new ArrayList<>(), querys, ConstantUtil.CONDITION_MUST, null);
        Integer briCount = Integer.valueOf(Long.toString(response.getHits().getTotalHits()));
        companyLawEs.setBriCount(briCount);
        querys = new ArrayList<>();
        querys.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_WILDCARD, "content", "*" + comName + "*"));
        querys.add(new QuerysModel(ConstantUtil.CONDITION_MUST_NOT, ConstantUtil.MATCHING_WILDCARD, "content", "*行贿*"));
        response = nativeElasticSearchUtils.complexQuery(client, "biaodaa", "law", new ArrayList<>(), querys, ConstantUtil.CONDITION_MUST, null);
        Integer judCount = Integer.valueOf(Long.toString(response.getHits().getTotalHits()));
        companyLawEs.setJudCount(judCount);
        companyLawEs.setTotal(companyLawEs.getBriCount()+companyLawEs.getJudCount());
        return companyLawEs;
    }

    public CompanyLawEs queryCompanyLaew(Map<String, Object> param) {
        String comName = MapUtils.getString(param, "comName");
        CompanyLawEs companyLawEs = new CompanyLawEs();
        companyLawEs.setTotal(0);
        companyLawEs.setBriCount(0);
        companyLawEs.setJudCount(0);
        if(MyStringUtils.isNull(comName)){
            return companyLawEs;
        }
        List<QuerysModel> querys = new ArrayList();
        querys.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_TERMS, "comName", comName));
        SearchResponse response = nativeElasticSearchUtils.complexQuery(client, "biaodaa", "companyforlaw", querys, new ArrayList<>(), null, null);
        if(null == response.getHits() || response.getHits().getTotalHits() <= 0){
            return companyLawEs;
        }
        String result = response.getHits().getAt(0).getSourceAsString();
        JSONObject jsonObject = JSON.parseObject(result);
        companyLawEs.setComId(jsonObject.getString("comId"));
        companyLawEs.setTotal(jsonObject.getInteger("total"));
        companyLawEs.setJudCount(jsonObject.getInteger("judCount"));
        companyLawEs.setComName(jsonObject.getString("comName"));
        companyLawEs.setBriCount(jsonObject.getInteger("briCount"));
        return companyLawEs;
    }
}
