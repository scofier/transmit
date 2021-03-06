package com.hebaibai.ctrt.transmit.util.ext;

import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

import java.util.Map;

/**
 * 默认插件, 没有配置插件的时候,默认添加
 */
public final class BaseExt implements Ext {


    @Override
    public void init(Vertx vertx, Map<String, Object> transmitJson) {

    }

    /**
     * 默认原样返回
     *
     * @param value    请求体中的数据
     * @param valueMap 原始数据经过解析后得到的map(放进freemarker的数据)
     * @return
     * @throws Exception
     */
    @Override
    public Handler<Promise<Map<String, Object>>> outRequestBodyMap(String value, Map<String, Object> valueMap) {
        return event -> {
            event.complete(valueMap);
        };
    }

    /**
     * 默认原样返回
     *
     * @param value
     * @param valueMap :{ ROOT:     api接口返回数据
     *                 REQUEST:  第三方系统的请求数据
     *                 }
     * @return
     * @throws Exception
     */
    @Override
    public Handler<Promise<Map<String, Object>>> apiResponseBodyMap(String value, Map<String, Object> valueMap) {
        return event -> {
            event.complete(valueMap);
        };
    }

    /**
     * 不处理
     *
     * @param value 转换完成的请求参数
     * @return
     */
    @Override
    public Handler<Promise<String>> getApiResult(String value) {
        return null;
    }
}
