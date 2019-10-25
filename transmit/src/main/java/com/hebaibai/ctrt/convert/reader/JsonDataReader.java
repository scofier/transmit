package com.hebaibai.ctrt.convert.reader;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * json格式的报文解析
 */
public class JsonDataReader extends AbstractDataReader implements DataReader {

    /**
     * 解析json格式的报文
     *
     * @param stringData
     * @return
     */
    @Override
    public void read(String stringData) {
        this.beConvertData = stringData;
        for (char c : stringData.toCharArray()) {
            if (c == '\u0000') {
                continue;
            }
            if (c == '[') {
                ArrayList list = JSONObject.parseObject(stringData, ArrayList.class);
                this.requestData = new HashMap(1);
                this.requestData.put(ROOT_NAME, list);
                return;
            }
            if (c == '{') {
                this.requestData = new HashMap<>();
                this.requestData.put(ROOT_NAME, JSONObject.parseObject(stringData, HashMap.class));
                return;
            }
        }
        throw new RuntimeException("解析json异常:\n" + stringData);
    }

}
