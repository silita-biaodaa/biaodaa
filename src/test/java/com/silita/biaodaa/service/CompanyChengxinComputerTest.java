package com.silita.biaodaa.service;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.model.es.CompanyEs;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.poi.hssf.usermodel.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhushuai on 2019/6/14.
 */
@ContextConfiguration(locations = {"classpath:config/spring/applicationContext.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CompanyChengxinComputerTest {

    @Autowired
    ReputationComputerService reputationComputerService;
    @Autowired
    TbCompanyMapper tbCompanyMapper;

    @org.junit.Test
    public void test() {
        Map<String, Object> param = new HashedMap() {{
            put("projType", "建筑工程");
        }};
        //查询公司
        Map<String, Object> val = new HashedMap() {{
            put("regisAddress", "湖南省");
            put("pageSize", 1000);
        }};
        int total = tbCompanyMapper.queryCompanyAddressCount(val);
        if (total <= 0) {
            return;
        }
        Integer pages = getPage(total, 1000);
        List<Map<String, Object>> resList = new ArrayList<>();
        Map<String, Object> resMap = null;
        for (int i = 1; i <= pages; i++) {
            val.put("page", (i - 1) * 1000);
            List<CompanyEs> list = tbCompanyMapper.queryCompanyEsList(val);
            for (CompanyEs es : list) {
                resMap = new HashedMap();
                resMap.put("comName", es.getComName());
                param.put("comId", es.getComId());
                Map map = reputationComputerService.computer(param);
                if (MapUtils.isEmpty(map)) {
                    continue;
                }
                resMap.put("shijihege", 0);
                resMap.put("shijiyouxiu", 0);
                resMap.put("shengjihege", 0);
                resMap.put("shengjiyouxiu", 0);
                resMap.put("reviewCompany", 0);
                resMap.put("reviewProject", 0);
                List<Map<String, Object>> gjj = (List) map.get("gjhj");
                int lu = 0;
                int BUILD = 0;
                int DECORATE = 0;
                int furong = 0;
                int youzhi = 0;
                for (Map<String, Object> gj : gjj) {
                    if (Constant.PRIZE_LUBAN.equals(gj.get("awardName"))) {
                        lu++;
                    } else if (Constant.PRIZE_BUILD.equals(gj.get("awardName"))) {
                        BUILD++;
                    } else if (Constant.PRIZE_DECORATE.equals(gj.get("awardName"))) {
                        DECORATE++;
                    }
                }
                resMap.put("lu", lu);
                resMap.put("BUILD", BUILD);
                resMap.put("DECORATE", DECORATE);
                Map<String, Object> sjj = (Map<String, Object>) map.get("sjhj");
                if (null != sjj.get("reviewProject")) {
                    resMap.put("reviewProject", ((List) sjj.get("reviewProject")).size());
                }
                if (null != sjj.get("aqrz")) {
                    Map aqrz = (Map) sjj.get("aqrz");
                    String grade = aqrz.get("grade").toString();
                    if (grade.equals("市级合格")) {
                        resMap.put("shijihege", 1);
                    } else if (grade.equals("市级优秀")) {
                        resMap.put("shijiyouxiu", 1);
                    } else if (grade.equals("省级合格")) {
                        resMap.put("shengjihege", 1);
                    } else {
                        resMap.put("shengjiyouxiu", 1);
                    }
                }
                if (null != sjj.get("reviewCompany")) {
                    resMap.put("reviewCompany", ((List) sjj.get("reviewCompany")).size());
                }
                if (null != sjj.get("awards")) {
                    List<Map<String, Object>> awards = (List<Map<String, Object>>) sjj.get("awards");
                    for (Map<String, Object> as : awards) {
                        if (Constant.PRIZE_LOTUS.equals(as.get("awardName"))) {
                            furong++;
                        } else {
                            youzhi++;
                        }
                    }
                }
                resMap.put("furong", furong);
                resMap.put("youzhi", youzhi);
                resMap.put("score", map.get("score"));
                //不良
                Map under = reputationComputerService.listUndesirable(param);
                resMap.put("buhegexiangm", ((List) under.get("unProject")).size());
                resMap.put("buhegeqiye", ((List) under.get("unCompany")).size());
                resMap.put("under", ((List) under.get("undesirable")).size());
                resList.add(resMap);
            }
        }
        this.test222(resList);
    }

    private static Integer getPage(Integer total, Integer pageSize) {
        Integer pages = 0;
        if (total % pageSize == 0) {
            pages = total / pageSize;
        } else {
            pages = (total / pageSize) + 1;
        }
        return pages;
    }


    public void test222(List<Map<String, Object>> list) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("统计");  // 创建第一个Sheet页
        HSSFRow row = sheet.createRow(0); // 创建一个行
        row.setHeightInPoints(30); //设置这一行的高度
        createCell(wb, row, (short) 0, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "企业名称"); //要充满屏幕又要中间
        createCell(wb, row, (short) 1, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "鲁班奖"); //要充满屏幕又要中间
        createCell(wb, row, (short) 2, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "全国安全生产标准化工地"); //要充满屏幕又要中间
        createCell(wb, row, (short) 3, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "全国建筑工程装饰奖"); //要充满屏幕又要中间
        createCell(wb, row, (short) 4, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "芙蓉奖"); //要充满屏幕又要中间
        createCell(wb, row, (short) 5, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "省优质工程"); //要充满屏幕又要中间
        createCell(wb, row, (short) 6, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "年度安全考评优良工地"); //要充满屏幕又要中间
        createCell(wb, row, (short) 7, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "年度安全考评优良企业"); //要充满屏幕又要中间
        createCell(wb, row, (short) 8, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "安全认证省级优秀"); //要充满屏幕又要中间
        createCell(wb, row, (short) 9, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "安全认证省级合格"); //要充满屏幕又要中间
        createCell(wb, row, (short) 10, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "安全认证市级优秀"); //要充满屏幕又要中间
        createCell(wb, row, (short) 11, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "安全认证市级合格"); //要充满屏幕又要中间
        createCell(wb, row, (short) 12, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "季度安全考评不合格项目"); //要充满屏幕又要中间
        createCell(wb, row, (short) 13, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "年度安全考评不合格企业"); //要充满屏幕又要中间
        createCell(wb, row, (short) 14, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "不良记录"); //要充满屏幕又要中间
        createCell(wb, row, (short) 15, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, "合计总分"); //要充满屏幕又要中间
        for (int i = 0; i < list.size(); i++) {
            row = sheet.createRow(i + 1); // 创建一个行
            row.setHeightInPoints(30); //设置这一行的高度
            createCell(wb, row, (short) 0, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("comName").toString()); //要充满屏幕又要中间
            createCell(wb, row, (short) 1, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("lu").toString()); //要充满屏幕又要中间
            createCell(wb, row, (short) 2, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("BUILD").toString()); //要充满屏幕又要中间
            createCell(wb, row, (short) 3, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("DECORATE").toString()); //要充满屏幕又要中间
            createCell(wb, row, (short) 4, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("furong").toString()); //要充满屏幕又要中间
            createCell(wb, row, (short) 5, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("youzhi").toString()); //要充满屏幕又要中间
            createCell(wb, row, (short) 6, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("reviewProject").toString()); //要充满屏幕又要中间
            createCell(wb, row, (short) 7, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("reviewCompany").toString()); //要充满屏幕又要中间
            createCell(wb, row, (short) 8, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("shengjiyouxiu").toString()); //要充满屏幕又要中间
            createCell(wb, row, (short) 9, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("shengjihege").toString()); //要充满屏幕又要中间
            createCell(wb, row, (short) 10, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("shijiyouxiu").toString()); //要充满屏幕又要中间
            createCell(wb, row, (short) 11, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("shijihege").toString()); //要充满屏幕又要中间
            createCell(wb, row, (short) 12, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("buhegexiangm").toString()); //要充满屏幕又要中间
            createCell(wb, row, (short) 13, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("buhegeqiye").toString()); //要充满屏幕又要中间
            createCell(wb, row, (short) 14, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("under").toString()); //要充满屏幕又要中间
            createCell(wb, row, (short) 15, HSSFCellStyle.ALIGN_FILL, HSSFCellStyle.VERTICAL_CENTER, list.get(i).get("score").toString()); //要充满屏幕又要中间
        }
        try {
            FileOutputStream fileOut = new FileOutputStream("E:\\朱帅\\耀邦\\诚信\\非去重建筑工程诚信.xls");
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createCell(HSSFWorkbook wb, HSSFRow row, int column, short halign, short valign, String val) {
        HSSFCell cell = row.createCell(column);  // 创建单元格
        cell.setCellValue(new HSSFRichTextString(val));  // 设置值
    }
}
