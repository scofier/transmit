package com.hebaibai.ctrt.transmit;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.datagram.DatagramSocket;

public class Main extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        DatagramSocket socket = vertx.createDatagramSocket();
        socket.listen(20809, "0.0.0.0", asyncResult -> {
            if (asyncResult.succeeded()) {
                socket.handler(event -> {
                    String body = event.data().toString();
                    System.out.println(body);
                });
            }
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new Main());
    }
}
