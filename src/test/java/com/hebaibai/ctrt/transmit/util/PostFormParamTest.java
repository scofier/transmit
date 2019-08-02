package com.hebaibai.ctrt.transmit.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class PostFormParamTest {

    @Test
    public void name() {

        Map map = new HashMap();
        String[] split = "name=&age=19".split("&");
        for (String nameParam : split) {
            String[] p = nameParam.split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            } else {
                map.put(p[0], null);
            }
        }
        System.out.println(map);
    }

    @Test
    public void post() {

    }
}