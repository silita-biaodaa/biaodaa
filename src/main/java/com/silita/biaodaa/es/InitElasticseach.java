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

    public static TransportClient initClient() {
        TransportClient client = null;
        try {
            Settings settings = Settings.builder()
                    .put("cluster.name", cluster)
                    .put("client.transport.sniff", true)
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
