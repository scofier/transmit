package com.hebaibai.crtr.test;

import com.alibaba.fastjson.JSONObject;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 数据转换提供的接口
 */
public class Main extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        router.post("/test/xml").handler(event -> {
            event.request().bodyHandler(body -> {
                event.request().response().end("<xml>" +
                        "<status>" + new Random().nextInt() + "</status>" +
                        "<result>" + UUID.randomUUID().toString() + "</result>" +
                        "</xml>");
            });
        });

        router.post("/test/json").handler(event -> {
            event.request().bodyHandler(body -> {
                Map map = new HashMap();
                map.put("status", new Random().nextInt() + "");
                map.put("result", UUID.randomUUID().toString());
                event.request().response().end(JSONObject.toJSONString(map));
            });
        });

        vertx.createHttpServer().requestHandler(router).listen(8891);
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.factory.vertx();
        vertx.deployVerticle(new Main());
    }

}
