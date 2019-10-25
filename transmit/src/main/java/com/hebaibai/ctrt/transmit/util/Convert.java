package com.hebaibai.ctrt.transmit.util;

import com.hebaibai.ctrt.transmit.DataType;

import java.util.Map;

public interface Convert {

    boolean support(DataType from, DataType to);

    String convert(Map<String, Object> objectMap, String ftl, String ftlName) throws Exception;

}
