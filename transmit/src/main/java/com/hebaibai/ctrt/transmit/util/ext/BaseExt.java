package com.hebaibai.ctrt.transmit.util.ext;

import java.util.HashMap;
import java.util.Map;

/**
 * 基础的插件
 */
public class BaseExt implements Ext {

    private Map<String, Object> transmitConfig;

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
    public String getCode() {
        return "base_ext";
    }

    @Override
    public void setConfig(Map<String, Object> transmitConfig) {
        this.transmitConfig = transmitConfig;
    }

    @Override
    public void beforRequestConvert(String value, Map<String, Object> valueMap) throws Exception {

    }

    @Override
    public String beforRequest(String value, Map<String, Object> valueMap) throws Exception {
        return value;
    }

    @Override
    public void afterResponse(String value, Map<String, Object> valueMap) throws Exception {

    }

    @Override
    public Map<String, String> requestHeaders(String value) throws Exception {
        return new HashMap<>();
    }
}
