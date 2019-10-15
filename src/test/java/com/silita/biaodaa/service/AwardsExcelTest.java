package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbAwardNationwideMapper;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.model.TbAwardNationwide;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.utils.MyDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

/**
 * Created by zhushuai on 2019/8/8.
 */
public class AwardsExcelTest extends ConfigTest {

    @Autowired
    TbAwardNationwideMapper tbAwardNationwideMapper;
    @Autowired
    TbCompanyMapper tbCompanyMapper;

    @org.junit.Test
    public void analysisAwardExcel() throws Exception {
        File file = new File("E:\\朱帅\\耀邦\\奖项\\奖项(1)(3).xlsx");
        FileInputStream inputStream = new FileInputStream(file);
        XSSFWorkbook hssfWorkbook = new XSSFWorkbook(inputStream);
        if (null != hssfWorkbook) {
            XSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            XSSFRow row = null;
            XSSFCell cell = null;
            TbCompany company;
            for (int i = 1; i <= rowCount; i++) {
                TbAwardNationwide tbAwardNationwide = new TbAwardNationwide();
                row = sheet.getRow(i);
                cell = row.getCell(0);
                if (null != cell && !"国家级".equals(cell.getStringCellValue())) {
                    tbAwardNationwide.setSource(cell.getStringCellValue());
                }
                cell = row.getCell(1);
                tbAwardNationwide.setAwardCate(cell.getStringCellValue());
                cell = row.getCell(2);
                tbAwardNationwide.setProjName(cell.getStringCellValue());
                cell = row.getCell(3);
                if (null != cell) {
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    tbAwardNationwide.setAcreage(cell.getStringCellValue());
                }
                cell = row.getCell(4);
                StringBuffer unitName = new StringBuffer(cell.getStringCellValue());
                String[] unitNames = unitName.toString().split("、");
                if (unitName.length() > 1) {
                    StringBuffer unitOrg = new StringBuffer();
                    for (String unit : unitNames) {
                        if (unit.contains("公司")) {
                            StringBuffer subUnit = new StringBuffer(unit.substring(0, unit.indexOf("公司") + 2));
                            company = tbCompanyMapper.queryCompanyDetail(subUnit.toString());
                        } else {
                            company = tbCompanyMapper.queryCompanyDetail(unit);
                        }
                        if (null != company) {
                            unitOrg.append(unit + "/" + company.getComId()).append(",");
                        } else {
                            tbAwardNationwideMapper.insertNotCompany(unit.toString());
                        }
                    }
                    if (null != unitOrg) {
                        tbAwardNationwide.setUnitOrg(StringUtils.strip(unitOrg.toString(), ","));
                    } else {
                        continue;
                    }
                } else {
                    StringBuffer subUnit = new StringBuffer(unitName.substring(0, unitName.indexOf("公司") + 2));
                    company = tbCompanyMapper.queryCompanyDetail(subUnit.toString());
                    if (null != company) {
                        tbAwardNationwide.setUnitOrg(unitName + "/" + company.getComId());
                    } else {
                        continue;
                    }
                }
                cell = row.getCell(5);
                if (null != cell && StringUtils.isNotEmpty(cell.getStringCellValue())) {
                    tbAwardNationwide.setProjManager(cell.getStringCellValue());
                }
                cell = row.getCell(6);
                if (null != cell && StringUtils.isNotEmpty(cell.getStringCellValue())) {
                    tbAwardNationwide.setSkillManager(cell.getStringCellValue());
                }
                cell = row.getCell(7);
                if (null != cell && StringUtils.isNotEmpty(cell.getStringCellValue())) {
                    tbAwardNationwide.setQualityManager(cell.getStringCellValue());
                }
                cell = row.getCell(8);
                if (null != cell && StringUtils.isNotEmpty(cell.getStringCellValue())) {
                    tbAwardNationwide.setTechQualManage(cell.getStringCellValue());
                }
                cell = row.getCell(9);
                tbAwardNationwide.setBuildOrg(StringUtils.strip(setOrg(cell), ","));
                cell = row.getCell(10);
                tbAwardNationwide.setSuperOrg(StringUtils.strip(setOrg(cell), ","));
                cell = row.getCell(11);
                if (null != cell && StringUtils.isNotEmpty(cell.getStringCellValue())) {
                    tbAwardNationwide.setSuperPerson(cell.getStringCellValue());
                }
                cell = row.getCell(12);
                tbAwardNationwide.setExploreOrg(StringUtils.strip(setOrg(cell), ","));
                cell = row.getCell(13);
                tbAwardNationwide.setDesignOrg(StringUtils.strip(setOrg(cell), ","));
                cell = row.getCell(14);
                tbAwardNationwide.setCheckOrg(StringUtils.strip(setOrg(cell), ","));
                cell = row.getCell(15);
                if (null != cell && StringUtils.isNotEmpty(cell.getStringCellValue())) {
                    String[] joinOrgs = cell.getStringCellValue().split("、");
                    StringBuffer sbfJoin = new StringBuffer();
                    for (String join : joinOrgs) {
                        StringBuffer subUnit = new StringBuffer(join.substring(0, join.indexOf("公司") + 2));
                        company = tbCompanyMapper.queryCompanyDetail(subUnit.toString());
                        if (null != company) {
                            sbfJoin.append(join + "/" + company.getComId()).append(",");
                        } else {
                            sbfJoin.append(join).append(",");
                            tbAwardNationwideMapper.insertNotCompany(join);
                        }
                    }
                    tbAwardNationwide.setJoinOrg(StringUtils.strip(sbfJoin.toString(), ","));
                }
                cell = row.getCell(16);
                if (null != cell && StringUtils.isNotEmpty(cell.getStringCellValue())) {
                    tbAwardNationwide.setRemark(cell.getStringCellValue());
                }
                cell = row.getCell(17);
                if (null != cell && StringUtils.isNotEmpty(cell.getStringCellValue())) {
                    tbAwardNationwide.setProjType(cell.getStringCellValue());
                }
                cell = row.getCell(18);
                if (null != cell && StringUtils.isNotEmpty(cell.getStringCellValue())) {
                    tbAwardNationwide.setAwardName(cell.getStringCellValue());
                }
                cell = row.getCell(19);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                if (null != cell && StringUtils.isNotEmpty(cell.getStringCellValue())) {
                    tbAwardNationwide.setPubOrg(cell.getStringCellValue());
                }
                cell = row.getCell(20);
                if (null != cell) {
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        Date isue = cell.getDateCellValue();
                        tbAwardNationwide.setIssued(MyDateUtils.getDate(isue, "yyyy-MM-dd"));
                    }
                }
                tbAwardNationwideMapper.insertAward(tbAwardNationwide);
            }
        }
    }

    private String setOrg(Cell cell) {
        TbCompany company;
        if (null != cell && StringUtils.isNotEmpty(cell.getStringCellValue())) {
            String[] buildOrgs = cell.getStringCellValue().split("、");
            StringBuffer sbfBuild = new StringBuffer();
            for (String build : buildOrgs) {
                StringBuffer subUnit = new StringBuffer(build.substring(0, build.indexOf("公司") + 2));
                company = tbCompanyMapper.queryCompanyDetail(subUnit.toString());
                if (null != company) {
                    sbfBuild.append(build + "/" + company.getComId()).append(",");
                } else {
                    sbfBuild.append(build).append(",");
                }
            }
            return sbfBuild.toString();
        }
        return null;
    }

    public static void main(String[] args) {
        String comName = "北京住六欣跃机电安装工程有限公司（机电工程安装）";
        System.out.println(comName.substring(0, comName.indexOf("公司") + 2));
    }

    @org.junit.Test
    public void analysisCompanyExcel() throws Exception {
        File file = new File("C:\\Users\\dh\\Desktop\\企业.xlsx");
        FileInputStream inputStream = new FileInputStream(file);
        XSSFWorkbook hssfWorkbook = new XSSFWorkbook(inputStream);
        if (null != hssfWorkbook) {
            XSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            XSSFRow row = null;
            XSSFCell cell = null;
            TbCompany company;
            String comName;
            String newComName;
            for (int i = 0; i < rowCount; i++) {
                row = sheet.getRow(i);
                cell = row.getCell(0);
                if (null != cell && StringUtils.isNotEmpty(cell.getStringCellValue())) {
                    comName = cell.getStringCellValue();
                    newComName = comName.substring(0, comName.lastIndexOf("司") + 1);
                    company = tbCompanyMapper.queryCompanyDetail(newComName);
                    if (null != company) {
                        continue;
                    }
//                    tbCompanyMapper.insertNotCompany(newComName);
                }
            }
        }
    }
}
