package com.hebaibai.ctrt.transmit.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hebaibai.ctrt.transmit.TransmitConfig;
import com.hebaibai.ctrt.transmit.util.CrtrUtils;
import com.hebaibai.ctrt.transmit.util.ext.Exts;
import com.hebaibai.ctrt.transmit.verticle.DataBaseVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author hjx
 */
@Slf4j
public class DbTypeConfig implements CrtrConfig {

    /**
     * vertx 实例
     */
    private Vertx vertx;


    /**
     * 启动时监听的端口
     */
    private int port;

    /**
     * 是否缓{
     * 转发配置
     * }
     */
    private boolean cache;

    /**
     * 事件(调用数据库查询配置)
     */
    private EventBus eventBus;


    /**
     * 构造函数
     *
     * @param configFilePath
     * @throws IOException
     */
    public DbTypeConfig(Vertx vertx, String configFilePath) throws Exception {
        this.vertx = vertx;
        String fileText = CrtrUtils.getFileText(configFilePath);
        JSONObject jsonObject = JSONObject.parseObject(fileText);
        //获取系统配置
        JSONObject configJson = jsonObject.getJSONObject("config");
        this.eventBus = vertx.eventBus();
        initConfig(configJson);
        if (cache) {
            eventBus.request(DataBaseVerticle.EXECUTE_SQL_ALL_CONFIG, null, (Handler<AsyncResult<Message<JsonArray>>>) event -> {
                if (event.succeeded()) {
                    JsonArray body = event.result().body();
                    System.out.println(body);
                }
            });
        }
    }

    /**
     * 获取 配置
     *
     * @param method
     * @param path
     * @return
     */
    @Override
    public Handler<Promise<TransmitConfig>> transmitConfig(HttpMethod method, String path) {
        return event -> {
            event.fail("not find config  method: " + method + " path: " + path);
        };
    }

    @Override
    public int getPort() {
        return port;
    }


    /**
     * 加载 配置文件 config 节点
     *
     * @param configJson
     */
    private void initConfig(JSONObject configJson) throws IOException {
        //获取系统端口配置
        this.port = configJson.getInteger("port");
        log.info("init port: {}", port);
        //是否缓存模板
        if (configJson.containsKey("cache")) {
            this.cache = configJson.getBoolean("cache");
        } else {
            this.cache = true;
        }
        //插件加载
        if (!configJson.containsKey("ext")) {
            return;
        }
        JSONArray ext = configJson.getJSONArray("ext");
        for (int i = 0; i < ext.size(); i++) {
            String extClassName = ext.getString(i);
            try {
                Class.forName(extClassName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        for (String extCode : Exts.codes()) {
            log.info("load ext code {}", extCode);
        }
    }
}
