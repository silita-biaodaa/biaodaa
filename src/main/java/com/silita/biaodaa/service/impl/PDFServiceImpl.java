package com.silita.biaodaa.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPHeaderCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.dao.TbReportInfoMapper;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbReportInfo;
import com.silita.biaodaa.service.PDFService;
import com.silita.biaodaa.service.ProjectService;
import com.silita.biaodaa.service.ReportService;
import com.silita.biaodaa.service.TbCompanyService;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhushuai on 2019/4/18.
 */
@Service("pDFService")
public class PDFServiceImpl implements PDFService {

    @Autowired
    TbReportInfoMapper tbReportInfoMapper;
    @Autowired
    MyRedisTemplate myRedisTemplate;
    @Autowired
    TbCompanyService tbCompanyService;
    @Autowired
    ReportService reportService;
    @Autowired
    ProjectService projectService;

    @Override
    public boolean buildPdf(String order) {
        try {
            TbReportInfo reportInfo = tbReportInfoMapper.queryReportDetailOrderNo(order);
            if (null == reportInfo) {
                return false;
            }
            String condition = reportInfo.getRepCondition();
            Map<String, Object> conditionMap = JSONObject.parseObject(condition);
            reportService.setQualName(conditionMap);
            java.util.List<TbCompany> list = tbCompanyService.filterCompanyList(conditionMap);
            if (null != list && list.size() > 0) {
                List<Map<String, Object>> compayList = new ArrayList<>();
                Map<String, Object> companyMap;
                for (TbCompany com : list) {
                    companyMap = new HashedMap();
                    companyMap.put("comName", com.getComName());
                    if (MyStringUtils.isNotNull(com.getRange())) {
                        String code = "";
                        String[] quaCodes = com.getRange().split(",");
                        String[] qual;
                        if (quaCodes != null && quaCodes.length > 0) {
                            for (String str : quaCodes) {
                                qual = str.split("/");
                                if (null != qual && qual.length == 3) {
                                    code = code + qual[1] + "/" + qual[2] + ",";
                                } else {
                                    code = code + qual[1] + ",";
                                }
                            }
                        }
                        companyMap.put("qualCode", StringUtils.strip(code, ","));
                        reportService.setQualName(companyMap);
                    }
                    conditionMap.put("comName", com.getComName());
                    List<Map<String, Object>> proList = projectService.listCompanyProject(conditionMap);
                    if (null != proList && proList.size() > 0) {
                        companyMap.put("proList", proList);
                        compayList.add(companyMap);
                    }
                }
                if (null != compayList && compayList.size() > 0) {
                    if (compayList.size() > 100) {
                        compayList = compayList.subList(0, 100);
                    }
                    //生成pdf
                    conditionMap.put("comList", compayList);
                    this.create(conditionMap, order);
                    return true;
                }
                // TODO: 2019/4/19 退款
                return false;
            }
            // TODO: 2019/4/18  退款

        } catch (Exception e) {
            e.printStackTrace();
//            myRedisTemplate.lpush("order_list", order);
        }
        return false;
    }

    private void create(Map<String, Object> param, String orderNo) throws Exception {
        // 创建一个文档（默认大小A4，边距36, 36, 36, 36）
        Document document = new Document(PageSize.A4);
        // 设置边距，单位都是像素，换算大约1厘米=28.33像素
        document.setMargins(50, 50, 50, 50);

        // 创建writer，通过writer将文档写入磁盘
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("E://" + orderNo + ".pdf"));

