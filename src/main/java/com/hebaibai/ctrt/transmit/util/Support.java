package com.hebaibai.ctrt.transmit.util;

import com.hebaibai.ctrt.transmit.DataType;
import io.vertx.core.http.HttpMethod;

public interface Support {
    boolean support(HttpMethod method, DataType dataType);
}
