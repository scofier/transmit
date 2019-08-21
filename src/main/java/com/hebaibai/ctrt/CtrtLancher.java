package com.hebaibai.ctrt;

import com.hebaibai.ctrt.transmit.TransmitVerticle;
import com.hebaibai.ctrt.transmit.Config;
import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.List;

/**
 * 转发器启动器
 * @author hjx
 */
public class CtrtLancher {

    private Vertx vertx = Vertx.vertx();

    private List<String> verticleIds = new ArrayList<>();

    public void start(Config config) {
        TransmitVerticle transmitVerticle = new TransmitVerticle();
        transmitVerticle.setConfig(config);
        //部署
        vertx.deployVerticle(transmitVerticle, res -> {
            if (res.succeeded()) {
                verticleIds.add(res.result());
            }
        });
    }
}
