package com.hebaibai.ctrt.transmit.router;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

/**
 * 转换接口
 *
 * @author hjx
 */
public interface TransmitRouter extends Handler<RoutingContext> {

    String CHARSET_NAME = "utf-8";

    String CONTENT_TYPE = "Content-Type";

    /**
     * 请求路径
     *
     * @return
     */
    String reqPath();

    /**
     * 请求方式
     *
     * @return
     */
    HttpMethod reqMethod();


    /**
     * 初始化一些配置
     *
     * @param vertx
     */
    void init(Vertx vertx);
}
