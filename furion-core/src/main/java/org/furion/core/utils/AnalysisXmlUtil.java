package org.furion.core.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Functional description
 *
 * @author Leo
 * @date 2019-12-18
 */
public class AnalysisXmlUtil {

    /**
     * 使用jaxb将对象转换为xml字符串
     *
     * @param obj
     * @return
     */
    public static String objToXML(Object obj) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
        StringWriter writer = new StringWriter();
        Marshaller marshaller = jaxbContext.createMarshaller();
        //设置编码格式
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        //设置否是格式化xml
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        //是否省略头信息
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
        //设置schema约束的命名空间
//        marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper() {
//            @Override
//            public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
//                if (XMLSchemaDict.NAMESPACE_S1.equals(namespaceUri))
//                    return XMLSchemaDict.NAMESPACE_S1_PREFIX;
//                if (XMLSchemaDict.NAMESPACE_S2.equals(namespaceUri))
//                    return XMLSchemaDict.NAMESPACE_S2_PREFIX;
//                return suggestion;
//            }
//        });
        marshaller.marshal(obj, writer);

        return writer.toString();
    }

    /**
     * 将String类型的xml转换成对象
     */
    public static Object convertXmlStrToObject(Class clazz, String xmlStr) {
        Object xmlObject = null;
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            // 进行将Xml转成对象的核心接口
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader sr = new StringReader(xmlStr);
            xmlObject = unmarshaller.unmarshal(sr);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return xmlObject;
    }
}
