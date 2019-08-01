package com.hebaibai.ctrt.transmit;

import com.hebaibai.ctrt.transmit.router.TransmitRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;

public class TransmitVerticle extends AbstractVerticle {


    @Getter
    @Setter
    private Config config;

    private HttpServer httpServer;

    private WebClient webClient;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        httpServer = vertx.createHttpServer();
        webClient = WebClient.create(vertx);
    }

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        Iterator<TransmitRouter> iterator = config.getRouters().iterator();
        while (iterator.hasNext()) {
            TransmitRouter handler = iterator.next();
            handler.init(vertx);
            router.route()
                    .method(handler.reqMethod())
                    .path(handler.reqPath())
                    .handler(handler);
        }
        httpServer.requestHandler(router).listen(config.getPort());
    }


    @Override
    public void stop(Future<Void> stopFuture) throws Exception {

    }


}
