package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSON;
import com.silita.biaodaa.model.TbCompanyInfo;
import com.silita.biaodaa.utils.ProjectAnalysisUtil;
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
//         province = fileName.substring(ProjectAnalysisUtil.getIndex(fileName,"-")+1,ProjectAnalysisUtil.getIndex(fileName,"-")+3);
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
//                companyInfo.setTrade("建筑业");
                cell = row.getCell(0);
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
                companyInfo.setTabCode(this.getCode(province));
                companyInfo.setProvince(this.getProvince(province));
                cell = row.getCell(4);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    if(StringUtils.isNotBlank(cell.getStringCellValue())) {
                        companyInfo.setCity(cell.getStringCellValue().trim());
//                        companyInfo.setProvince(tbCompanyInfoService.getProvince(companyInfo.getCity()));
//                        if(null == companyInfo.getProvince()){
//                            companyInfo.setTabCode(this.getCode("湖南"));
//                        }else {
//                            companyInfo.setTabCode(this.getCode(companyInfo.getProvince()));
//                        }
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
            case "广西":
                tabCode = "guangx";
                province = "广西壮族自治区";
                break;
            case "江西":
                tabCode = "jiangx";
                province = "江西省";
                break;
            case "贵州":
                tabCode = "guiz";
                province = "贵州省";
                break;
            case "吉林":
                tabCode = "jil";
                province = "吉林省";
                break;
            case "河北":
                tabCode = "hebei";
                province = "河北省";
                break;
            case "四川":
                tabCode = "sichuan";
                province = "四川省";
                break;
            case "天津":
                tabCode = "tianj";
                province = "天津市";
                break;
            case "甘肃":
                tabCode = "gans";
                province = "甘肃省";
                break;
            case "黑龙":
                tabCode = "heilj";
                province = "黑龙江省";
                break;
            case "青海":
                tabCode = "qingh";
                province = "青海省";
                break;
            case "西藏":
                tabCode = "xizang";
                province = "西藏自治区";
                break;
            case "安徽":
                tabCode = "anh";
                province = "安徽省";
                break;
            case "北京":
                tabCode = "beij";
                province = "北京市";
                break;
            case "福建":
                tabCode = "fuj";
                province = "福建";
                break;
            case "浙江":
                tabCode = "zhej";
                province = "浙江省";
                break;
            case "河南":
                tabCode = "henan";
                province = "河南省";
                break;
            case "江苏":
                tabCode = "jiangs";
                province = "江苏省";
                break;
            case "内蒙":
                tabCode = "neimg";
                province = "内蒙古自治区";
                break;
            case "宁夏":
                tabCode = "ningx";
                province = "宁夏回族自治区";
                break;
            case "山东":
                tabCode = "shand";
                province = "山东省";
                break;
            case "山西":
                tabCode = "sanx";
                province = "山西省";
                break;
            case "海南":
                tabCode = "hain";
                province = "海南";
                break;
            case "上海":
                tabCode = "shangh";
                province = "上海市";
                break;
            case "广东":
                tabCode = "guangd";
                province = "广东省";
                break;
            case "新疆":
                tabCode = "xinjiang";
                province = "新疆维吾尔自治区";
                break;
            case "云南":
                tabCode = "yunn";
                province = "云南省";
                break;
            case "陕西":
                tabCode = "shanxi";
                province = "陕西省";
                break;
            case "湖北":
                tabCode = "hubei";
                province = "湖北省";
                break;
            case "重庆":
                tabCode = null;
                province = "重庆市";
                break;
            case "湖南":
                tabCode = null;
                province = "湖南省";
                break;
            case "辽宁":
                tabCode = null;
                province = "辽宁省";
                break;
                default:
                    tabCode = null;
                    break;

        }
        return tabCode;
    }

    public String  getProvince(String province){
        switch (province) {
            case "广西":
                province = "广西壮族自治区";
                break;
            case "江西":
                province = "江西省";
                break;
            case "贵州":
                province = "贵州省";
                break;
            case "吉林":
                province = "吉林省";
                break;
            case "河北":
                province = "河北省";
                break;
            case "四川":
                province = "四川省";
                break;
            case "天津":
                province = "天津市";
                break;
            case "甘肃":
                province = "甘肃省";
                break;
            case "黑龙":
                province = "黑龙江省";
                break;
            case "青海":
                province = "青海省";
                break;
            case "西藏":
                province = "西藏自治区";
                break;
            case "安徽":
                province = "安徽省";
                break;
            case "北京":
                province = "北京市";
                break;
            case "福建":
                province = "福建";
                break;
            case "浙江":
                province = "浙江省";
                break;
            case "河南":
                province = "河南省";
                break;
            case "江苏":
                province = "江苏省";
                break;
            case "内蒙":
                province = "内蒙古自治区";
                break;
            case "宁夏":
                province = "宁夏回族自治区";
                break;
            case "山东":
                province = "山东省";
                break;
            case "山西":
                province = "山西省";
                break;
            case "海南":
                province = "海南";
                break;
            case "上海":
                province = "上海市";
                break;
            case "广东":
                province = "广东省";
                break;
            case "新疆":
                province = "新疆维吾尔自治区";
                break;
            case "云南":
                province = "云南省";
                break;
            case "陕西":
                province = "陕西省";
                break;
            case "湖北":
                province = "湖北省";
                break;
            case "重庆":
                province = "重庆市";
                break;
            case "湖南":
                province = "湖南省";
                break;
            case "辽宁":
                province = "辽宁省";
                break;

        }
        return province;
    }
    @Test
    public void test1() {
        String data = Long.toString(System.currentTimeMillis()) + new Date();
        System.out.println(this.md5(data));
    }

    @Test
    public void getFile(){
        File file = new File("F:\\data\\江苏");
        File[]  files = file.listFiles();
        for(File f : files){
            System.out.println(f.getName());
            try {
                test(f,"江苏");
            } catch (Exception e) {
                System.out.println(f.getName());
                e.printStackTrace();
                break;
            }
        }
    }

    @Test
    public void test2(){
        String str = "建筑劳务-安徽（0～20）.xlsx";
        String dddd = str.substring(ProjectAnalysisUtil.getIndex(str,"-")+1,ProjectAnalysisUtil.getIndex(str,"-")+3);
        System.out.println(dddd);
    }
}
