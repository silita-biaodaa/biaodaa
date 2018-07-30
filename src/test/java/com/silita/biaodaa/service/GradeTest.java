package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbZzGradeMapper;
import com.silita.biaodaa.model.AptitudeDictionary;
import com.silita.biaodaa.model.TbZzGrade;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradeTest extends ConfigTest {

    @Autowired
    TbZzGradeMapper tbZzGradeMapper;
    @Autowired
    GradeService gradeService;

    @Test
    public void test() throws Exception {
        File file = new File("F:\\Company\\数据\\公告.xls");
        FileInputStream inputStream = new FileInputStream(file);
        String fileName = file.getName();
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
        if (null != hssfWorkbook) {
            List<TbZzGrade> zzGrades = new ArrayList<>();
            TbZzGrade grade = null;
            AptitudeDictionary aptitudeDictionary = null;
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            HSSFRow row = null;
            HSSFCell cell = null;
            Map<String, Object> param = new HashMap<>();
            for (int i = 0; i < rowCount; i++) {
                aptitudeDictionary = null;
                grade = new TbZzGrade();
                row = null;
                row = sheet.getRow(i);
                cell = row.getCell(0);
                param.put("name", cell.getStringCellValue().trim());
                aptitudeDictionary = tbZzGradeMapper.queryApti(param);
                if (cell.getStringCellValue().contains("-")) {
                    param.put("name", cell.getStringCellValue().trim().split("-")[1]);
                    param.put("aptitudeName", cell.getStringCellValue().split("-")[0]);
                    aptitudeDictionary = tbZzGradeMapper.queryApti(param);
                    grade.setGradeName("全部");
                    grade.setGradeType(1);
                    grade.setGradeUuid(aptitudeDictionary.getMajoruuid());
                    grade.setZzId(aptitudeDictionary.getId());
                    zzGrades.add(grade);
                    param.remove("aptitudeName");
                    continue;
                } else {
                    aptitudeDictionary = tbZzGradeMapper.queryApti(param);
                    if ("".equals(row.getCell(1).getStringCellValue())) {
                        grade = new TbZzGrade();
                        grade.setGradeType(1);
                        grade.setGradeName("全部");
                        grade.setGradeUuid(aptitudeDictionary.getMajoruuid());
                        grade.setZzId(aptitudeDictionary.getId());
                        zzGrades.add(grade);
                        continue;
                    } else {
                        if(null != aptitudeDictionary){
                            cell = row.getCell(1);
                            if (null != cell && !"".equals(cell.getStringCellValue())) {
                                grade = new TbZzGrade();
                                grade.setGradeType(1);
                                grade.setGradeName(cell.getStringCellValue());
                                grade.setZzId(aptitudeDictionary.getId());
                                grade.setGradeUuid(this.getUuid(aptitudeDictionary.getMajoruuid(), aptitudeDictionary.getZztype(), cell.getStringCellValue().trim()));
                                zzGrades.add(grade);
                            }
                            cell = row.getCell(2);
                            if (null != cell && !"".equals(cell.getStringCellValue())) {
                                grade = new TbZzGrade();
                                grade.setGradeType(1);
                                grade.setGradeName(cell.getStringCellValue());
                                grade.setZzId(aptitudeDictionary.getId());
                                grade.setGradeUuid(this.getUuid(aptitudeDictionary.getMajoruuid(), aptitudeDictionary.getZztype(), cell.getStringCellValue().trim()));
                                zzGrades.add(grade);
                            }
                            cell = row.getCell(3);
                            if (null != cell && !"".equals(cell.getStringCellValue())) {
                                grade = new TbZzGrade();
                                grade.setGradeType(1);
                                grade.setGradeName(cell.getStringCellValue());
                                grade.setZzId(aptitudeDictionary.getId());
                                grade.setGradeUuid(this.getUuid(aptitudeDictionary.getMajoruuid(), aptitudeDictionary.getZztype(), cell.getStringCellValue().trim()));
                                zzGrades.add(grade);
                            }
                        }
                    }
                }
            }
            if (zzGrades != null) {
                gradeService.getZzTpyeList(zzGrades);
            }
        }
    }

    private String getUuid(String majoruuid, String zzType, String grade) {
        String uuid = null;
//        if ("1".equals(zzType)) {
            //等级：0=特级，1=一级，2=二级，11=一级及以上，21=二级及以上，31=三级及以上，-1=甲级，-2=乙级，-3=丙级
            switch (grade) {
                case "特级":
                    uuid = majoruuid + "/" + "0";
                    break;
                case "一级":
                    uuid = majoruuid + "/" + "1";
                    break;
                case "二级":
                    uuid = majoruuid + "/" + "2";
                    break;
                case "三级":
                    uuid = majoruuid + "/" + "3";
                    break;
                case "一级及以上":
                    uuid = majoruuid + "/" + "11";
                    break;
                case "二级及以上":
                    uuid = majoruuid + "/" + "21";
                    break;
                case "三级及以上":
                    uuid = majoruuid + "/" + "31";
                    break;
                case "甲级":
                    uuid = majoruuid + "/" + "-1";
                    break;
                case "乙级":
                    uuid = majoruuid + "/" + "-2";
                    break;
                case "丙级":
                    uuid = majoruuid + "/" + "-3";
                    break;
                case "丁级":
                    uuid = majoruuid + "/" + "-4";
                    break;
                case "甲级及以上":
                    uuid = majoruuid + "/" + "-11";
                    break;
                case "乙级及以上":
                    uuid = majoruuid + "/" + "-21";
                    break;
                case "丙级及以上":
                    uuid = majoruuid + "/" + "-31";
                    break;
                case "丁级及以上":
                    uuid = majoruuid + "/" + "-41";
                    break;
            }
//        } else if ("2".equals(zzType)) {
//            switch (grade) {
//                case "甲级":
//                    uuid = majoruuid + "/" + "-1";
//                    break;
//                case "乙级":
//                    uuid = majoruuid + "/" + "-2";
//                    break;
//                case "丙级":
//                    uuid = majoruuid + "/" + "-3";
//                    break;
//                case "丁级":
//                    uuid = majoruuid + "/" + "-4";
//                    break;
//                case "甲级及以上":
//                    uuid = majoruuid + "/" + "-11";
//                    break;
//                case "乙级及以上":
//                    uuid = majoruuid + "/" + "-21";
//                    break;
//                case "丙级及以上":
//                    uuid = majoruuid + "/" + "-31";
//                    break;
//                case "丁级及以上":
//                    uuid = majoruuid + "/" + "-41";
//                    break;
//            }
//        }
        return uuid;
    }


    @Test
    public void test1() throws Exception {
        File file = new File("F:\\Company\\数据\\企业.xls");
        FileInputStream inputStream = new FileInputStream(file);
        String fileName = file.getName();
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(inputStream);
        if (null != hssfWorkbook) {
            List<TbZzGrade> zzGrades = new ArrayList<>();
            TbZzGrade grade = null;
            AptitudeDictionary aptitudeDictionary = null;
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            int rowCount = sheet.getLastRowNum();
            HSSFRow row = null;
            HSSFCell cell = null;
            Map<String, Object> param = new HashMap<>();
            for (int i = 0; i < rowCount; i++) {
                aptitudeDictionary = null;
                grade = new TbZzGrade();
                row = null;
                row = sheet.getRow(i);
                cell = row.getCell(0);
                param.put("name", cell.getStringCellValue().trim());
                aptitudeDictionary = tbZzGradeMapper.queryApti(param);
                if (cell.getStringCellValue().contains("-")) {
                    param.put("name", cell.getStringCellValue().trim().split("-")[1]);
                    param.put("aptitudeName", cell.getStringCellValue().split("-")[0]);
                    aptitudeDictionary = tbZzGradeMapper.queryApti(param);
                    grade.setGradeName("全部");
                    grade.setGradeType(2);
                    grade.setGradeUuid(aptitudeDictionary.getMajoruuid());
                    grade.setZzId(aptitudeDictionary.getId());
                    zzGrades.add(grade);
                    param.remove("aptitudeName");
                    continue;
                } else {
                    aptitudeDictionary = tbZzGradeMapper.queryApti(param);
                    if ("".equals(row.getCell(1).getStringCellValue())) {
                        grade = new TbZzGrade();
                        grade.setGradeType(2);
                        grade.setGradeName("全部");
                        grade.setGradeUuid(aptitudeDictionary.getMajoruuid());
                        grade.setZzId(aptitudeDictionary.getId());
                        zzGrades.add(grade);
                        continue;
                    } else {
                        if(null != aptitudeDictionary){
                            cell = row.getCell(1);
                            if (null != cell && !"".equals(cell.getStringCellValue())) {
                                grade = new TbZzGrade();
                                grade.setGradeType(2);
                                grade.setGradeName(cell.getStringCellValue());
                                grade.setZzId(aptitudeDictionary.getId());
                                grade.setGradeUuid(this.getUuid(aptitudeDictionary.getMajoruuid(), aptitudeDictionary.getZztype(), cell.getStringCellValue().trim()));
                                zzGrades.add(grade);
                            }
                            cell = row.getCell(2);
                            if (null != cell && !"".equals(cell.getStringCellValue())) {
                                grade = new TbZzGrade();
                                grade.setGradeType(2);
                                grade.setGradeName(cell.getStringCellValue());
                                grade.setZzId(aptitudeDictionary.getId());
                                grade.setGradeUuid(this.getUuid(aptitudeDictionary.getMajoruuid(), aptitudeDictionary.getZztype(), cell.getStringCellValue().trim()));
                                zzGrades.add(grade);
                            }
                            cell = row.getCell(3);
                            if (null != cell && !"".equals(cell.getStringCellValue())) {
                                grade = new TbZzGrade();
                                grade.setGradeType(2);
                                grade.setGradeName(cell.getStringCellValue());
                                grade.setZzId(aptitudeDictionary.getId());
                                grade.setGradeUuid(this.getUuid(aptitudeDictionary.getMajoruuid(), aptitudeDictionary.getZztype(), cell.getStringCellValue().trim()));
                                zzGrades.add(grade);
                            }
                            cell = row.getCell(4);
                            if (null != cell && !"".equals(cell.getStringCellValue())) {
                                grade = new TbZzGrade();
                                grade.setGradeType(2);
                                grade.setGradeName(cell.getStringCellValue());
                                grade.setZzId(aptitudeDictionary.getId());
                                grade.setGradeUuid(this.getUuid(aptitudeDictionary.getMajoruuid(), aptitudeDictionary.getZztype(), cell.getStringCellValue().trim()));
                                zzGrades.add(grade);
                            }
                            cell = row.getCell(5);
                            if (null != cell && !"".equals(cell.getStringCellValue())) {
                                grade = new TbZzGrade();
                                grade.setGradeType(2);
                                grade.setGradeName(cell.getStringCellValue());
                                grade.setZzId(aptitudeDictionary.getId());
                                grade.setGradeUuid(this.getUuid(aptitudeDictionary.getMajoruuid(), aptitudeDictionary.getZztype(), cell.getStringCellValue().trim()));
                                zzGrades.add(grade);
                            }
                            cell = row.getCell(6);
                            if (null != cell && !"".equals(cell.getStringCellValue())) {
                                grade = new TbZzGrade();
                                grade.setGradeType(2);
                                grade.setGradeName(cell.getStringCellValue());
                                grade.setZzId(aptitudeDictionary.getId());
                                grade.setGradeUuid(this.getUuid(aptitudeDictionary.getMajoruuid(), aptitudeDictionary.getZztype(), cell.getStringCellValue().trim()));
                                zzGrades.add(grade);
                            }
                            cell = row.getCell(7);
                            if (null != cell && !"".equals(cell.getStringCellValue())) {
                                grade = new TbZzGrade();
                                grade.setGradeType(2);
                                grade.setGradeName(cell.getStringCellValue());
                                grade.setZzId(aptitudeDictionary.getId());
                                grade.setGradeUuid(this.getUuid(aptitudeDictionary.getMajoruuid(), aptitudeDictionary.getZztype(), cell.getStringCellValue().trim()));
                                zzGrades.add(grade);
                            }
                        }
                    }
                }
            }
            if (zzGrades != null) {
                gradeService.getZzTpyeList(zzGrades);
            }
        }
    }
}
