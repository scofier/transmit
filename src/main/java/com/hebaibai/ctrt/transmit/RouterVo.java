package com.hebaibai.ctrt.transmit;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import lombok.Data;

import java.util.UUID;

@Data
public class RouterVo {

    private String uuid = UUID.randomUUID().toString();

    private String path;

    private String body;

    private MultiMap params;

    private HttpMethod method;

    private TransmitConfig transmitConfig;
}
