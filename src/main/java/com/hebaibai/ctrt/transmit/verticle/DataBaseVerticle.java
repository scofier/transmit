package com.hebaibai.ctrt.transmit.verticle;


import com.alibaba.fastjson.JSONObject;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.JULLogDelegateFactory;
import io.vertx.core.spi.logging.LogDelegate;
import io.vertx.ext.asyncsql.AsyncSQLClient;

/**
 * @author hjx
 */
public class DataBaseVerticle extends AbstractVerticle {

    public static String EXECUTE_SQL_INSERT = "execute-sql-insert";
    public static String EXECUTE_SQL_UPDATE = "execute-sql-update";

    private AsyncSQLClient sqlClient;

    private static LogDelegate log = new JULLogDelegateFactory().createDelegate(DataBaseVerticle.class.getName());

    public DataBaseVerticle(AsyncSQLClient sqlClient) {
        this.sqlClient = sqlClient;
    }

    @Override
    public void start() throws Exception {
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer(EXECUTE_SQL_INSERT, this::insert);
        eventBus.consumer(EXECUTE_SQL_UPDATE, this::update);
    }

    @Override
    public void stop() throws Exception {
    }

    /**
     * 执行更新
     *
     * @param jsonMsg
     */
    public void update(Message<String> jsonMsg) {
        JSONObject sqlParams = JSONObject.parseObject(jsonMsg.body());
        String sql = "UPDATE `api_log` SET `receive` = ?, `end_time` = now(), `status` = 1 WHERE `id` = ? ";
        JsonArray params = new JsonArray().add(sqlParams.getString("receive")).add(sqlParams.getString("id"));

        log.debug(sql);
        sqlClient.updateWithParams(sql, params, res -> {
            if (res.succeeded()) {
                jsonMsg.reply(res.result().getUpdated());
            } else {
                jsonMsg.fail(500, res.cause().toString());
                res.cause().printStackTrace();
                log.error("update", res.cause());
            }
        });
    }

    /**
     * 执行新增
     *
     * @param jsonMsg
     */
    public void insert(Message<String> jsonMsg) {
        JSONObject sqlParams = JSONObject.parseObject(jsonMsg.body());
        String sql = "INSERT INTO `api_log` (`id`, `type_code`, `send_msg`, `create_time`, `status`) VALUES (?, ?, ?, now(), ?)";
        JsonArray params = new JsonArray().add(sqlParams.getString("id")).add(sqlParams.getString("type_code"))
                .add(sqlParams.getString("send_msg")).add(0);

        log.debug(sql);
        sqlClient.updateWithParams(sql, params, res -> {
            if (res.succeeded()) {
                jsonMsg.reply(res.result().getUpdated());
            } else {
                jsonMsg.fail(500, res.cause().toString());
                res.cause().printStackTrace();
                log.error("insert", res.cause());
            }
        });

    }
}
