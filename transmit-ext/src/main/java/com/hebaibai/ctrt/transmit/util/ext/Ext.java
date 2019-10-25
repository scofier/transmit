package com.hebaibai.ctrt.transmit.util.ext;

import java.util.Map;

/**
 * 插件
 */
public interface Ext {

    /**
     * @param extCode
     * @return
     */
    boolean support(String extCode);

    /**
     * 获取插件编号
     *
     * @return
     */
    String getCode();

    /**
     * 在api请求前前执行
     *
     * @param value    完整的数据
     * @param valueMap 放进freemarker的数据
     * @return 插件处理后的数据
     */
    String beforRequest(String value, Map<String, Object> valueMap) throws Exception;

    /**
     * 在api响应后执行
     *
     * @param value    完整的数据
     * @param valueMap 放进freemarker的数据
     * @return
     */
    String afterResponse(String value, Map<String, Object> valueMap) throws Exception;
}
