package com.hebaibai.crtr.test;

import com.alibaba.fastjson.JSONObject;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import java.util.concurrent.TimeUnit;

/**
 * 数据转换提供的接口
 */
public class Main extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        /**
         * request  :{"p-name": "name"}
         * response :<xml><name>name</name></xml>
         */
        router.post("/test/demo").handler(event -> {
            event.request().bodyHandler(body -> {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String string = body.toString();
                JSONObject jsonObject = JSONObject.parseObject(string);
                event.request().response().end("<xml><name>" + jsonObject.getString("p-name") + "</name></xml>");
            });
        });
        vertx.createHttpServer().requestHandler(router).listen(8891);
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.factory.vertx();
        vertx.deployVerticle(new Main());
    }

}
