package com.silita.biaodaa.utils;

import com.aliyun.oss.OSSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by zhushuai on 2019/4/22.
 */
public class OSSUtils {

    private static Logger logger = LoggerFactory.getLogger(OSSClient.class);

    private static String accessKeyId = PropertiesUtils.getProperty("accesskey.id");
    private static String accessKeySecret = PropertiesUtils.getProperty("accesskey.secret");
    private static String endpoint = PropertiesUtils.getProperty("endpoint");
    private static String bucketName = PropertiesUtils.getProperty("bucket.name");
    private static String bucketServer = PropertiesUtils.getProperty("bucket.server");

    /**
     * 上传阿里云OSS
     *
     * @param file        需上传的文件
     * @param outFileName 文件名
     * @return
     */
    public static String uploadOSS(File file, String outFileName) {
        OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        logger.info("--------------上传OSS开始---------");
        client.putObject(bucketName, outFileName, file);
        client.shutdown();
        String resultUrl = bucketServer + "/" + outFileName;
        file.deleteOnExit();
        logger.info("--------------上传OSS成功，文件名：" + resultUrl + "---------");
        return resultUrl;
    }

}
