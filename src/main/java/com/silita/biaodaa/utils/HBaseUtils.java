package com.silita.biaodaa.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * Created by zhushuai on 2019/3/15.
 */
public class HBaseUtils {

    /**
     * 初始化连接
     * @return
     * @throws IOException
     */
    public static Connection init(String ip,String port,String master,String url) throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", ip);
        configuration.set("hbase.zookeeper.property.clientPort", port);
        configuration.set("hbase.master", master);
        configuration.set("hbase.rootdir", url);
        Connection connection = ConnectionFactory.createConnection(configuration);
        return connection;
    }

    /**
     * 关闭连接
     * @param connection
     */
    public static void close(Connection connection){
        if (null != connection){
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String ip = "192.168.2.101";
        String port = "2181";
        String master = "192.168.2.101:60000";
        String url = "hdfs://192.168.2.101:9000/hbase";
        try {
            Connection connection = init(ip,port,master,url);
            Table table = connection.getTable(TableName.valueOf("gsxt"));
            Get get = new Get("d82be405069001616cefd448d5bf83a1".getBytes()).setId("d82be405069001616cefd448d5bf83a1");
            Result result = table.get(get);
           showCell(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 格式化输出getTime
    public static void showCell(Result result) {
        Cell[] cells = result.rawCells();
        for (Cell cell : cells) {
            String key = Bytes.toString(CellUtil.cloneQualifier(cell));
            String value = Bytes.toString(CellUtil.cloneValue(cell));
            System.out.println(key+":"+value);
        }
    }
}
