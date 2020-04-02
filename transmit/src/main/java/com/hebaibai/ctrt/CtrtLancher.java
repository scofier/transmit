package com.hebaibai.ctrt;

import com.hebaibai.ctrt.transmit.config.CrtrConfig;
import com.hebaibai.ctrt.transmit.config.FileTypeConfig;
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

    /**
     * vert.x 部署 启动
     *
     * @param configFilePath 配置文件绝对路径
     * @throws Exception
     */
    public void start(String configFilePath) throws Exception {
        TransmitVerticle transmitVerticle = new TransmitVerticle();

        CrtrConfig crtrConfig = new FileTypeConfig(vertx, configFilePath);

        transmitVerticle.setCrtrConfig(crtrConfig);

        //部署
        vertx.deployVerticle(transmitVerticle, res -> {
            if (res.succeeded()) {
                verticleIds.add(res.result());
            }
        });

        //数据库部署
        DataConfig dataConfig = crtrConfig.getDataConfig();
        DataBaseVerticle dataBaseVerticle = new DataBaseVerticle();
        if (dataConfig != null) {
            AsyncSQLClient sqlClient = MySQLClient.createShared(
                    vertx, dataConfig.getJson(), "plumber_pool:" + dataConfig.getHost()
            );
            dataBaseVerticle.setSqlClient(sqlClient);
        }
        dataBaseVerticle.init(vertx, this.context);
        vertx.deployVerticle(dataBaseVerticle, res -> {
            if (res.succeeded()) {
                String id = res.result();
                verticleIds.add(id);
            }
        });

    }
}
