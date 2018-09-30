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
import com.silita.biaodaa.model.es.CompanyEs;
import com.silita.biaodaa.model.es.CompanyLawEs;
import com.silita.biaodaa.model.es.Law;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.MyStringUtils;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.MapUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LawService {

    private Logger logger = LoggerFactory.getLogger(LawService.class);

    @Autowired
    NativeElasticSearchUtils nativeElasticSearchUtils;
    @Autowired
    ElasticseachService elasticseachService;
    @Autowired
    TbCompanyMapper tbCompanyMapper;

    private static TransportClient client = InitElasticseach.initLawClient();

    /**
     * 获取法务列表
     *
     * @param param
     * @return
     */
    public Map<String, Object> getLawList(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        Map sort = new HashMap<String, String>();
        sort.put("date", SortOrder.DESC);
        String keyWord = MapUtils.getString(param, "keyWord");
        String start = MapUtils.getString(param, "start");
        String end = MapUtils.getString(param, "end");
        String comName = MapUtils.getString(param, "comName");
        PaginationAndSort pageSort = new PaginationAndSort(MapUtils.getInteger(param, "pageNo"), MapUtils.getInteger(param, "pageSize"), sort);
        QueryBuilder queryBuilder1 = null;
        QueryBuilder queryBuilder2 = null;
        QueryBuilder queryBuilder3 = null;
        QueryBuilder queryBuilder4 = null;
        QueryBuilder queryBuilder5 = null;
        SearchRequestBuilder requestBuilder = null;
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        BoolQueryBuilder boolQueryBuilder1 = QueryBuilders.boolQuery();
        BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery();
        if (MyStringUtils.isNotNull(comName)) {
            queryBuilder1 = QueryBuilders.queryStringQuery("\"" + comName.replace("有限公司", "") + "\"").field("content").splitOnWhitespace(false);
            queryBuilder4 = QueryBuilders.queryStringQuery("\"" + comName.replace("有限公司", "") + "\"").field("title").splitOnWhitespace(false);
            boolQueryBuilder1.should(queryBuilder1).should(queryBuilder4);
        }
        if (MyStringUtils.isNotNull(keyWord)) {
            queryBuilder2 = QueryBuilders.queryStringQuery("\"" + keyWord.replace("有限公司", "") + "\"").field("content").splitOnWhitespace(false);
            queryBuilder5 = QueryBuilders.queryStringQuery("\"" + keyWord.replace("有限公司", "") + "\"").field("title").splitOnWhitespace(false);
            boolQueryBuilder2.should(queryBuilder2).should(queryBuilder5);
        }
        if (null != start && null != end) {
            Long[] yearLog = getLong(start, end);
            queryBuilder3 = QueryBuilders.rangeQuery("date").from(yearLog[0]).to(yearLog[1]).includeLower(true).includeUpper(true);
        }
        if (null != queryBuilder1 && null != queryBuilder2 && null != queryBuilder3) {
            boolQueryBuilder.must(boolQueryBuilder1).must(boolQueryBuilder2).must(queryBuilder3);
            requestBuilder = client.prepareSearch("biaodaa").setTypes("law").setQuery(boolQueryBuilder);
        } else if (null != queryBuilder1 && null != queryBuilder2) {
            boolQueryBuilder.must(boolQueryBuilder1).must(boolQueryBuilder2);
            requestBuilder = client.prepareSearch("biaodaa").setTypes("law").setQuery(boolQueryBuilder);
        } else if (null != queryBuilder1 && null != queryBuilder3) {
            boolQueryBuilder.must(boolQueryBuilder1).must(queryBuilder3);
            requestBuilder = client.prepareSearch("biaodaa").setTypes("law").setQuery(boolQueryBuilder);
        } else if (null != queryBuilder2 && null != queryBuilder3) {
            boolQueryBuilder.must(boolQueryBuilder2).must(queryBuilder3);
            requestBuilder = client.prepareSearch("biaodaa").setTypes("law").setQuery(boolQueryBuilder);
        } else if (null != queryBuilder1) {
            requestBuilder = client.prepareSearch("biaodaa").setTypes("law").setQuery(boolQueryBuilder1);
        } else if (null != queryBuilder2) {
            requestBuilder = client.prepareSearch("biaodaa").setTypes("law").setQuery(boolQueryBuilder2);
        } else if (null != queryBuilder3) {
            requestBuilder = client.prepareSearch("biaodaa").setTypes("law").setQuery(QueryBuilders.boolQuery().must(queryBuilder3));
        } else {
            requestBuilder = client.prepareSearch("biaodaa").setTypes("law");
        }
        addSearchBuild(requestBuilder, pageSort);
        logger.info("查询elasticsearch-beginTime:" + System.currentTimeMillis() + "\n" + requestBuilder);
//        System.out.println(requestBuilder);
        SearchResponse response = requestBuilder.setFetchSource(new String[]{"date","case_no","title","court"},null).execute().actionGet();
        logger.info("查询elasticsearch-endTime:" + System.currentTimeMillis() + "----------------------------------");
//        SearchResponse response = nativeElasticSearchUtils.complexQuery(client, "biaodaa", "law", querys1, querys, ConstantUtil.CONDITION_MUST, pageSort);
        if (null == response && response.getHits().getTotalHits() <= 0) {
            return null;
        }
        Integer total = Integer.valueOf(Long.toString(response.getHits().getTotalHits()));
        if (MyStringUtils.isNull(keyWord) && MyStringUtils.isNull(comName) && MyStringUtils.isNull(start) && MyStringUtils.isNull(end)) {
            total = getAll(client, "biaodaa", "law");
        }
        Integer pages = elasticseachService.getPage(total, MapUtils.getInteger(param, "pageSize"));
        resultMap.put("total", total);
        resultMap.put("pages", pages);
        resultMap.put("pageNum", MapUtils.getInteger(param, "pageNo"));
        resultMap.put("pageSize", MapUtils.getInteger(param, "pageSize"));
        String result;
        JSONObject jsonObject;
        List<Law> lawList = new ArrayList<>();
        Law law;
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

    /**
     * 获取法务详情
     *
     * @param param
     * @return
     */
    public Law getLawDetal(Map<String, Object> param) {
        String id = MapUtils.getString(param, "id");
        List<QuerysModel> querys = new ArrayList();
        querys.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_TERMS, "_id", id));
        SearchRequestBuilder requestBuilder = nativeElasticSearchUtils.baseComplexQuery(client, "biaodaa", "law", querys, null, ConstantUtil.CONDITION_MUST, null);
        logger.info("查询elasticsearch-begin:" + "\n" + requestBuilder);
        requestBuilder.setFetchSource(new String[]{"date","case_no","title","court","content","url"},null);
        SearchResponse response = nativeElasticSearchUtils.builderToSearchResponse(requestBuilder);
        if (response.getHits().getTotalHits() <= 0) {
            return new Law();
        }
        String result = null;
        JSONObject jsonObject = null;
        result = response.getHits().getAt(0).getSourceAsString();
        Law law = new Law();
        law.setId(response.getHits().getAt(0).getId());
        jsonObject = JSON.parseObject(result);
        law.setDateStr((MyDateUtils.longDateToStr(jsonObject.getLong("date"), "yyyy-MM-dd")));
//        law.setNumber(jsonObject.getString("number"));
        law.setCaseNo(jsonObject.getString("case_no"));
        law.setCourt(jsonObject.getString("court"));
        law.setTitle(jsonObject.getString("title"));
        law.setUrl(jsonObject.getString("url"));
        law.setContent(jsonObject.getString("content").replaceAll("<a href[^>]*>", "").replaceAll("</a>", ""));
        return law;
    }

    /**
     * 获取时间（Long类型）
     *
     * @param start
     * @param end
     * @return
     */
    public Long[] getLong(String start, String end) {
        Long[] lon = new Long[2];
        start = start + "-01-01";
        end = end + "-12-31";
        lon[0] = MyDateUtils.strToDate(start, "yyyy-MM-dd").getTime();
        lon[1] = MyDateUtils.strToDate(end, "yyyy-MM-dd").getTime();
        return lon;
    }

    /**
     * 获取总数
     *
     * @param client
     * @param indexName
     * @param type
     * @return
     */
    private Integer getAll(TransportClient client, String indexName, String type) {
        SearchRequestBuilder builder = client.prepareSearch(indexName).setTypes(type);
        SearchResponse response = builder.setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
        Long hits = response.getHits().getTotalHits();
        Integer count = Integer.valueOf(Long.toString(hits));
        return count;
    }

    /**
     * 统计公司法务
     */
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
                        nativeElasticSearchUtils.batchInsertDate(client, "biaodaa", "companyforlaw", list);
                    }
                }
            }
        }
    }

    /**
     * 查询公司法务
     *
     * @param comName
     * @return
     */
    public CompanyLawEs getCompanyLawCount(String comName) {
        CompanyLawEs companyLawEs = new CompanyLawEs();
        companyLawEs.setBriCount(0);
        companyLawEs.setComName(comName);
        companyLawEs.setJudCount(0);
        QueryBuilder queryBuilder1 = QueryBuilders.queryStringQuery("\"" + comName.replace("有限公司", "") + "\"").field("content").splitOnWhitespace(false);
        QueryBuilder queryBuilder2 = QueryBuilders.queryStringQuery("\"行贿\"").field("content").splitOnWhitespace(false);
        SearchRequestBuilder requestBuilder = client.prepareSearch("biaodaa").setTypes("law").setQuery(QueryBuilders.boolQuery().must(queryBuilder1).must(queryBuilder2));
        requestBuilder.setFetchSource(new String[]{"case_no"},null);
        logger.info("查询" + comName + "行贿-beginTime:" + System.currentTimeMillis() + "\n" + requestBuilder);
        SearchResponse response = requestBuilder.execute().actionGet();
        logger.info("查询" + comName + "行贿-endTime:" + System.currentTimeMillis());
        Integer briCount = Integer.valueOf(Long.toString(response.getHits().getTotalHits()));
        companyLawEs.setBriCount(briCount);
        requestBuilder = client.prepareSearch("biaodaa").setTypes("law").setQuery(QueryBuilders.boolQuery().must(queryBuilder1).mustNot(queryBuilder2));
        requestBuilder.setFetchSource(new String[]{"case_no"},null);
        logger.info("查询" + comName + "司法-beginTime:" + System.currentTimeMillis() + "\n" + requestBuilder);
        response = requestBuilder.execute().actionGet();
        logger.info("查询" + comName + "司法-endTime:" + System.currentTimeMillis());
        Integer judCount = Integer.valueOf(Long.toString(response.getHits().getTotalHits()));
        companyLawEs.setJudCount(judCount);
        companyLawEs.setTotal(companyLawEs.getBriCount() + companyLawEs.getJudCount());
        return companyLawEs;
    }

    /**
     * 查询公司法务
     *
     * @param param
     * @return
     */
    public CompanyLawEs queryCompanyLaw(Map<String, Object> param) {
        String comName = MapUtils.getString(param, "comName");
        if(MyStringUtils.isNull(comName)){
            return new CompanyLawEs();
        }
//        CompanyLawEs companyLawEs = new CompanyLawEs();
//        companyLawEs.setTotal(0);
//        companyLawEs.setBriCount(0);
//        companyLawEs.setJudCount(0);
//        if (MyStringUtils.isNull(comName)) {
//            return companyLawEs;
//        }
//        QueryBuilder queryBuilder1 = QueryBuilders.queryStringQuery("\"" + comName + "\"").field("comName").splitOnWhitespace(false);
//        SearchRequestBuilder requestBuilder = client.prepareSearch("biaodaa").setTypes("companyforlaw").setQuery(QueryBuilders.boolQuery().must(queryBuilder1));
//        logger.info("查询elasticsearch-begin:" + "\n" + requestBuilder);
//        SearchResponse response = requestBuilder.execute().actionGet();
//        if (null == response.getHits() || response.getHits().getTotalHits() <= 0) {
//            return companyLawEs;
//        }
//        String result = response.getHits().getAt(0).getSourceAsString();
//        JSONObject jsonObject = JSON.parseObject(result);
//        companyLawEs.setComId(jsonObject.getString("comId"));
//        companyLawEs.setTotal(jsonObject.getInteger("total"));
//        companyLawEs.setJudCount(jsonObject.getInteger("judCount"));
//        companyLawEs.setComName(jsonObject.getString("comName"));
//        companyLawEs.setBriCount(jsonObject.getInteger("briCount"));
        CompanyLawEs companyLawEs = getCompanyLawCount(comName);
        return companyLawEs;
    }

    /**
     * 查询中标企业法务个数
     * @param param
     * @return
     */
    public CompanyLawEs queryZhongbiaoCompanyLaw(Map<String, Object> param) {
        String comName = MapUtils.getString(param, "comName");
        if(MyStringUtils.isNull(comName)){
            return new CompanyLawEs();
        }
        CompanyLawEs companyLawEs = new CompanyLawEs();
        companyLawEs.setComName(comName);
        QueryBuilder queryBuilder1 = QueryBuilders.queryStringQuery("\"" + comName.replace("有限公司", "") + "\"").field("content").splitOnWhitespace(false);
        SearchRequestBuilder requestBuilder = client.prepareSearch("biaodaa").setTypes("law").setQuery(QueryBuilders.boolQuery().must(queryBuilder1)).setSize(1);
        requestBuilder.setFetchSource(new String[]{},null);
        logger.info("查询elasticsearch-beginTime:" + System.currentTimeMillis() + "\n" + requestBuilder);
        SearchResponse response = requestBuilder.execute().actionGet();
        logger.info("查询elasticsearch-endTime:" + System.currentTimeMillis() + "----------------------------------");
        Integer total = Integer.valueOf(Long.toString(response.getHits().getTotalHits()));
        companyLawEs.setTotal(total);
        return companyLawEs;
    }
    /**
     * 查询公司资质
     *
     * @param comName
     * @return
     */
    public String queryCompangScope(String comName) {
        String scope = null;
        if (MyStringUtils.isNull(comName)) {
            return scope;
        }
        List<QuerysModel> querys = new ArrayList();
        querys.add(new QuerysModel(ConstantUtil.CONDITION_MUST, ConstantUtil.MATCHING_TERMS, "name", comName));
        SearchResponse response = nativeElasticSearchUtils.complexQuery(client, "biaodaa", "company", querys, new ArrayList<>(), null, null);
        if (null == response.getHits() || response.getHits().getTotalHits() <= 0) {
            return scope;
        }
        String result = response.getHits().getAt(0).getSourceAsString();
        JSONObject jsonObject = JSON.parseObject(result);
        scope = jsonObject.getString("operate_range");
        return scope;
    }

    /**
     * es查询排序
     *
     * @param builder
     * @param paginationAndSort
     */
    private void addSearchBuild(SearchRequestBuilder builder, PaginationAndSort paginationAndSort) {
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
    }

    /**
     * 获取公司文案
     *
     * @param param
     * @return
     */
    public Map<String, Object> getCompanyLaw(Map<String, Object> param) {
        CompanyLawEs companyLawEs = this.queryCompanyLaw(param);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("lawBri", PropertiesUtils.getProperty("law.bri"));
        resultMap.put("briCount", companyLawEs.getBriCount());
        resultMap.put("lawJud", PropertiesUtils.getProperty("law.jud"));
        resultMap.put("judCount", companyLawEs.getJudCount());
        resultMap.put("lawTotal", PropertiesUtils.getProperty("law.total"));
        resultMap.put("total", companyLawEs.getTotal());
        return resultMap;
    }
}
