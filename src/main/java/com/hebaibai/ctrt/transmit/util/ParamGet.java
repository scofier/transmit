package com.hebaibai.ctrt.transmit.util;

import com.hebaibai.ctrt.transmit.RouterVo;

import java.util.Map;

/**
 * @author hjx
 */
public interface ParamGet extends Support {

    Map<String, Object> get(RouterVo routerVo);
}
