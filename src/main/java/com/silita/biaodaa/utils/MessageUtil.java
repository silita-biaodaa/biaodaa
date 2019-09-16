package com.silita.biaodaa.utils;

import com.silita.biaodaa.model.weixin.Articles;
import com.silita.biaodaa.model.weixin.NewsMessage;
import com.silita.biaodaa.model.weixin.TextMessage;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangxiahui on 17/6/14.
 */
public class MessageUtil {
    public static final Logger log = Logger.getLogger(MessageUtil.class);
    public static final String MESSAGG_TYPE_TEXT = "text";
    public static final String MESSAGG_TYPE_IMAGE = "image";
    public static final String MESSAGG_TYPE_VOICE = "voice";
    public static final String MESSAGG_TYPE_VIDEO = "video";
    public static final String MESSAGG_TYPE_LOCATION = "location";
    public static final String MESSAGG_TYPE_LINK = "link";
    public static final String MESSAGG_TYPE_MUSIC = "music";
    public static final String MESSAGG_TYPE_NEWS = "news";
    public static final String MESSAGG_TYPE_EVENT = "event";
    public static final String EVENT_TYPE_CLICK = "CLICK";
    // 关注
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
    // 取消关注
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
    /**
     * 扩展XStream，使其支持CDATA
     */
    private static XStream xStream = new XStream(new XppDriver() {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有的XML节点增加CDATA标记
                boolean cdata = true;
                public void startNode(String name,Class clazz) {
                    super.startNode(name,clazz);
                }
                protected void writeText(QuickWriter writer,String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    }
                    else {
                        writer.write(text);
                    }
                }
            };
        }
    });
    /**
     * 解析微信发过来的请求（XML）
     *
     * @author qincd
     * @date Nov 4, 2014 11:03:55 AM
     */
    public static Map<String,String> parseXml(HttpServletRequest request) throws IOException, DocumentException {
        Map<String,String> map = new HashMap<String,String>();
        InputStream input = request.getInputStream();
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(input);
        Element root = document.getRootElement();
        List<Element> eles = root.elements();
        for (Element ele:eles) {
            log.info(ele.getName() + ":" + ele.getText());
            map.put(ele.getName(), ele.getText());
        }
        input.close();
        input = null;
        return map;
    }
    public static String textMessageToXml(TextMessage textMessage) {
        xStream.alias("xml", textMessage.getClass());
        return xStream.toXML(textMessage);
    }

    public static String NewsMessageToXml(NewsMessage newsMessage) {
        xStream.alias("xml", newsMessage.getClass());
        xStream.alias("item", new Articles().getClass());
        return xStream.toXML(newsMessage);
    }
}
