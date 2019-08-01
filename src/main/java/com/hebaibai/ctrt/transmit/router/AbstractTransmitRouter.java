package com.hebaibai.ctrt.transmit.router;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 公用抽象类
 *
 * @author hjx
 */
public abstract class AbstractTransmitRouter implements TransmitRouter {

    protected Vertx vertx;

    protected String reqPath;

    protected String relayPath;

    protected HttpMethod reqMethod;

    protected HttpMethod relayMethod;

    protected File reqFile;

    protected String reqFileText;

    protected File relayFile;

    protected String relayFileText;

    protected WebClient webClient;

    @Override
    public String reqPath() {
        return this.reqPath;
    }

    @Override
    public HttpMethod reqMethod() {
        return this.reqMethod;
    }

    @Override
    public void handle(RoutingContext event) {
        HttpServerRequest request = event.request();
        HttpServerResponse response = event.response();
        transmit(request, response);
    }

    @Override
    public void init(Vertx vertx) {
        this.vertx = vertx;
        this.webClient = WebClient.create(vertx);
    }

    public void relayMethod(HttpMethod relayMethod) {
        this.relayMethod = relayMethod;
    }

    public void reqMethod(HttpMethod reqMethod) {
        this.reqMethod = reqMethod;
    }


    public void reqPath(String reqPath) {
        this.reqPath = reqPath;
    }

    public void relayPath(String relayPath) {
        this.relayPath = relayPath;
    }

    public void reqFile(File file) throws IOException {
        this.reqFile = file;
        this.reqFileText = fileToText(file);
    }

    public void relayFile(File file) throws IOException {
        this.relayFile = file;
        this.relayFileText = fileToText(file);
    }

    protected abstract void transmit(HttpServerRequest request, HttpServerResponse response);

    private String fileToText(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[fileInputStream.available()];
        fileInputStream.read(bytes);
        return new String(bytes, CHARSET_NAME);
    }

    protected String errorMsg(Throwable e) {
        if (e == null) {
            return "error";
        }
        String message = e.getMessage();
        if (StringUtils.isNotBlank(message)) {
            return message;
        }
        return e.getClass().getName();
    }

}
