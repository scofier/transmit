package com.hebaibai.ctrt.transmit.util;

import java.util.Map;

public interface Convert extends Support {

    String convert(Map<String, Object> objectMap, String ftl, String ftlName) throws Exception;

}
