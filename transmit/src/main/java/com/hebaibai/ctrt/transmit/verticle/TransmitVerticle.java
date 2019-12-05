package com.hebaibai.ctrt.transmit.verticle;

import com.hebaibai.ctrt.transmit.Config;
import com.hebaibai.ctrt.transmit.RouterVo;
import com.hebaibai.ctrt.transmit.TransmitConfig;
import com.hebaibai.ctrt.transmit.util.Convert;
import com.hebaibai.ctrt.transmit.util.CrtrUtils;
import com.hebaibai.ctrt.transmit.util.Param;
import com.hebaibai.ctrt.transmit.util.Request;
import com.hebaibai.ctrt.transmit.util.ext.Ext;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;

import static com.hebaibai.ctrt.transmit.util.CrtrUtils.CHARSET_NAME;

/**
 * 请求转发Verticle
 *
 * @author hjx
 */
@Slf4j
public class TransmitVerticle extends AbstractVerticle {


    @Getter
    @Setter
    private Config config;

    private HttpServer httpServer;

    private WebClient webClient;

    private EventBus eventBus;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        httpServer = vertx.createHttpServer();
        this.webClient = WebClient.create(vertx);
    }

    @Override
    public void start() {
        Router router = Router.router(vertx);
        router.route().handler(this::requestBody);
        //转换请求数据,并发送请求
        router.route().handler(this::convert);
        //请求数据
        router.route().handler(this::request);
        //转换响应数据,并发返回
        router.route().handler(this::convertAndReturn);
        //开启路由
        httpServer.requestHandler(router).listen(config.getPort());
        //事件总线
        eventBus = vertx.eventBus();
    }

    @Override
    public void stop() {
    }

    /**
     * 获取转换所需要的数据
     *
     * @param routingContext
     */
    private void requestBody(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        HttpMethod method = request.method();
        //去除重复的'/'符号
        String path = new File(request.path()).getPath();
        TransmitConfig transmitConfig = config.get(method, path);
        //没有找到配置
        if (transmitConfig == null) {
            routingContext.response().end(error("not find config"), CHARSET_NAME);
            return;
        }
        request.bodyHandler(event -> {
            String requestBody = event.toString(CHARSET_NAME);
            RouterVo routerVo = new RouterVo();
            routerVo.setTransmitConfig(transmitConfig);
            routerVo.setMethod(method);
            routerVo.setParams(request.params());
            routerVo.setBody(requestBody);
            routerVo.setPath(path);
            routerVo.setTypeCode(transmitConfig.getCode());
            log.info("request {} befor:\n {}", routerVo.getUuid(), requestBody);
            //保存请求记录
            eventBus.send(DataBaseVerticle.EXECUTE_SQL_INSERT, routerVo.getInsertJsonStr());
            routingContext.put(RouterVo.class.getName(), routerVo);
            routingContext.next();
        });
    }

    /**
     * 转换请求数据
     *
     * @param routingContext
     */
    private void convert(RoutingContext routingContext) {
        RouterVo routerVo = routingContext.get(RouterVo.class.getName());
        TransmitConfig transmitConfig = routerVo.getTransmitConfig();
        Ext ext = CrtrUtils.ext(transmitConfig.getExtCode());
        try {
            //接受请求的参数
            Param param = CrtrUtils.param(routerVo.getMethod(), transmitConfig.getReqType());
            Convert convert = CrtrUtils.convert(transmitConfig.getReqType(), transmitConfig.getApiReqType());
            if (convert == null) {
                routingContext.response().end(error("not find convert util"), CHARSET_NAME);
                return;
            }
            if (param == null) {
                routingContext.response().end(error("not find param util"), CHARSET_NAME);
                return;
            }
            Map<String, Object> map = param.params(routerVo);
            log.info("request {} map:\n {}", routerVo.getUuid(), map);

            //插件,获取请求体后,转换参数格式前
            ext.beforRequestConvert(routerVo.getBody(), map);
            //转换请求参数,使其符合目标接口
            String apiReqFtlText = transmitConfig.getApiReqFtlText();
            log.debug("request {} ftl:\n {}", routerVo.getUuid(), apiReqFtlText);
            String value = convert.convert(map, apiReqFtlText, transmitConfig.getCode() + "-REQ");


            //插件, 数据转换后, 请求接口前
            value = ext.beforRequest(value, map);
            log.info("request {} after:\n {}", routerVo.getUuid(), value);
            //更新body
            routerVo.setBody(value);
            routingContext.put(RouterVo.class.getName(), routerVo);
            routingContext.next();
        } catch (Exception e) {
            log.error("request " + routerVo.getUuid() + " error", e);
            routingContext.response().end(error(e), CHARSET_NAME);
            return;
        }
    }

    /**
     * 发送请求
     *
     * @param routingContext
     */
    private void request(RoutingContext routingContext) {
        RouterVo routerVo = routingContext.get(RouterVo.class.getName());
        TransmitConfig transmitConfig = routerVo.getTransmitConfig();
        try {
            String value = routerVo.getBody();
            //转发数据
            Request request = CrtrUtils.request(transmitConfig.getApiMethod(), transmitConfig.getApiReqType());
            if (request == null) {
                routingContext.response().end(error("not find request util"), CHARSET_NAME);
                return;
            }
            request.request(webClient, value, transmitConfig.getApiPath(), transmitConfig.getTimeout(), event -> {
                if (!event.succeeded()) {
                    routingContext.response().end(error(event.cause()), CHARSET_NAME);
                } else {
                    //响应数据
                    String body = event.result().bodyAsString(CHARSET_NAME);
                    if (StringUtils.isBlank(body)) {
                        log.error("request " + routerVo.getUuid() + " error no response body");
                        routingContext.response().end(error("no response body"), CHARSET_NAME);
                        return;
                    }
                    //更新body中的值, 设置为接口返回值
                    routerVo.setBody(body);
                    //保存接口返回参数
                    eventBus.send(DataBaseVerticle.EXECUTE_SQL_UPDATE, routerVo.getUpdateJsonStr());
                    routingContext.next();
                }
                return;
            });
        } catch (Exception e) {
            log.error("request " + routerVo.getUuid() + " error", e);
            routingContext.response().end(error(e), CHARSET_NAME);
            return;
        }
    }

    /**
     * 转换响应数据,并发返回
     *
     * @param routingContext
     */
    private void convertAndReturn(RoutingContext routingContext) {
        RouterVo routerVo = routingContext.get(RouterVo.class.getName());
        TransmitConfig transmitConfig = routerVo.getTransmitConfig();
        log.info("response {} befor:\n {}", routerVo.getUuid(), routerVo.getBody());
        try {
            //取响应参数 和 转换 按照Post形式(从 body 中解析)
            Param param = CrtrUtils.param(HttpMethod.POST, transmitConfig.getApiResType());
            Convert convert = CrtrUtils.convert(transmitConfig.getApiResType(), transmitConfig.getResType());
            if (convert == null) {
                routingContext.response().end(error("not find convert util"), CHARSET_NAME);
                return;
            }
            if (param == null) {
                routingContext.response().end(error("not find param util"), CHARSET_NAME);
                return;
            }

            //插件, 请求接口后, 转换响应前
            Map<String, Object> map = param.params(routerVo);
            Ext ext = CrtrUtils.ext(transmitConfig.getExtCode());
            ext.afterResponse(routerVo.getBody(), map);


            String apiResFtlText = transmitConfig.getApiResFtlText();
            log.debug("response {} ftl:\n {}", routerVo.getUuid(), apiResFtlText);
            String value = convert.convert(map, apiResFtlText, transmitConfig.getCode() + "-RES");
            log.info("response {} after:\n {}", routerVo.getUuid(), value);
            //返回响应结果
            routingContext.response().end(value, CHARSET_NAME);
        } catch (Exception e) {
            log.error("response " + routerVo.getUuid() + " error", e);
            routingContext.response().end(error(e), CHARSET_NAME);
            return;
        }
    }

    /**
     * 返回异常信息
     *
     * @param msg
     * @return
     */
    private String error(String msg) {
        return error(new RuntimeException(msg));
    }

    private String error(Throwable throwable) {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"error_code\": \"0\", \"error_msg\": \"");
        if (throwable == null) {
            builder.append("Internal Server Error");
        } else {
            builder.append(throwable.getMessage());
        }
        builder.append("\" }");
        return builder.toString();
    }

}
