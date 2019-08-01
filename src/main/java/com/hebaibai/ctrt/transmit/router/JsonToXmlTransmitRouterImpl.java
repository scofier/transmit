package com.hebaibai.ctrt.transmit.router;

import com.hebaibai.ctrt.convert.reader.DataReader;
import com.hebaibai.ctrt.convert.reader.JsonDataReader;

import java.util.Map;

/**
 * 接受参数格式:JSON
 * <p>
 * 转发接口格式:XML
 *
 * @author hjx
 */
@SuppressWarnings("ALL")
public class JsonToXmlTransmitRouterImpl extends JsonToJsonTransmitRouterImplTo {

    @Override
    protected Map<String, Object> readRequest(String value) throws Exception {
        DataReader dataReader = new JsonDataReader();
        dataReader.read(value);
        return dataReader.getRequestData();
    }

}
