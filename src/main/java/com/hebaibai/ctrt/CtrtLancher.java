package com.hebaibai.ctrt;

import com.hebaibai.ctrt.transmit.Config;
import com.hebaibai.ctrt.transmit.DataConfig;
import com.hebaibai.ctrt.transmit.verticle.DataBaseVerticle;
import com.hebaibai.ctrt.transmit.verticle.TransmitVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.MySQLClient;

import java.util.ArrayList;
import java.util.List;

/**
 * 转发器启动器
 *
 * @author hjx
 */
public class CtrtLancher {

    private Vertx vertx = Vertx.vertx();

    private Context context = vertx.getOrCreateContext();

    private List<String> verticleIds = new ArrayList<>();

    public void start(Config config) {
        TransmitVerticle transmitVerticle = new TransmitVerticle();
        transmitVerticle.setConfig(config);

        //部署
        vertx.deployVerticle(transmitVerticle, res -> {
            if (res.succeeded()) {
                verticleIds.add(res.result());
            }
        });

        //数据库部署
        DataConfig dataConfig = config.getDataConfig();
        DataBaseVerticle dataBaseVerticle = new DataBaseVerticle();
        if (dataConfig != null) {
            AsyncSQLClient sqlClient = MySQLClient.createShared(
                    vertx, dataConfig.getJson(), "plumber_pool:" + dataConfig.getHost()
            );
            dataBaseVerticle.setSqlClient(sqlClient);
        }
        dataBaseVerticle.init(vertx, context);
        vertx.deployVerticle(dataBaseVerticle, res -> {
            if (res.succeeded()) {
                String id = res.result();
                verticleIds.add(id);
            }
        });

    }
}
