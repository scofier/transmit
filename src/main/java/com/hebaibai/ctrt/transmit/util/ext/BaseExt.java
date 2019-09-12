package com.hebaibai.ctrt.transmit.util.ext;

import java.util.Map;

/**
 * 基础的插件
 */
public class BaseExt implements Ext {

    /**
     * extCode isBlank => true
     *
     * @param extCode
     * @return
     */
    @Override
    public boolean support(String extCode) {
        return true;
    }

    @Override
    public String beforRequest(String value, Map<String, Object> valueMap) {
        return value;
    }

    @Override
    public String afterResponse(String value, Map<String, Object> valueMap) {
        return value;
    }
}
