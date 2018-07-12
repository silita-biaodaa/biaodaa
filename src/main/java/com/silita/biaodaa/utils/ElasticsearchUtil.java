package com.silita.biaodaa.utils;

import com.silita.biaodaa.elastic.common.NativeElasticSearchUtils;
import org.elasticsearch.client.transport.TransportClient;

public class ElasticsearchUtil {

    public static TransportClient transportClient = null;

    public static NativeElasticSearchUtils nativeElasticSearchUtils = null;

    private static String ip = PropertiesUtils.getProperty("ELASTICSEARCH_IP");

    private static String host = PropertiesUtils.getProperty("ELASTICSEARCH_HOST");

    private static String cluster = PropertiesUtils.getProperty("ELASTICSEARCH_CLUSTER");

    static {
        nativeElasticSearchUtils = new NativeElasticSearchUtils();
        transportClient = nativeElasticSearchUtils.initClient(ip, cluster, Integer.parseInt(host));
    }

}
