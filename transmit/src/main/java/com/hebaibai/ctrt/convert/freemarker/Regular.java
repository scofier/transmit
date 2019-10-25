package com.hebaibai.ctrt.convert.freemarker;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * freemarker扩展指令
 * 通过正则提取数据
 */
public class Regular implements TemplateDirectiveModel {

    /**
     * 正则
     */
    private static final String PATTERN_KEY = "pattern";

    /**
     * 组别, 默认是1
     */
    private static final String GROUP_KEY = "group";

    @Override
    public void execute(
            Environment environment, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body
    ) throws TemplateException, IOException {
        if (loopVars.length != 0 || body == null) {
            return;
        }

        if (params == null) {
            throw new RuntimeException("指令: @regular 缺少 pattern");
        }

        Object pattern = params.get(PATTERN_KEY);
        if (pattern == null) {
            throw new RuntimeException("指令: @regular pattern 不能为 null");
        }

        String patternStr = pattern.toString();
        Writer out = environment.getOut();
        body.render(new Writer(out) {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                int groupInt = 1;
                Object group = params.get(GROUP_KEY);
                if (group != null) {
                    groupInt = Integer.parseInt(group.toString());
                }
                String byPattern = getByPattern(new String(cbuf), patternStr, groupInt);
                if (byPattern != null) {
                    byPattern = byPattern.trim();
                }
                out.write(byPattern);
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
