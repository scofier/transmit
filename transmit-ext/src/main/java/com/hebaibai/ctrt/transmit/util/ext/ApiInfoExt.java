package com.hebaibai.ctrt.transmit.util.ext;

import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;

import java.util.Map;

/**
 * 项目配置展示接口
 */
public final class ApiInfoExt implements Ext {

    private Vertx vertx;

    private Map<String, Object> transmitJson;

    private FileSystem fileSystem;

    @Override
    public void init(Vertx vertx, Map<String, Object> transmitJson) {
        this.vertx = vertx;
        this.transmitJson = transmitJson;
        this.fileSystem = vertx.fileSystem();

    }

    /**
     * 默认原样返回
     *
     * @param value    请求体中的数据
     * @param valueMap 原始数据经过解析后得到的map(放进freemarker的数据)
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> outRequestBodyMap(String value, Map<String, Object> valueMap) throws Exception {
        return valueMap;
    }

    /**
     * 默认原样返回
     *
     * @param value
     * @param valueMap:{ ROOT:     api接口返回数据
     *                   REQUEST:  第三方系统的请求数据
     *                   }
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> apiResponseBodyMap(String value, Map<String, Object> valueMap) throws Exception {
        return valueMap;
    }

    /**
     * 不处理
     *
     * @param value 转换完成的请求参数
     * @return
     */
    @Override
    public Handler<Promise<String>> getApiResult(String value) {
        return promise -> {
            Context context = vertx.getOrCreateContext();
            //配置文件绝对路径
            String configFilePath = context.get("config_file_path").toString();
            fileSystem.readFile(configFilePath, event -> {
                if (!event.succeeded()) {
                    promise.complete(event.cause().getMessage());
                } else {
                    String configJson = event.result().toString("utf-8");
                    JsonObject jsonObject = new JsonObject(configJson);
                    JsonObject config = jsonObject.getJsonObject("config");
                    promise.complete(configJson);
                }
            });
        };
    }
}
