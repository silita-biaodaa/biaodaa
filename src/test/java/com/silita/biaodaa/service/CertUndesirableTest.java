package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.CertUndesirableMapper;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.model.CertUndesirable;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.utils.MyDateUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

/**
 * @author zhushuai
 * @Created 2020/7/16.
 */
public class CertUndesirableTest extends ConfigTest {

    @Autowired
    private TbCompanyMapper tbCompanyMapper;
    @Autowired
    private CertUndesirableMapper certUndesirableMapper;

    @org.junit.Test
    public void analysisCertUndesriable() throws Exception {
        File file = new File("E:\\朱帅\\耀邦\\奖项\\湖南省不良记录情况20200513.xlsx");
        FileInputStream inputStream = new FileInputStream(file);
        XSSFWorkbook hssfWorkbook = new XSSFWorkbook(inputStream);
        if (null != hssfWorkbook) {
            XSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            XSSFRow row = null;
            XSSFCell cell = null;
            TbCompany company;
            CertUndesirable certUndesirable;
            for (int i = 2; i <= rowCount; i++) {
                row = sheet.getRow(i);
                cell = row.getCell(1);
                company = tbCompanyMapper.queryCompanyDetail(cell.getStringCellValue());
                if (null == company) {
                    continue;
                }
                certUndesirable = new CertUndesirable();
                certUndesirable.setSrcuuid(company.getComName());
                certUndesirable.setComId(company.getComId());
                certUndesirable.setZhuuid("2be3538d-0000-11e5-a0ed-5d9dfa106aaa");
                cell = row.getCell(2);
                certUndesirable.setProjectname(cell.getStringCellValue());
                cell = row.getCell(3);
                certUndesirable.setActioncode(cell.getStringCellValue());
                cell = row.getCell(4);
                certUndesirable.setBadbehaviorcontent(cell.getStringCellValue());
                cell = row.getCell(5);
                certUndesirable.setNature(cell.getStringCellValue());
                cell = row.getCell(9);
                if (null != cell) {
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        Date isue = cell.getDateCellValue();
                        certUndesirable.setPublishdate(MyDateUtils.getDate(isue, "yyyy-MM-dd"));
                    }
                }
                cell = row.getCell(10);
                certUndesirable.setPublishsite(cell.getStringCellValue());
                cell = row.getCell(12);
                if (null != cell) {
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        Date isue = cell.getDateCellValue();
                        certUndesirable.setValiditydate(MyDateUtils.getDate(isue, "yyyy-MM-dd"));
                    }
                }
                certUndesirable.setReview("湖南省建筑市场责任主体" + certUndesirable.getNature() + "不良行为记录");
                certUndesirableMapper.insertCertUndesirable(certUndesirable);
            }
        }
    }

}
