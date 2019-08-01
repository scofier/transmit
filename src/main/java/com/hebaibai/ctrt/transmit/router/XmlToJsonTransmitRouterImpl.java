package com.hebaibai.ctrt.transmit.router;

import com.hebaibai.ctrt.convert.reader.DataReader;
import com.hebaibai.ctrt.convert.reader.JsonDataReader;

import java.util.Map;

/**
 * 接受参数格式:XML
 * <p>
 * 转发接口格式:JSON
 *
 * @author hjx
 */
@SuppressWarnings("ALL")
public class XmlToJsonTransmitRouterImpl extends XmlToXmlTransmitRouterImpl {

    @Override
    protected boolean checkContentType(String header) {
        return "application/xml".equals(header);
    }

    @Override
    protected String relayContentType() {
        return "application/json";
    }

    @Override
    protected Map<String, Object> readResponse(String value) throws Exception {
        DataReader dataReader = new JsonDataReader();
        dataReader.read(value);
        return dataReader.getRequestData();
    }
}
