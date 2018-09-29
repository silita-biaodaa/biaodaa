package com.silita.biaodaa.task;

import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.elastic.common.NativeElasticSearchUtils;
import com.silita.biaodaa.es.InitElasticseach;
import com.silita.biaodaa.model.es.CompanyEs;
import com.silita.biaodaa.model.es.CompanyLawEs;
import com.silita.biaodaa.service.LawService;
import org.elasticsearch.client.transport.TransportClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公司法务统计
 */
public class CompanyForLawTask implements Runnable {

    private int start;  //开始页
    private int end;  //结束页
    private TbCompanyMapper tbCompanyMapper;
    private LawService lawService;
    private NativeElasticSearchUtils nativeElasticSearchUtils;

    private TransportClient transportClient = InitElasticseach.initLawClient();

    public CompanyForLawTask() {

    }

    public CompanyForLawTask(int start, int end, TbCompanyMapper tbCompanyMapper, LawService lawService, NativeElasticSearchUtils nativeElasticSearchUtils) {
        this.start = start;
        this.end = end;
        this.tbCompanyMapper = tbCompanyMapper;
        this.lawService = lawService;
        this.nativeElasticSearchUtils = nativeElasticSearchUtils;
    }

    public void run() {
        List<CompanyEs> comList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("pageSize", 1000);
        Integer pageTotal = 0;
        while (pageTotal <= end) {          //线程会循环执行，直到所有数据都处理完
            synchronized (this) {    //在分包时需要线程同步，避免线程间处理重复的数据
                for (int i = start + 1; i <= end; i++) {
                    param.put("page", (i - 1) * 1000);
                    System.out.println(param.get("page") + "\t" + param.get("pageSize"));
                    comList = tbCompanyMapper.queryCompanyEsList(param);
//                    if (comList != null && comList.size() > 0) {
                    List<CompanyLawEs> lista;
                    CompanyLawEs companyLawEs;
                    for (CompanyEs companyEs : comList) {
                        lista = new ArrayList<>();
                        companyLawEs = lawService.getCompanyLawCount(companyEs.getComName());
                        companyLawEs.setComId(companyEs.getComId());
                        lista.add(companyLawEs);
                        nativeElasticSearchUtils.batchInsertDate(transportClient, "biaodaa", "companyforlaw", lista);
                    }
//                    }
                    pageTotal = i + 1;
                }
            }
        }
    }
}