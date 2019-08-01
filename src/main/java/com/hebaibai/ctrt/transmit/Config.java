package com.hebaibai.ctrt.transmit;

import com.hebaibai.ctrt.transmit.router.TransmitRouter;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

public class Config {

    @Getter
    @Setter
    private int port;

    /**
     * 请求方式 对应数据转换
     */
    @Getter
    private Set<TransmitRouter> routers = new HashSet();

    /**
     * 添加对应关系
     *
     * @param router
     * @return
     */
    public boolean put(TransmitRouter router) {
        routers.add(router);
        return true;
    }

}
