package com.hebaibai.ctrt.transmit.util.sign;

import com.hebaibai.ctrt.transmit.util.Sign;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 基础的签名-不签名
 */
public class BaseSign implements Sign {

    /**
     * signCode isBlank => true
     *
     * @param signCode
     * @return
     */
    @Override
    public boolean support(String signCode) {
        return true;
    }

    @Override
    public String sign(String value) {
        return value;
    }

    @Override
    public boolean verify(Map<String, Object> value) {
        return true;
    }
}
