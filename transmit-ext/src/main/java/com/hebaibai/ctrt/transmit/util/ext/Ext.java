package com.hebaibai.ctrt.transmit.util.ext;

import io.vertx.core.AsyncResult;

import java.util.Map;

/**
 * 插件
 *
 * @author hjx
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
     * 设置参数
     * 并初始化插件
     *
     * @param transmitJson
     */
    void setConfig(Map<String, Object> transmitJson);


    /**
     * 获取请求体后
     * 转换参数格式前
     *
     * @param value    请求体中的数据
     * @param valueMap 原始数据(放进freemarker的数据)
     * @return
     */
    void beforRequestConvert(String value, Map<String, Object> valueMap) throws Exception;

    /**
     * 在api请求前前执行
     *
     * @param value    使用模板转换后的数据
     * @param valueMap 原始数据(放进freemarker的数据)
     * @return 插件处理后的数据
     */
    String beforRequest(String value, Map<String, Object> valueMap) throws Exception;

    /**
     * 在api响应后执行
     *
     * @param value    使用模板转换后的数据
     * @param valueMap 原始数据(放进freemarker的数据)
     * @return
     */
    void afterResponse(String value, Map<String, Object> valueMap) throws Exception;

    /**
     * 自定义请求方式
     *
     * @return
     */
    AsyncResult<String> getApiResult();

}
