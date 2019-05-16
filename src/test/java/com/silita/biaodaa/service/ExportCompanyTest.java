package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbCompanyInfoMapper;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.model.TbCompanyInfo;
import com.silita.biaodaa.service.impl.ReportServiceImpl;
import com.silita.biaodaa.utils.CommonUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhushuai on 2019/5/13.
 */
public class ExportCompanyTest extends ConfigTest{

    @Autowired
    TbCompanyMapper tbCompanyMapper;
    @Autowired
    TbCompanyService tbCompanyService;
    @Autowired
    TbCompanyInfoMapper tbCompanyInfoMapper;

    @org.junit.Test
    public void test(){
        List<Map<String, Object>> comUrlList = tbCompanyMapper.queryCompanyChangsha();
        List<Map<String,Object>> comList = new ArrayList<>();
        for (Map<String,Object> map : comUrlList){
            String comName = MapUtils.getString(map,"com_name");
            String regis_address = MapUtils.getString(map,"regis_address");
            String tabCode = CommonUtil.getCode(regis_address);
            TbCompanyInfo companyInfo = tbCompanyInfoMapper.queryDetailByComName(comName, tabCode);
            if (null != companyInfo && null != companyInfo.getPhone()) {
                String phone = tbCompanyService.solPhone(companyInfo.getPhone().trim(), null);
                map.put("phone",phone);
            }
            if (null != companyInfo && null != companyInfo.getRegisDate()){
                map.put("date",companyInfo.getRegisDate());
            }
            if (null != companyInfo && null != companyInfo.getEmail()){
                map.put("email",companyInfo.getEmail());
            }
            if (null != companyInfo && null != companyInfo.getStatus()){
                map.put("status",companyInfo.getStatus());
            }
            if (null != companyInfo && null != companyInfo.getProvince()){
                map.put("province",companyInfo.getProvince());
            }
            if (null != companyInfo && null != companyInfo.getCity()){
                map.put("city",companyInfo.getCity());
            }
            comList.add(map);
        }

        XSSFWorkbook wb = new XSSFWorkbook ();
        Sheet sheet = wb.createSheet("公司");  // 创建第一个Sheet页;
        Row row = sheet.createRow(0); // 创建一个行
        row.setHeightInPoints(30); //设置这一行的高度
        createCell(wb, row, (short) 0, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, "企业名称"); //要充满屏幕又要中间
        createCell(wb, row, (short) 1, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, "法定代表人"); //要充满屏幕又要中间
        createCell(wb, row, (short) 2, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, "电话"); //要充满屏幕又要中间
        createCell(wb, row, (short) 3, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, "邮箱"); //要充满屏幕又要中间
        createCell(wb, row, (short) 4, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, "注册时间"); //要充满屏幕又要中间
        createCell(wb, row, (short) 5, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, "经营状态"); //要充满屏幕又要中间
        createCell(wb, row, (short) 6, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, "所属省份"); //要充满屏幕又要中间
        createCell(wb, row, (short) 7, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, "所属市区"); //要充满屏幕又要中间
        for (int i = 0; i < comList.size(); i++) {
            row = sheet.createRow(i + 1); // 创建一个行
            row.setHeightInPoints(30); //设置这一行的高度
            createCell(wb, row, 0, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, comList.get(i).get("com_name").toString()); //要充满屏幕又要中间
            createCell(wb, row, 1, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, comList.get(i).get("legal_person").toString()); //要充满屏幕又要中间
            if (null != comList.get(i).get("phone")){
                createCell(wb, row, 2, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, comList.get(i).get("phone").toString()); //要充满屏幕又要中间
            }
            if (null != comList.get(i).get("email")){
                createCell(wb, row, 3, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, comList.get(i).get("email").toString()); //要充满屏幕又要中间
            }
            if (null != comList.get(i).get("date")){
                createCell(wb, row, 4, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, comList.get(i).get("date").toString()); //要充满屏幕又要中间
            }
            if (null != comList.get(i).get("status")){
                createCell(wb, row, 5, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, comList.get(i).get("status").toString()); //要充满屏幕又要中间
            }
            if (null != comList.get(i).get("province")){
                createCell(wb, row, 6, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, comList.get(i).get("province").toString()); //要充满屏幕又要中间
            }
            if (null != comList.get(i).get("city")){
                createCell(wb, row, 7, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, comList.get(i).get("city").toString()); //要充满屏幕又要中间
            }
        }
        try {
            FileOutputStream fileOut = new FileOutputStream("e:\\陕西.xls");
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createCell(XSSFWorkbook wb, Row row, int column, short halign, short valign, String val) {
        Cell cell = row.createCell(column);  // 创建单元格
        cell.setCellValue(new XSSFRichTextString(val));  // 设置值
    }

}
