package com.silita.biaodaa.service;

import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.RedisConstantInterface;
import com.silita.biaodaa.dao.TbCompanyInfoMapper;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.dao.TbCompanyQualificationMapper;
import com.silita.biaodaa.es.ElasticseachService;
import com.silita.biaodaa.model.TbCompanyInfo;
import com.silita.biaodaa.model.es.CompanyEs;
import com.silita.biaodaa.utils.CommonUtil;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportCompanyEmailTest extends ConfigTest {

    @Autowired
    TbCompanyMapper tbCompanyMapper;
    @Autowired
    ElasticseachService elasticseachService;
    @Autowired
    TbCompanyQualificationMapper tbCompanyQualificationMapper;
    @Autowired
    TbCompanyInfoMapper tbCompanyInfoMapper;
    @Autowired
    MyRedisTemplate myRedisTemplate;

    @Test
    public void test() {
//        Integer count = tbCompanyMapper.queryCompanyCount();
//        Integer pages = elasticseachService.getPage(count, 3000);
//        List<CompanyEs> comList = new ArrayList<>();
//        Map<String, Object> param = new HashMap<>();
//        List<Map<String, Object>> comUrlList = new ArrayList<>();
//        Map<String,Object> companyMap = null;
//        param.put("pageSize", 3000);
//        Integer qualCount=0;
//        for (int i = 1; i <= pages; i++) {
//            param.put("page", (i - 1) * 3000);
//            comList = tbCompanyMapper.queryCompanyEsList(param);
//            for (CompanyEs companyEs : comList) {
//                qualCount= tbCompanyQualificationMapper.queryCompanyQualCount(companyEs.getComId());
//                if(qualCount > 0){
//                    String tabCode = CommonUtil.getCode(companyEs.getRegisAddress());
//                    TbCompanyInfo companyInfo = tbCompanyInfoMapper.queryDetailByComName(companyEs.getComName(), tabCode);
//                    if(null != companyInfo && MyStringUtils.isNotNull(companyInfo.getEmail())){
//                        companyMap = new HashMap<>();
//                        companyMap.put("comName",companyEs.getComName());
//                        companyMap.put("email",companyInfo.getEmail());
//                        comUrlList.add(companyMap);
//                    }
//                }
//            }
//        }

//        myRedisTemplate.setObject("compangList",comUrlList,RedisConstantInterface.LIST_OVER_TIME);

        List<Map<String, Object>> comUrlList = myRedisTemplate.getList("compangList");

        XSSFWorkbook wb = new XSSFWorkbook ();
        Sheet sheet = wb.createSheet("公司邮箱");  // 创建第一个Sheet页;
        Row row = sheet.createRow(0); // 创建一个行
        row.setHeightInPoints(30); //设置这一行的高度
        createCell(wb, row, (short) 0, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, "企业名称"); //要充满屏幕又要中间
        createCell(wb, row, (short) 1, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, "邮箱"); //要充满屏幕又要中间
        for (int i = 0; i < comUrlList.size(); i++) {
            row = sheet.createRow(i + 1); // 创建一个行
            row.setHeightInPoints(30); //设置这一行的高度
            createCell(wb, row, 0, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, comUrlList.get(i).get("comName").toString()); //要充满屏幕又要中间
            createCell(wb, row, 1, CellStyle.ALIGN_FILL, CellStyle.VERTICAL_CENTER, comUrlList.get(i).get("email").toString()); //要充满屏幕又要中间
        }
        try {
            FileOutputStream fileOut = new FileOutputStream("e:\\公司.xls");
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createCell(XSSFWorkbook wb, Row row, int column, short halign, short valign, String val) {
        Cell cell = row.createCell(column);  // 创建单元格
        cell.setCellValue(new XSSFRichTextString(val));  // 设置值
//        HSSFCellStyle cellStyle = wb.createCellStyle(); // 创建单元格样式
//        cellStyle.setAlignment(halign);  // 设置单元格水平方向对其方式
//        cellStyle.setVerticalAlignment(valign); // 设置单元格垂直方向对其方式
//        cell.setCellStyle(cellStyle); // 设置单元格样式
    }
}
