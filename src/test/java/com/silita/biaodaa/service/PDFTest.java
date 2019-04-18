package com.silita.biaodaa.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.FileOutputStream;

/**
 * Created by zhushuai on 2019/4/18.
 */
public class PDFTest {

    private static void create() throws Exception {
        // 创建一个文档（默认大小A4，边距36, 36, 36, 36）
        Document document = new Document(PageSize.A4);
        // 设置边距，单位都是像素，换算大约1厘米=28.33像素
        document.setMargins(50, 50, 50, 50);

        // 创建writer，通过writer将文档写入磁盘
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("E:/demo1.pdf"));

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
        Paragraph des1 = new Paragraph("本报告生成时间为2019年4月18日", font);
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
        Font font5 = ffi.getFont("宋体", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 14, Font.NORMAL, null);
        Paragraph des5 = new Paragraph("1、企业所在地：湖南省：", font5);
        des5.setLeading(40);
        document.add(des5);
        Paragraph des6 = new Paragraph("2、资质要求：建筑工程施工总承包一级和市政公用工程施工总承包一级：", font5);
        des6.setLeading(40);
        document.add(des6);
        des6 = new Paragraph("3、业绩要求：", font5);
        des6.setLeading(40);
        document.add(des6);
        Paragraph des7 = new Paragraph("①平台：全国建筑市场监管公共服务平台；", font5);
        des7.setLeading(40);
        document.add(des7);
        Paragraph des8 = new Paragraph("②项目名称：土石方；", font5);
        des8.setLeading(40);
        document.add(des8);
        Paragraph des9 = new Paragraph("③竣工时间2016-4-10至2019-4-10；", font5);
        des9.setLeading(40);
        document.add(des9);
        Paragraph des10 = new Paragraph("④金额>=450万。", font5);
        des10.setLeading(40);
        document.add(des10);
        //企业
        Paragraph des11 = new Paragraph("根据已选择的筛选条件查询出符合要求的企业共6家：", font4);
        des11.setLeading(40);
        document.add(des11);
        Font font12 = ffi.getFont("微软雅黑", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 14, Font.NORMAL, null);
        Paragraph des12 = new Paragraph("1.湖南省第五工程有限公司", font12);
        des12.setLeading(40);
        document.add(des12);
        Paragraph des13 = new Paragraph("※ 符合资质要求", font2);
        des13.setLeading(40);
        document.add(des13);
        Paragraph des14 = new Paragraph("建筑工程施工总承包一级", font3);
        des14.setLeading(20);
        document.add(des14);
        Paragraph des15 = new Paragraph("建筑工程施工总承包一级", font3);
        des15.setLeading(20);
        document.add(des15);
        Paragraph des16 = new Paragraph("※ 符合要求业绩（共2个）", font2);
        des16.setLeading(30);
        document.add(des16);
        document.add(new Paragraph(" "));
        //创建表格
        // 创建表格，5列的表格
        PdfPTable table = new PdfPTable(5);
        table.setTotalWidth(PageSize.A4.getWidth() - 100);
        table.setTotalWidth(new float[]{40, 250, 60, 60, 80});
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
        paragrah = new Paragraph("类别", font);
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
        // 添加内容
        table.addCell(new Paragraph("1", font));
        PdfPCell cell = new PdfPCell(new Paragraph("泸溪县武溪工业园北区土石方（标准厂房三期用地）项目", font));
        table.addCell(cell);
        table.addCell(new Paragraph("施工总包", font));
        table.addCell(new Paragraph("1614.26", font));
        table.addCell(new Paragraph("2017-9-16", font));
        document.add(table);
        // 关闭文档，才能输出
        document.close();
        writer.close();
    }

    public static void main(String[] args) throws Exception {
        create();
    }

}
