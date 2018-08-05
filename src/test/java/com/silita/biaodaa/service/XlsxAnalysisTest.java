package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSON;
import com.silita.biaodaa.model.TbCompanyInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class XlsxAnalysisTest extends ConfigTest{

    @Autowired
    TbCompanyInfoService tbCompanyInfoService;


    public void test(File file, String province) throws Exception {
//        File file = new File("F:\\data\\江西-广西\\广西-建筑\\天眼查(广西)(W20080327171533264902113).xls");
        FileInputStream inputStream = new FileInputStream(file);
        String fileName = file.getName();
//        String provice = fileName.substring(ProjectAnalysisUtil.getIndex(fileName, "(") + 1, ProjectAnalysisUtil.getIndex(fileName, ")"));
//        String provice = "江西省";
        Workbook hssfWorkbook = new XSSFWorkbook(inputStream);
        List<String> companyList = new ArrayList<>();
        if (null != hssfWorkbook) {
            List<TbCompanyInfo> comList = new ArrayList<>();
            TbCompanyInfo companyInfo = null;
            Sheet sheet = hssfWorkbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            Row row = null;
            Cell cell = null;
            Map<String, Object> param = new HashMap<>();
            for (int i = 2; i <= rowCount; i++) {
                row = null;
                row = sheet.getRow(i);
                companyInfo = new TbCompanyInfo();
                param.put("time", System.currentTimeMillis());
                param.put("date", new Date());
                companyInfo.setTrade("建筑业");
                cell = row.getCell(0);
                companyInfo.setTabCode(this.getCode(province));
                if(null != cell){
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if(null != cell.getStringCellValue()) {
                        companyInfo.setComName(cell.getStringCellValue().trim());
                    }
                }
                if ("".equals(companyInfo.getComName()) || companyList.contains(companyInfo.getComName())) {
                    continue;
                }
                companyList.add(companyInfo.getComName());
                param.put("comName", companyInfo.getComName());
                companyInfo.setPkid(this.md5(JSON.toJSONString(param)));
                cell = row.getCell(1);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if(null != cell.getStringCellValue()) {
                        companyInfo.setLegalPerson(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(2);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if(null != cell.getStringCellValue() && !"-".equals(cell.getStringCellValue())) {
                        companyInfo.setRegisCapital(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(3);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if(null != cell.getStringCellValue()) {
                        companyInfo.setRegisDate(cell.getStringCellValue().trim());
                    }
                }
                companyInfo.setProvince(province);
                cell = row.getCell(4);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if(StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setCity(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(5);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if(StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setCreditCode(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(6);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if(StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setPhone(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(7);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if(StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setComAddress(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(8);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if(StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setComUrl(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(9);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if(StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setEmail(cell.getStringCellValue().trim());
                    }
                }
                cell = row.getCell(10);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if(StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setScope(cell.getStringCellValue().trim());
                    }
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
            case "吉林省":
                tabCode = "jil";
                break;
            case "河北省":
                tabCode = "hebei";
                break;
            case "四川省":
                tabCode = "sichuan";
                break;
            case "天津市":
                tabCode = "tianj";
                break;
            case "甘肃省":
                tabCode = "gans";
                break;
            case "黑龙江省":
                tabCode = "heilj";
                break;
            case "青海省":
                tabCode = "qingh";
                break;
            case "西藏自治区":
                tabCode = "xizang";
                break;
            case "安徽省":
                tabCode = "anh";
                break;
            case "北京市":
                tabCode = "beij";
                break;
            case "福建省":
                tabCode = "fuj";
                break;
            case "浙江省":
                tabCode = "zhej";
                break;
            case "河南省":
                tabCode = "henan";
                break;
            case "江苏省":
                tabCode = "jiangs";
                break;
            case "内蒙古自治区":
                tabCode = "neimg";
                break;
            case "宁夏回族自治区":
                tabCode = "ningx";
                break;
            case "山东省":
                tabCode = "shand";
                break;
            case "山西省":
                tabCode = "sanx";
                break;
            case "海南省":
                tabCode = "hain";
                break;
            case "上海市":
                tabCode = "shangh";
                break;
            case "广东省":
                tabCode = "guangd";
                break;
            case "新疆维吾尔自治区":
                tabCode = "xinjiang";
                break;
            case "云南省":
                tabCode = "yunn";
                break;
            case "陕西省":
                tabCode = "shanxi";
                break;
            case "湖北省":
                tabCode = "hubei";
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
        File file = new File("F:\\data\\云南省");
        File[]  files = file.listFiles();
        for(File f : files){
            System.out.println(f.getName());
            try {
                test(f,"云南省");
            } catch (Exception e) {
                System.out.println(f.getName());
                e.printStackTrace();
                break;
            }
        }

    }
}
