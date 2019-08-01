package com.hebaibai.ctrt.transmit.router;

import com.hebaibai.ctrt.convert.reader.DataReader;
import com.hebaibai.ctrt.convert.reader.XmlDataReader;

import java.util.Map;

/**
 * 接受参数格式:XML
 * <p>
 * 转发接口格式:XML
 * <p>
 *
 * @author hjx
 */
@SuppressWarnings("ALL")
public class XmlToXmlTransmitRouterImpl extends AbstractPostBodyTransmitRouter {

    /**
     * 校验请求格式
     *
     * @param header
     * @return
     */
    @Override
    boolean checkContentType(String header) {
        return "application/xml".equals(header) || "text/xml".equals(header);
    }

    /**
     * 转发接口类型
     *
     * @return
     */
    @Override
    String relayContentType() {
        return "application/xml";
    }

    /**
     * 读取请求中的参数
     *
     * @param body
     * @return
     */
    @Override
    Map<String, Object> readRequest(String body) throws Exception {
        DataReader dataReader = new XmlDataReader();
        dataReader.read(body);
        return dataReader.getRequestData();
    }

    /**
     * 读取响应中的参数
     *
     * @param value
     * @return
     */
    @Override
    Map<String, Object> readResponse(String value) throws Exception {
        XmlDataReader dataReader = new XmlDataReader();
        dataReader.read(value);
        return dataReader.getRequestData();
    }

}
