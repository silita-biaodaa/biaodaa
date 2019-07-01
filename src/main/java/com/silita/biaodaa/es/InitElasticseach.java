package com.silita.biaodaa.es;

import com.silita.biaodaa.utils.PropertiesUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class InitElasticseach {

    private static String ip = PropertiesUtils.getProperty("ELASTICSEARCH_IP");

    private static String host = PropertiesUtils.getProperty("ELASTICSEARCH_HOST");

    private static String cluster = PropertiesUtils.getProperty("ELASTICSEARCH_CLUSTER");

    private static String lawIp = PropertiesUtils.getProperty("BIAODAA_ELASTICSEARCH_IP");

    private static String lawHost = PropertiesUtils.getProperty("BIAODAA_ELASTICSEARCH_HOST");

    private static String lawCluster = PropertiesUtils.getProperty("BIAODAA_ELASTICSEARCH_CLUSTER");

    public static TransportClient initClient() {
        TransportClient client = init(ip,cluster,host);
        return client;
    }

    public static TransportClient initLawClient() {
        TransportClient client = init(lawIp,lawCluster,lawHost);
        return client;
    }

    private static TransportClient init(String ip,String cluster,String host){
        TransportClient client = null;
        try {
            Settings settings = Settings.builder()
                    .put("cluster.name", cluster)
                    .put("client.transport.sniff", false)
                    .build();
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ip), Integer.parseInt(host)));
            System.out.println(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return client;
    }
}
