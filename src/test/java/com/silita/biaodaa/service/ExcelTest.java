package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.SnatchurlMapper;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.hssf.usermodel.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelTest extends ConfigTest{

    @Autowired
    SnatchurlMapper snatchurlMapper;

    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFSheet sheet = null;

    @Test
    public void test() {
        sheet = wb.createSheet("中标候选人统计");  // 创建第一个Sheet页
        HSSFRow row = sheet.createRow(0); // 创建一个行
        row.setHeightInPoints(30); //设置这一行的高度
        createCell(wb, row, (short) 0, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "企业名称"); //要充满屏幕又要中间
        createCell(wb, row, (short) 1, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "中标次数"); //要充满屏幕又要中间
        createCell(wb, row, (short) 2, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "注册地址"); //要充满屏幕又要中间
        createCell(wb, row, (short) 3, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "法人"); //要充满屏幕又要中间
        createCell(wb, row, (short) 4, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "安许证号"); //要充满屏幕又要中间
        createCell(wb, row, (short) 5, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "资质范围"); //要充满屏幕又要中间
        createCell(wb, row, (short) 6, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "红黑榜"); //要充满屏幕又要中间
//        List<Map<String, Object>> list = snatchurlMapper.queryZhongList();
        List<Map<String, Object>> list = new ArrayList<>();
        String regisAddress = null;
        String legalPerson = null;
        String ranges = null;
        String certNo = null;
        Map<String, Object> resultMap = null;
        for (int i = 0; i < list.size(); i++) {
            regisAddress = null;
            legalPerson = null;
            ranges = null;
            certNo = null;
            row = sheet.createRow(i + 1); // 创建一个行
            row.setHeightInPoints(30); //设置这一行的高度
//            resultMap = snatchurlMapper.queryZhongDetail(list.get(i).get("comName").toString());
            if (null != resultMap) {
                regisAddress = MapUtils.getString(resultMap, "regisAddress");
                legalPerson = MapUtils.getString(resultMap, "legalPerson");
                ranges = MapUtils.getString(resultMap, "ranges");
                certNo = MapUtils.getString(resultMap, "certNo");
            }
            String score = MapUtils.getString(list.get(i), "score");
            createCell(wb, row, 0, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("comName").toString()); //要充满屏幕又要中间
            createCell(wb, row, 1, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("bidCount").toString()); //要充满屏幕又要中间
            createCell(wb, row, 2, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, regisAddress); //要充满屏幕又要中间
            createCell(wb, row, 3, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, legalPerson); //要充满屏幕又要中间
            createCell(wb, row, (short) 4, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, certNo); //要充满屏幕又要中间
            createCell(wb, row, (short) 5, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, ranges); //要充满屏幕又要中间
            createCell(wb, row, (short) 6, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, score); //要充满屏幕又要中间
        }
        try {
            FileOutputStream fileOut = new FileOutputStream("e:\\中标.xls");
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createCell(HSSFWorkbook wb, HSSFRow row, int column, short halign, short valign, String val) {
        HSSFCell cell = row.createCell(column);  // 创建单元格
        cell.setCellValue(new HSSFRichTextString(val));  // 设置值
//        HSSFCellStyle cellStyle = wb.createCellStyle(); // 创建单元格样式
//        cellStyle.setAlignment(halign);  // 设置单元格水平方向对其方式
//        cellStyle.setVerticalAlignment(valign); // 设置单元格垂直方向对其方式
//        cell.setCellStyle(cellStyle); // 设置单元格样式
    }
}
