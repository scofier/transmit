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
        if (environment == null || body == null) {
            throw new RuntimeException("@has 指令必须包含数据");
        }
        Writer out = environment.getOut();
        if (out == null) {
            throw new RuntimeException("@has 指令必须包含数据");
        }
        body.render(new Writer(out) {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                String name = null;
                if (params != null) {
                    Object o = params.get(NAME_KEY);
                    if (o != null) {
                        name = o.toString();
                    }
                }

                String value = new String(cbuf);
                if (StringUtils.isBlank(value)) {
                    throw new RuntimeException("指令: @has 必须包含数据 " + name);
                }
                out.write(value);
            }

            @Override
            public void flush() throws IOException {
                out.flush();
            }

            @Override
            public void close() throws IOException {
                out.close();
            }
        });

    }

    /**
     * 根据正则查找
     *
     * @param string
     * @param pattern
     * @param group
     * @return
     */
    private static String getByPattern(String string, String pattern, int group) {
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(string);
        while (matcher.find()) {
            return matcher.group(group);
        }
        return "";
    }
}
