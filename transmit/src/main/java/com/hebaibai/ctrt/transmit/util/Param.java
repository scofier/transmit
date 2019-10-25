package com.hebaibai.ctrt.transmit.util;

import com.hebaibai.ctrt.transmit.DataType;
import com.hebaibai.ctrt.transmit.RouterVo;
import io.vertx.core.http.HttpMethod;

import java.util.Map;

/**
 * @author hjx
 */
public interface Param {

    boolean support(HttpMethod method, DataType dataType);

    Map<String, Object> params(RouterVo routerVo);
}
