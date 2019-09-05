package com.hebaibai.ctrt.transmit.util;

import java.util.Map;

/**
 * 签名工具
 */
public interface Sign {

    /**
     * @param signCode
     * @return
     */
    boolean support(String signCode);

    /**
     * 签名
     *
     * @param value 完整的数据
     * @return 签名处理后的数据
     */
    String sign(String value) throws Exception;

    /**
     * 验证签名
     *
     * @param value
     * @return
     */
    boolean verify(String value) throws Exception;
}
