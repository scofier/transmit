package com.hebaibai.ctrt.transmit.verticle;


import com.alibaba.fastjson.JSONObject;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 没有配置sqlClient时，不做任何处理
 *
 * @author hjx
 */
@Slf4j
public class DataBaseVerticle extends AbstractVerticle {

    /**
     * 插入请求日志
     */
    public static String EXECUTE_SQL_INSERT = "execute_sql_insert";

    /**
     * 更新请求日志
     */
    public static String EXECUTE_SQL_UPDATE = "execute_sql_update";

    /**
     * 获取所有的配置项code
     */
    public static String EXECUTE_SQL_ALL_CONFIG = "execute_sql_all_config";

    private static String[] ADDRESS = {
            EXECUTE_SQL_INSERT,
            EXECUTE_SQL_UPDATE,
            EXECUTE_SQL_ALL_CONFIG
    };

    @Setter
    private MySQLPool mySQLPool;

    @Override
    public void start() throws Exception {
        EventBus eventBus = vertx.eventBus();
        if (mySQLPool == null) {
            for (String address : ADDRESS) {
                eventBus.consumer(address, this::log);
            }
        } else {
            eventBus.consumer(EXECUTE_SQL_INSERT, this::insert);
            eventBus.consumer(EXECUTE_SQL_UPDATE, this::update);
            eventBus.consumer(EXECUTE_SQL_ALL_CONFIG, this::allConfig);
        }
    }

    @Override
    public void stop() throws Exception {
    }

    /**
     * 没有配置sqlClient时的处理
     *
     * @param jsonMsg
     */
    private void log(Message<String> jsonMsg) {
    }

    /**
     * 执行更新
     *
     * @param jsonMsg
     */
    public void update(Message<String> jsonMsg) {
        JSONObject sqlParams = JSONObject.parseObject(jsonMsg.body());
        String sql = "UPDATE `api_log` SET `receive` = ?, `end_time` = now(), `status` = 1 WHERE `id` = ? ";
        log.debug(sql);
        mySQLPool.preparedQuery(sql, Tuple.of(
                sqlParams.getString("receive"),
                sqlParams.getString("id")
        ), event -> {
            if (event.succeeded()) {
                RowSet result = event.result();
                jsonMsg.reply(result.size());
            } else {
                jsonMsg.fail(500, event.cause().toString());
                event.cause().printStackTrace();
                log.error("update", event.cause());
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
        log.debug(sql);
        mySQLPool.preparedQuery(sql, Tuple.of(
                sqlParams.getString("id"),
                sqlParams.getString("type_code"),
                sqlParams.getString("send_msg"),
                0
        ), event -> {
            if (event.succeeded()) {
                RowSet result = event.result();
                jsonMsg.reply(result.size());
            } else {
                jsonMsg.fail(500, event.cause().toString());
                event.cause().printStackTrace();
                log.error("insert", event.cause());
            }
        });

    }

    /**
     * 获取所有的配置项的code
     *
     * @param jsonMsg
     */
    private void allConfig(Message<JsonArray> jsonMsg) {
        String sql = "select * from api_config where status = 1;";
        log.debug(sql);
        mySQLPool.query(sql, res -> {
            if (res.succeeded()) {
                RowSet result = res.result();
                JsonArray jsonArray = toJsonArray(result);
                jsonMsg.reply(jsonArray);
            } else {
                jsonMsg.fail(500, res.cause().toString());
                res.cause().printStackTrace();
                log.error("allConfigCode", res.cause());
            }
        });
    }

    JsonArray toJsonArray(RowSet rowSet) {
        List<String> columnsNames = rowSet.columnsNames();
        RowIterator iterator = rowSet.iterator();
        JsonArray jsonArray = new JsonArray();
        while (iterator.hasNext()) {
            Row next = iterator.next();
            JsonObject jsonObject = new JsonObject();
            for (String columnsName : columnsNames) {
                Object value = next.getValue(columnsName);
                jsonObject.put(columnsName, value);
            }
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
