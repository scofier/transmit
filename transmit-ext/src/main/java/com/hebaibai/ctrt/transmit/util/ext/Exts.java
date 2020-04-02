package com.hebaibai.ctrt.transmit.util.ext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Exts {

    /**
     * 默认的ext
     */
    public final static Ext BASE_EXT = new BaseExt();

    /**
     * 插件. 可扩展
     * key   : ext code
     * value : ext class
     */
    private static Map<String, Ext> EXT_MAP = new HashMap<>();

    /**
     * 通过编号获取ext
     *
     * @param extCode
     * @return
     */
    public static Ext get(String extCode) {
        if (extCode == null) {
            return BASE_EXT;
        }
        return EXT_MAP.get(extCode);
    }

    /**
     * 添加ext
     *
     * @param extCode
     * @param ext
     */
    public static void add(String extCode, Ext ext) {
        if (extCode == null) {
            throw new UnsupportedOperationException("ext code is null");
        }
        if (EXT_MAP.containsKey(extCode)) {
            throw new UnsupportedOperationException("ext code already exists");
        }
        EXT_MAP.put(extCode, ext);
    }

    /**
     * 获取所有的 ext 编号
     *
     * @return
     */
    public static Set<String> codes() {
        return EXT_MAP.keySet();
    }

}
