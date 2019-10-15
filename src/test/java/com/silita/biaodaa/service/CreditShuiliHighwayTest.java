package com.silita.biaodaa.service;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.dao.TbHighwayCreditMapper;
import com.silita.biaodaa.dao.TbShuiliCreditMapper;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbHighwayCredit;
import com.silita.biaodaa.model.TbShuiliCredit;
import com.silita.biaodaa.utils.MongodbUtils;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.MapUtils;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.json.zip.JSONzip.end;

/**
 * 公路/水利信用等级导入
 * Created by zhushuai on 2019/10/15.
 */
public class CreditShuiliHighwayTest extends ConfigTest {

    @Autowired
    TbHighwayCreditMapper tbHighwayCreditMapper;
    @Autowired
    TbCompanyMapper tbCompanyMapper;
    @Autowired
    TbShuiliCreditMapper tbShuiliCreditMapper;

    private static Datastore db = MongodbUtils.init(PropertiesUtils.getProperty("mongodb.ip"), PropertiesUtils.getProperty("mongodb.port"), "tb_all");

    @org.junit.Test
    public void test() {
        DBCollection dbCollection = db.getDB().getCollection("credit_highway");
        int total = dbCollection.find().count();
        if (total <= 0) {
            return;
        }
        int pageSize = 2000;
        int pages = this.getPage(total, pageSize);
        TbCompany company;
        for (int i = 1; i <= pages; i++) {
            DBCursor dbCursor = dbCollection.find().skip((i - 1) * pageSize).limit(end).batchSize(pageSize);
            while (dbCursor.hasNext()) {
                DBObject dbObject = dbCursor.next();
                Map map = dbObject.toMap();
                TbHighwayCredit highwayCredit = new TbHighwayCredit();
                highwayCredit.setComName(MapUtils.getString(map,"company"));
                company = tbCompanyMapper.queryCompanyDetail(highwayCredit.getComName());
                if (null == company){
                    continue;
                }
                highwayCredit.setComId(company.getComId());
                highwayCredit.setIssueProvince(MapUtils.getString(map,"province"));
                highwayCredit.setLevel(MapUtils.getString(map,"creditrat"));
                highwayCredit.setYears(MapUtils.getInteger(map,"year"));
                highwayCredit.setRegisAddress(company.getRegisAddress());
                tbHighwayCreditMapper.insert(highwayCredit);
            }
        }
    }

    @org.junit.Test
    public void testShuili() {
        DBCollection dbCollection = db.getDB().getCollection("credit_water");
        int total = dbCollection.find().count();
        if (total <= 0) {
            return;
        }
        int pageSize = 2000;
        int pages = this.getPage(total, pageSize);
        TbCompany company;
        for (int i = 1; i <= pages; i++) {
            DBCursor dbCursor = dbCollection.find().skip((i - 1) * pageSize).limit(end).batchSize(pageSize);
            while (dbCursor.hasNext()) {
                DBObject dbObject = dbCursor.next();
                Map map = dbObject.toMap();
                TbShuiliCredit shuiliCredit = new TbShuiliCredit();
                shuiliCredit.setComName(MapUtils.getString(map,"company"));
                company = tbCompanyMapper.queryCompanyDetail(shuiliCredit.getComName());
                if (null == company){
                    continue;
                }
                shuiliCredit.setComId(company.getComId());
                shuiliCredit.setCreditType(MapUtils.getString(map,"applicationType"));
                shuiliCredit.setLevel(MapUtils.getString(map,"creditRat"));
                shuiliCredit.setYears(MapUtils.getInteger(map,"evalYear"));
                shuiliCredit.setIssued(MapUtils.getString(map,"awardTime"));
                shuiliCredit.setValied(MapUtils.getString(map,"validity"));
                tbShuiliCreditMapper.insert(shuiliCredit);
            }
        }
    }

    private Integer getPage(Integer total, Integer pageSize) {
        Integer pages = 0;
        if (total % pageSize == 0) {
            pages = total / pageSize;
        } else {
            pages = (total / pageSize) + 1;
        }
        return pages;
    }

}
