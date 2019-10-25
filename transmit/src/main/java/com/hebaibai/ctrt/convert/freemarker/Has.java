package com.hebaibai.ctrt.convert.freemarker;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * freemarker扩展指令
 * 判断包含数据, 不包含时抛出异常
 */
public class Has implements TemplateDirectiveModel {

    /**
     * 数据名称
     */
    private static final String NAME_KEY = "name";

    @Override
    public void execute(
            Environment environment, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body
    ) throws TemplateException, IOException {
        if (loopVars.length != 0) {
            return;
        }
        String name = getName(params);
        if (environment == null || body == null) {
            throw new RuntimeException("@has 指令必须包含数据 " + name);
        }
        body.render(new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                String value = new String(cbuf).trim();
                if (StringUtils.isBlank(value)) {
                    throw new RuntimeException("指令: @has 必须包含数据 " + name);
                }
            }

            @Override
            public void flush() throws IOException {
            }

            @Override
            public void close() throws IOException {
            }
        });

    }

    /**
     * 获取name
     *
     * @param params
     * @return
     */
    private static String getName(Map params) {
        if (params != null) {
            Object o = params.get(NAME_KEY);
            if (o != null) {
                return o.toString();
            }
        }
        return "";
    }
}
