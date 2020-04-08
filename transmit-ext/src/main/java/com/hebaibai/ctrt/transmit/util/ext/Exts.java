package com.hebaibai.ctrt.transmit.util.ext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Exts {

    /**
     * 默认的ext
     */
    public final static Class<? extends Ext> BASE_EXT = BaseExt.class;

    /**
     * 插件. 可扩展
     * key   : ext code
     * value : ext class
     */
    private static Map<String, Class<? extends Ext>> EXT_MAP = new HashMap<>();

    /**
     * 插件说明
     * key   : ext code
     * value : ext 说明
     */
    private static Map<String, String> EXT_EXPLAIN = new HashMap<>();

    /**
     * 通过编号获取ext
     *
     * @param extCode
     * @return
     */
    public static Class<? extends Ext> get(String extCode) {
        if (extCode == null) {
            return BASE_EXT;
        }
        return EXT_MAP.get(extCode);
    }

    /**
     * 添加ext
     *
     * @param extCode
     * @param extClass
     */
    public static void add(String extCode, Class<? extends Ext> extClass) {
        add(extCode, extClass, null);
    }

    /**
     * 添加ext
     *
     * @param extCode
     * @param extClass
     * @param explain
     */
    public static void add(String extCode, Class<? extends Ext> extClass, String explain) {
        if (extCode == null) {
            throw new UnsupportedOperationException("ext code is null");
        }
        if (EXT_MAP.containsKey(extCode)) {
            throw new UnsupportedOperationException("ext code already exists");
        }
        if (explain == null) {
            explain = "";
        }
        EXT_MAP.put(extCode, extClass);
        EXT_EXPLAIN.put(extCode, explain);
    }

    /**
     * 获取所有的ext编号
     *
     * @return
     */
    public static Set<String> codes() {
        return EXT_MAP.keySet();
    }

    /**
     * 获取ext说明
     *
     * @param extCode
     * @return
     */
    public static String explain(String extCode) {
        return EXT_EXPLAIN.get(extCode);
    }

}
