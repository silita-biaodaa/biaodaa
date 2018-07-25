package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSON;
import com.silita.biaodaa.dao.TbCompanyInfoMapper;
import com.silita.biaodaa.model.TbCompanyInfo;
import com.silita.biaodaa.utils.ProjectAnalysisUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class XmlImportTest extends ConfigTest {

    @Autowired
    TbCompanyInfoService tbCompanyInfoService;

    @Test
    public void test() throws Exception {
        File file = new File("F:\\Company\\数据\\天眼查（重庆）.xls");
        FileInputStream inputStream = new FileInputStream(file);
        String fileName = file.getName();
        String provice = fileName.substring(ProjectAnalysisUtil.getIndex(fileName, "（") + 1, ProjectAnalysisUtil.getIndex(fileName, "）"));
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
        if (null != hssfWorkbook) {
            List<TbCompanyInfo> comList = new ArrayList<>();
            TbCompanyInfo companyInfo = null;
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            int cellCount = 0;
            HSSFRow row = null;
            HSSFCell cell = null;
            Map<String, Object> param = new HashMap<>();
            for (int i = 1; i <= rowCount; i++) {
                row = null;
                row = sheet.getRow(i);
                companyInfo = new TbCompanyInfo();
                param.put("time", System.currentTimeMillis());
                param.put("date", new Date());
                companyInfo.setTrade("建筑业");
                cell = row.getCell(0);
                companyInfo.setComName(cell.getStringCellValue().trim());
                param.put("comName", companyInfo.getComName());
                companyInfo.setPkid(this.md5(JSON.toJSONString(param)));
                cell = row.getCell(1);
                if (null != cell.getStringCellValue()) {
                    companyInfo.setLegalPerson(cell.getStringCellValue().trim());
                }
                cell = row.getCell(2);
                if (null != cell.getStringCellValue() && !"-".equals(cell.getStringCellValue())) {
                    companyInfo.setRegisCapital(cell.getStringCellValue().trim());
                }
                cell = row.getCell(3);
                if (null != cell.getStringCellValue()) {
                    companyInfo.setRegisDate(cell.getStringCellValue().trim());
                }
                companyInfo.setProvince(provice);
                cell = row.getCell(4);
                if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                    companyInfo.setCity(cell.getStringCellValue().trim());
                }
                cell = row.getCell(5);
                if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                    companyInfo.setCreditCode(cell.getStringCellValue().trim());
                }
                cell = row.getCell(6);
                if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                    companyInfo.setPhone(cell.getStringCellValue().trim());
                }
                cell = row.getCell(7);
                if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                    companyInfo.setComAddress(cell.getStringCellValue().trim());
                }
                cell = row.getCell(8);
                if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                    companyInfo.setComUrl(cell.getStringCellValue().trim());
                }
                cell = row.getCell(9);
                if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                    companyInfo.setEmail(cell.getStringCellValue().trim());
                }
                cell = row.getCell(10);
                if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                    companyInfo.setScope(cell.getStringCellValue().trim());
                }
                comList.add(companyInfo);
            }

            if (null != comList && comList.size() > 0) {
//                tbCompanyInfoMapper.batchInsertCompanyInfo(comList);
                tbCompanyInfoService.saveCompanyInfo(comList);
            }
        }
    }


    /**
     * 生成MD5
     *
     * @param content
     * @return
     */
    public String md5(String content) {
        return DigestUtils.md5Hex(content);
    }

    @Test
    public void test1() {
        String data = Long.toString(System.currentTimeMillis()) + new Date();
        System.out.println(this.md5(data));
    }
}