        // 定义字体
        FontFactoryImp ffi = new FontFactoryImp();
        // 注册全部默认字体目录，windows会自动找fonts文件夹的，返回值为注册到了多少字体
        ffi.registerDirectories();
        // 获取字体，其实不用这么麻烦，后面有简单方法
        Font font = ffi.getFont("宋体", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.NORMAL, null);
        Font font2 = ffi.getFont("宋体", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 14, Font.NORMAL, null);
        // 打开文档，只有打开后才能往里面加东西
        document.open();
        Paragraph p1 = new Paragraph(" ");
        p1.setLeading(50);
        document.add(p1);
        //设置logo
        Image image = Image.getInstance("E:\\logo.png");
        image.setAlignment(Element.ALIGN_CENTER);
        image.scaleAbsolute(200, 80);
        //Add to document
        document.add(image);
        p1 = new Paragraph(" ");
        p1.setLeading(50);
        document.add(p1);
        //设置title
        Font titleFont = ffi.getFont("宋体", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 20, Font.BOLD, null);
        Paragraph title = new Paragraph("企业综合查询报告", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setLeading(80);
        document.add(title);
        //设置二维码
        Image image1 = Image.getInstance("E:\\pic-erweima.png");
        image1.setAlignment(Element.ALIGN_CENTER);
        image1.scaleAbsolute(200, 200);
        document.add(image1);
        p1 = new Paragraph(" ");
        p1.setLeading(60);
        document.add(p1);
        //设置描述
        Paragraph des = new Paragraph("--最懂建筑业的互联网产品--", titleFont);
        des.setAlignment(Element.ALIGN_CENTER);
        document.add(des);
        //设置报告生成时间
        Paragraph des1 = new Paragraph("本报告生成时间为" + MyDateUtils.getDate("yyyy年MM月dd日"), font);
        des1.setAlignment(Element.ALIGN_CENTER);
        des1.setLeading(55);
        document.add(des1);
        //设置解释
        Paragraph des2 = new Paragraph("您所看到的报告内容为截止至该时间点的标大大数据", font);
        des2.setAlignment(Element.ALIGN_CENTER);
        des2.setLeading(55);
        document.add(des2);

        //正文
        LineSeparator line = new LineSeparator(1f, 100, BaseColor.BLACK, Element.ALIGN_CENTER, -3f);
        Font font3 = ffi.getFont("宋体", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 12, Font.NORMAL, null);
        Paragraph des3 = new Paragraph("企业综合报告查询", font3);
        des3.setLeading(50);
        document.add(des3);
        document.add(line);
        //筛选条件
        Font font4 = ffi.getFont("宋体", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 20, Font.BOLD, null);
        Paragraph des4 = new Paragraph("已选择的筛选条件：", font4);
        des4.setLeading(40);
        document.add(des4);
        int count = 1;
        Font font5 = ffi.getFont("宋体", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 14, Font.NORMAL, null);
        Paragraph des5 = new Paragraph(count + "、企业所在地：" + param.get("regisAddress") + "；", font5);
        des5.setLeading(40);
        document.add(des5);
        String qualName = MapUtils.getString(param, "qualName");
        if (MyStringUtils.isNotNull(qualName)) {
            String rangeType = MapUtils.getString(param, "rangeType") == "and" ? "和" : "或";
            qualName = qualName.replaceAll(",", rangeType);
            count++;
            Paragraph des6 = new Paragraph(count + "、资质要求：" + qualName + "；", font5);
            des6.setLeading(40);
            document.add(des6);
        }
        count++;
        Paragraph des6 = new Paragraph(count + "、业绩要求：", font5);
        des6.setLeading(40);
        document.add(des6);
        Paragraph des7 = new Paragraph("①平台：" + setPlatform(param) + "；", font5);
        des7.setLeading(40);
        document.add(des7);
        Paragraph des8 = new Paragraph("②项目名称：" + param.get("projName") + "；", font5);
        des8.setLeading(40);
        document.add(des8);
        if (null != param.get("buildStart") && null != param.get("buildEnd")) {
            Paragraph des9 = new Paragraph("③竣工时间：" + param.get("buildStart") + "至" + param.get("buildEnd") + "；", font5);
            des9.setLeading(40);
            document.add(des9);
        } else if (null != param.get("buildStart")) {
            Paragraph des9 = new Paragraph("③竣工时间" + param.get("buildStart"), font5);
            des9.setLeading(40);
            document.add(des9);
        } else if (null != param.get("buildEnd")) {
            Paragraph des9 = new Paragraph("③竣工时间" + param.get("buildEnd"), font5);
            des9.setLeading(40);
            document.add(des9);
        }
        if (null != param.get("amountStart") && null != param.get("amountEnd")) {
            Paragraph des10 = new Paragraph("④金额" + param.get("amountStart") + "万至" + param.get("amountEnd") + "万", font5);
            des10.setLeading(40);
            document.add(des10);
        } else if (null != param.get("amountStart")) {
            Paragraph des10 = new Paragraph("④金额>=" + param.get("amountStart") + "万", font5);
            des10.setLeading(40);
            document.add(des10);
        } else if (null != param.get("amountEnd")) {
            Paragraph des10 = new Paragraph("④金额<=" + param.get("amountEnd") + "万", font5);
            des10.setLeading(40);
            document.add(des10);
        }
        //企业
        List<Map<String, Object>> comList = (List<Map<String, Object>>) param.get("comList");
        Font fontF = ffi.getFont("宋体", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 18, Font.BOLD, null);
        Paragraph des11 = new Paragraph("根据已选择的筛选条件查询出符合要求的企业共" + comList.size() + "家：", fontF);
        des11.setLeading(40);
        document.add(des11);
        for (int i = 0; i < comList.size(); i++) {
            Font font12 = ffi.getFont("微软雅黑", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 14, Font.NORMAL, null);
            Paragraph des12 = new Paragraph((i + 1) + "." + comList.get(i).get("comName"), font12);
            des12.setLeading(40);
            document.add(des12);
            String comQualName = MapUtils.getString(comList.get(i), "qualName");
            if (MyStringUtils.isNotNull(comQualName)) {
                Paragraph des13 = new Paragraph("※ 符合资质要求", font2);
                des13.setLeading(40);
                document.add(des13);
                String[] comQualNames = comQualName.split(",");
                for (String str : comQualNames) {
                    Paragraph des14 = new Paragraph(str, font3);
                    des14.setLeading(20);
                    document.add(des14);
                }
            }
            List<Map<String, Object>> proList = (List<Map<String, Object>>) comList.get(i).get("proList");
            Paragraph des16 = new Paragraph("※ 符合要求业绩（共" + proList.size() + "个）", font2);
            des16.setLeading(30);
            document.add(des16);
            document.add(new Paragraph(" "));
            //创建表格
            // 创建表格，5列的表格
            PdfPTable table = new PdfPTable(5);
            table.setTotalWidth(PageSize.A4.getWidth() - 100);
            table.setTotalWidth(new float[]{40, 250, 60, 80, 60});
            table.setLockedWidth(true);
            table.setKeepTogether(true);
            // 创建头
            PdfPHeaderCell header = new PdfPHeaderCell();
            Paragraph paragrah = new Paragraph("序号", font);
            paragrah.setAlignment(Element.ALIGN_CENTER);
            header.addElement(paragrah);
            table.addCell(header);
            header = new PdfPHeaderCell();
            paragrah = new Paragraph("项目名称", font);
            paragrah.setAlignment(Element.ALIGN_CENTER);
            header.addElement(paragrah);
            table.addCell(header);
            header = new PdfPHeaderCell();
            if ("project".equals(param.get("projSource"))) {
                paragrah = new Paragraph("类别", font);
            } else if ("shuili".equals(param.get("projSource"))) {
                paragrah = new Paragraph("结算金额", font);
            } else if ("jiaotong".equals(param.get("projSource"))) {
                paragrah = new Paragraph("标段名称", font);
            }
            paragrah.setAlignment(Element.ALIGN_CENTER);
            header.addElement(paragrah);
            table.addCell(header);
            header = new PdfPHeaderCell();
            paragrah = new Paragraph("金额（万元）", font);
            paragrah.setAlignment(Element.ALIGN_CENTER);
            header.addElement(paragrah);
            table.addCell(header);
            header = new PdfPHeaderCell();
            paragrah = new Paragraph("竣工时间", font);
            paragrah.setAlignment(Element.ALIGN_CENTER);
            header.addElement(paragrah);
            table.addCell(header);
            document.add(table);
            // 添加内容
            PdfPTable table1 = new PdfPTable(5);
            table1.setTotalWidth(PageSize.A4.getWidth() - 100);
            table1.setTotalWidth(new float[]{40, 250, 60, 80, 60});
            table1.setLockedWidth(true);
            table1.setKeepTogether(true);
            for (int j = 0; j < proList.size(); j++) {
                int number = j + 1;
                System.out.println(number);
                table1.addCell(new Paragraph(String.valueOf(number), font));
                table1.addCell(new Paragraph(proList.get(j).get("proName").toString(), font));
                table1.addCell(new Paragraph(proList.get(j).get("proType").toString(), font));
                if (null != proList.get(j).get("amount")) {
                    table1.addCell(new Paragraph(proList.get(j).get("amount").toString(), font));
                } else {
                    table1.addCell(new Paragraph(" ", font));
                }
                if (null != proList.get(j).get("buildEnd")) {
                    table1.addCell(new Paragraph(proList.get(j).get("buildEnd").toString(), font));
                } else {
                    table1.addCell(new Paragraph(" ", font));
                }
            }
            document.add(table1);
        }
        //结尾
        Paragraph des19 = new Paragraph("*该报告由标大大提供。电话：0731-85076077；邮箱：hnsilita@163.com", font);
        des19.setLeading(40);
        document.add(des19);
        // 关闭文档，才能输出
        document.close();
        writer.close();
    }

    private String setPlatform(Map<String, Object> param) {
        if ("project".equals(param.get("projSource"))) {
            return "全国建筑市场监管公共服务平台";
        } else if ("shuili".equals(param.get("projSource"))) {
            return "全国水利建设市场信用信息平台";
        } else {
            return "全国公路建设市场信用信息管理系统";
        }
    }
}
