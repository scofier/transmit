package com.hebaibai.ctrt.transmit.util;

import org.apache.commons.lang3.StringUtils;

public class Assert {

    public static void isNotBlank(String val, String msg) {
        if (StringUtils.isNotBlank(val)) {
            return;
        }
        throw new RuntimeException(msg);
    }
}
