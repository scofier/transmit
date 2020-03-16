package com.hebaibai.ctrt.transmit.util;

import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.TransmitConfig;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.WebClient;

public interface Request {

    String CONTENT_TYPE = "Content-Type";

    boolean support(HttpMethod method, DataType dataType);

    /**
     * 发送请求
     *
     * @param webClient
     * @param transmitConfig
     * @param param
     * @param handler
     */
    void request(WebClient webClient, TransmitConfig transmitConfig, String param, Handler<AsyncResult<String>> handler);

}
