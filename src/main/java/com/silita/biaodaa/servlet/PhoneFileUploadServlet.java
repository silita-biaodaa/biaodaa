package com.silita.biaodaa.servlet;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;


/**
 * 为什么springmvc自带的文件上传不能用
 */
@WebServlet("/userCenter/updateHeadPortrait")
public class PhoneFileUploadServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, final HttpServletResponse response) {
        String savePath = PropertiesUtils.getProperty("HEAD_PORTRAIT_PATH");
//        String tempPath = this.getServletContext().getRealPath("/temp");
        File tmpFile = new File(savePath);
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }

        String realSavePath = "";
        //得到文件的保存目录
        realSavePath = makePath(savePath);
        JSONObject json = new JSONObject();
        //将数据写入流中
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            PrintWriter print = response.getWriter();
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(1024 * 100);
            factory.setRepository(tmpFile);
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setHeaderEncoding("UTF-8");
            if (!ServletFileUpload.isMultipartContent(request)) {
                return;
            }
            upload.setFileSizeMax(1024 * 1024);
            List<FileItem> list = upload.parseRequest(request);
            for (FileItem item : list) {
                if (!item.isFormField()) {
                    System.out.println("file.size====="+item.getSize());
                    if(item.getSize() > (1024 * 1024)){
                        json.put("code",0);
                        json.put("msg","文件过大，请重新上传！！！");
                        print.print(json.toJSONString());
                        return;
                    }
                    String filename = item.getName();
                    if (filename == null || filename.trim().equals("")) {
                        continue;
                    }
                    filename = filename.substring(filename.lastIndexOf("\\") + 1);
                    InputStream in = item.getInputStream();
                    String fileName = realSavePath + filename;
                    //拼接json数据
                    json.put("msg", "文件上传成功");
                    json.put("code", 1);
                    json.put("imgPath", PropertiesUtils.getProperty("HEAD_PORTRAIT_URL") + filename);
                    print.print(json.toJSONString());
                    FileOutputStream out = new FileOutputStream(fileName);
                    byte buffer[] = new byte[1024];
                    int len = 0;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    in.close();
                    out.close();
                    item.delete();
                }
            }
        } catch (FileUploadBase.FileSizeLimitExceededException e) {
            e.printStackTrace();
            request.setAttribute("message", "单个文件超出最大值！！！");
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String makePath(String savePath) {
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return savePath;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }


}
