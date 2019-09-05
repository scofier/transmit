package com.hebaibai.ctrt.transmit;

import io.vertx.core.http.HttpMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Config {

    @Getter
    @Setter
    private int port;

    @Getter
    @Setter
    private DataConfig dataConfig;

    /**
     * 请求方式 对应数据转换
     */
    @Getter
    private Set<TransmitConfig> routers = new HashSet();

    /**
     * 添加对应关系
     *
     * @param data
     * @return
     */
    public void put(TransmitConfig data) {
        routers.add(data);
    }

    public TransmitConfig get(HttpMethod method, String path) {
        Iterator<TransmitConfig> iterator = routers.iterator();
        while (iterator.hasNext()) {
            TransmitConfig next = iterator.next();
            if (next.getReqMethod() == method && next.getReqPath().equals(path)) {
                return next;
            }
        }
        return null;
    }

}
