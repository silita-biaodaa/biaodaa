    package com.silita.biaodaa.service;

    import com.silita.biaodaa.dao.TbPersonMapper;
    import org.apache.commons.collections.map.HashedMap;
    import org.apache.commons.lang3.StringUtils;
    import org.apache.poi.ss.usermodel.Cell;
    import org.apache.poi.xssf.usermodel.XSSFRow;
    import org.apache.poi.xssf.usermodel.XSSFSheet;
    import org.apache.poi.xssf.usermodel.XSSFWorkbook;
    import org.junit.*;
    import org.springframework.beans.factory.annotation.Autowired;

    import java.io.File;
    import java.io.FileInputStream;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;

    import static com.sun.tools.doclint.Entity.ni;
    import static com.sun.tools.doclint.Entity.nu;

    /**
     * Created by zhushuai on 2019/5/6.
     */
    public class PersonCateTest extends ConfigTest {

        @Autowired
        private TbPersonMapper tbPersonMapper;

        @org.junit.Test
        public void importExcel() {
            try {
                Map<String, Object> map;
                XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File("E:\\朱帅\\耀邦\\人员\\人员分类.xlsx")));
                XSSFSheet sheet = workbook.getSheet("Sheet1");
                int rows = sheet.getLastRowNum();
                XSSFRow row;
                List<String> list = new ArrayList<>();
                for (int i = 1; i <= rows; i++) {
                    map = new HashedMap(2);
                    row = sheet.getRow(i);
                    Cell cell = row.getCell(0);
                    Integer cou = tbPersonMapper.queryPersonCateName(cell.getStringCellValue());
                    map.put("parentId", cou);
                    Cell twoCell = row.getCell(1);
                    cou = tbPersonMapper.queryPersonCateName(twoCell.getStringCellValue());
                    if (null != cou && cou > 0) {
                        continue;
                    }
                    map.put("level", 2);
                    map.put("cateName", twoCell.getStringCellValue());
                    tbPersonMapper.insertPersonCate(map);
                    System.out.println(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @org.junit.Test
        public void importThreeExcel() {
            try {
                Map<String, Object> map;
                XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File("E:\\朱帅\\耀邦\\人员\\人员分类.xlsx")));
                XSSFSheet sheet = workbook.getSheet("Sheet1");
                int rows = sheet.getLastRowNum();
                XSSFRow row;
                for (int i = 1; i <= rows; i++) {
                    map = new HashedMap(2);
                    row = sheet.getRow(i);
                    Cell cell = row.getCell(1);
                    Integer cou = tbPersonMapper.queryPersonCateName(cell.getStringCellValue());
                    map.put("parentId", cou);
                    Cell twoCell = row.getCell(2);
                    if (null != twoCell && StringUtils.isNotEmpty(twoCell.getStringCellValue())) {
                        cou = tbPersonMapper.queryPersonCateName(twoCell.getStringCellValue());
                        if (null != cou && cou > 0) {
                            continue;
                        }
                        map.put("level", 3);
                        map.put("cateName", twoCell.getStringCellValue());
                        tbPersonMapper.insertPersonCate(map);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @org.junit.Test
        public void importFourExcel() {
            try {
                Map<String, Object> map;
                XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File("E:\\朱帅\\耀邦\\人员\\人员分类.xlsx")));
                XSSFSheet sheet = workbook.getSheet("Sheet1");
                int rows = sheet.getLastRowNum();
                XSSFRow row;
                for (int i = 1; i <= rows; i++) {
                    map = new HashedMap(2);
                    row = sheet.getRow(i);
                    Cell cell = row.getCell(2);
                    if (cell == null){
                        continue;
                    }
                    Integer cou = tbPersonMapper.queryPersonCateName(cell.getStringCellValue());
                    map.put("parentId", cou);
                    Cell twoCell = row.getCell(3);
                    if (null != twoCell && StringUtils.isNotEmpty(twoCell.getStringCellValue())) {
                        String[] four = twoCell.getStringCellValue().split("，");
                        if (four.length <= 0) {
                            continue;
                        }
                        if (four.length > 1) {
                            for (int j = 0; j < four.length; j++) {
                                map.put("level",4);
                                map.put("cateName",four[j]);
                                tbPersonMapper.insertPersonCate(map);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
