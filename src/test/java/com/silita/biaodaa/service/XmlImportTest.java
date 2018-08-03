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
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class XmlImportTest extends ConfigTest {

    @Autowired
    TbCompanyInfoService tbCompanyInfoService;


    public void test(File file) throws Exception {
//        File file = new File("F:\\data\\江西-广西\\广西-建筑\\天眼查(广西)(W20080327171533264902113).xls");
        FileInputStream inputStream = new FileInputStream(file);
        String fileName = file.getName();
//        String provice = fileName.substring(ProjectAnalysisUtil.getIndex(fileName, "(") + 1, ProjectAnalysisUtil.getIndex(fileName, ")"));
        String provice = "江西省";
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
        List<String> companyList = new ArrayList<>();
        if (null != hssfWorkbook) {
            List<TbCompanyInfo> comList = new ArrayList<>();
            TbCompanyInfo companyInfo = null;
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
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
                companyInfo.setTabCode(this.getCode(provice));
                companyInfo.setComName(cell.getStringCellValue().trim());
                if ("".equals(companyInfo.getComName()) || companyList.contains(companyInfo.getComName())) {
                    continue;
                }
                companyList.add(companyInfo.getComName());
                param.put("comName", companyInfo.getComName());
                companyInfo.setPkid(this.md5(JSON.toJSONString(param)));
                cell = row.getCell(1);
                if (null != cell && null != cell.getStringCellValue()) {
                    companyInfo.setLegalPerson(cell.getStringCellValue().trim());
                }
                cell = row.getCell(2);
                if (null != cell && null != cell.getStringCellValue() && !"-".equals(cell.getStringCellValue())) {
                    companyInfo.setRegisCapital(cell.getStringCellValue().trim());
                }
                cell = row.getCell(3);
                if (null != cell && null != cell.getStringCellValue()) {
                    companyInfo.setRegisDate(cell.getStringCellValue().trim());
                }
                companyInfo.setProvince(provice);
                cell = row.getCell(4);
                if (null != cell && StringUtils.isNotBlank(cell.getStringCellValue())) {
                    companyInfo.setCity(cell.getStringCellValue().trim());
                }
                cell = row.getCell(5);
                if (null != cell && StringUtils.isNotBlank(cell.getStringCellValue())) {
                    companyInfo.setCreditCode(cell.getStringCellValue().trim());
                }
                cell = row.getCell(6);
                if (null != cell && StringUtils.isNotBlank(cell.getStringCellValue())) {
                    companyInfo.setPhone(cell.getStringCellValue().trim());
                }
                cell = row.getCell(7);
                if (null != cell && StringUtils.isNotBlank(cell.getStringCellValue())) {
                    companyInfo.setComAddress(cell.getStringCellValue().trim());
                }
                cell = row.getCell(8);
                if (null != cell && StringUtils.isNotBlank(cell.getStringCellValue())) {
                    companyInfo.setComUrl(cell.getStringCellValue().trim());
                }
                cell = row.getCell(9);
                if (null != cell && StringUtils.isNotBlank(cell.getStringCellValue())) {
                    companyInfo.setEmail(cell.getStringCellValue().trim());
                }
                cell = row.getCell(10);
                if (null != cell && StringUtils.isNotBlank(cell.getStringCellValue())) {
                    companyInfo.setScope(cell.getStringCellValue().trim());
                }
                comList.add(companyInfo);
            }

            if (null != comList && comList.size() > 0) {
//                tbCompanyInfoMapper.batchInsertCompanyInfo(comList);
                tbCompanyInfoService.saveCompanyInfo(comList);
                companyList = new ArrayList<>();
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

    private String getCode(String province) {
        String tabCode = "";
        switch (province) {
            case "广西壮族自治区":
                tabCode = "guangx";
                break;
            case "江西省":
                tabCode = "jiangx";
                break;
            case "贵州省":
                tabCode = "guiz";
                break;
        }
        return tabCode;
    }

    @Test
    public void test1() {
        String data = Long.toString(System.currentTimeMillis()) + new Date();
        System.out.println(this.md5(data));
    }

    @Test
    public void getFile(){
        File file = new File("F:\\data\\江西-广西\\江西");
        File[]  files = file.listFiles();
        for(File f : files){
            System.out.println(f.getName());
            try {
                test(f);
            } catch (Exception e) {
                System.out.println(f.getName());
                e.printStackTrace();
                break;
            }
        }

    }


    @Test
    public void test2() throws Exception {
        File file = new File("F:\\Company\\数据\\11072【湖南子任建设有限公司提供企业名单】7.25.xls");
        FileInputStream inputStream = new FileInputStream(file);
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
                cell = row.getCell(0);
                companyInfo.setComName(cell.getStringCellValue().trim());
                param.put("comName", companyInfo.getComName());
                companyInfo.setPkid(this.md5(JSON.toJSONString(param)));
                cell = row.getCell(1);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (null != cell.getStringCellValue()) {
                        companyInfo.setProvince(cell.getStringCellValue().trim());
                    } else {
                        companyInfo.setProvince("湖南");
                    }
                } else {
                    companyInfo.setProvince("湖南");
                }
                cell = row.getCell(2);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (null != cell.getStringCellValue() && !"-".equals(cell.getStringCellValue()) && !"(NULL)".equals(cell.getStringCellValue())) {
                        companyInfo.setBusinessNum(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(3);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (null != cell.getStringCellValue()) {
                        companyInfo.setComAddress(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(4);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setLegalPerson(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(5);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setStatus(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(6);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setRegisDate(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(7);
                if (cell != null) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (StringUtils.isNotBlank(cell.getStringCellValue()) && !"0".equals(cell.getStringCellValue())) {
                        companyInfo.setRegisCapital(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(8);
                if (null != cell) {
                    if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setComType(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(9);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setCreditCode(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(10);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setCheckOrg(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(11);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setScope(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(12);
                if (cell != null) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (StringUtils.isNotBlank(cell.getStringCellValue()) && !"0".equals(cell.getStringCellValue())) {
                        if (cell.getStringCellValue().contains("至")) {
                            String[] strs = cell.getStringCellValue().split("至");
                            companyInfo.setStartDate(strs[0]);
                            companyInfo.setEndDate(strs[1]);
                        } else {
                            companyInfo.setStartDate(cell.getStringCellValue());
                        }
                    }
                }
                cell = row.getCell(13);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setPhone(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(14);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setTrade(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(15);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (StringUtils.isNotBlank(cell.getStringCellValue()) && !"(NULL)".equals(cell.getStringCellValue())) {
                        companyInfo.setComUrl(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(16);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setOrgCode(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(17);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (StringUtils.isNotBlank(cell.getStringCellValue()) && !"无".equals(cell.getStringCellValue())) {
                        companyInfo.setEmail(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(18);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if (StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setCheckDate(cell.getStringCellValue().trim());
                    }
                }
                comList.add(companyInfo);
            }

            if (null != comList && comList.size() > 0) {
//                tbCompanyInfoMapper.batchInsertCompanyInfo(comList);
                tbCompanyInfoService.saveCompanyInfo(comList);
            }
        }
    }
}

