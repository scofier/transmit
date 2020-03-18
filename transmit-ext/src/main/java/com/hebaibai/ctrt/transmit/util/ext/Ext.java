package com.hebaibai.ctrt.transmit.util.ext;

import io.vertx.core.Handler;
import io.vertx.core.Promise;

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
    void init(Map<String, Object> transmitJson);


    /**
     * 收到第三方系统请求后
     * 将收到的数据处理后,返回一个Map,
     * 在接下来的步骤中由freemarker处理成api接口中的报文
     *
     * @param value    请求体中的数据
     * @param valueMap 原始数据经过解析后得到的map(放进freemarker的数据)
     * @return
     */
    Map<String, Object> outRequestBodyMap(String value, Map<String, Object> valueMap) throws Exception;


    /**
     * 收到API接口的返回值后
     * 将收到的数据处理后,返回一个Map,
     * 在接下来的步骤中由freemarker处理成第三方系统的响应报文
     *
     * @param value
     * @param valueMap:{ ROOT:     api接口返回数据
     *                   REQUEST:  第三方系统的请求数据
     *                   }
     * @return
     * @throws Exception
     */
    Map<String, Object> apiResponseBodyMap(String value, Map<String, Object> valueMap) throws Exception;

    /**
     * 自定义请求方式
     *
     * @param value 转换完成的请求参数
     * @return
     */
    Handler<Promise<String>> getApiResult(String value);

}
