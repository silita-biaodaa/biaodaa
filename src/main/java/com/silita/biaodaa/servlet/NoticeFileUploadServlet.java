package com.silita.biaodaa.servlet;

import com.crm.domain.service.ReportManage;
import com.crm.system.BeanFactoryAnywhere;
import com.upload.servlet.multipart.FilePart;
import com.upload.servlet.multipart.MultipartParser;
import com.upload.servlet.multipart.Part;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;


/**
 * Created by on 2018/5/21.
 */

public class NoticeFileUploadServlet extends HttpServlet {
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private static final long serialVersionUID = -7255130000222626557L;
    private static final String DOTYPE = "datetimeflag";
    private static final String ACTIVE = "handle";
    private String dirName;
    private int maxSize;
    private String active;
    private BeanFactory ctx = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        dirName = config.getInitParameter("uploadPath");
        maxSize = Integer.parseInt(config.getInitParameter("maxSize"));
        if (dirName == null) {
            throw new ServletException("Please supply uploadDir parameter");
        }
        File dir = new File(dirName);
        if (!dir.isDirectory()) {
            dir.mkdir();
        }
        ctx = BeanFactoryAnywhere.beanFactory;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String datetimeflag = request.getHeader(DOTYPE);
        active = request.getHeader(ACTIVE);
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        ServletOutputStream out = response.getOutputStream();

        try {
            MultipartParser mp = new MultipartParser(request, maxSize * 1024 * 1024);
            Part part;
            File file = null;
            while ((part = mp.readNextPart()) != null) {
                String fileName = part.getName();
                if (part.isFile()) {

                    FilePart filePart = (FilePart) part;
                    if (filePart.getFileName() != null) {
                        fileName = new String(filePart.getFileName().getBytes(
                                "iso8859-1"), "utf8");

                        if ("excel_template".equals(active)) {
                            String realPath = this.getServletConfig()
                                    .getServletContext().getRealPath("/");// 获取真实应用路径
                            ReportManage handle = (ReportManage) ctx
                                    .getBean("reportService");
                            if (handle != null) {
                                String templateDir = handle.getTemplateDir();
                                file = new File((realPath + templateDir).replace("\\", "/"), fileName);
                                filePart.writeTo(file);

                            } else {
                                throw new RuntimeException("sys:system config error.");
                            }
                            //out.print(datetimeflag+fileName);
                        } else if ("img_upload".equals(active)) {
                            dirName = dirName.replace("\\", "/");
                            file = new File(dirName, fileName);

                            filePart.writeTo(file);
                            //out.print(datetimeflag+fileName);
                        } else if ("vi_upload".equals(active)) {
                            dirName = dirName.replace("\\", "/");
                            file = new File(dirName, fileName);

                            filePart.writeTo(file);
                            //out.print(fileName);
                        } else {
                            //dirName=dirName.replace("\\", "/");
                            file = new File(dirName, datetimeflag + fileName);
                            long size = filePart.writeTo(file);
                            logger.info("upload file name:" + fileName
                                    + " file size:" + size);
                            out.print(datetimeflag);
                        }
                    }
                }
            }
            out.flush();
            out.close();
        } catch (IOException lEx) {
            logger.error(" saving file", lEx);
        } catch (Exception e) {
            logger.error("Sys:error saving file", e);
            e.printStackTrace();
        }
    }
}
