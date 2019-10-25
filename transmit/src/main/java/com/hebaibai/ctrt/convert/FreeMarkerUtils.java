package com.hebaibai.ctrt.convert;

import com.hebaibai.ctrt.transmit.util.CrtrUtils;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveModel;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

/**
 * Freemarker 工具
 *
 * @author hjx
 */
public class FreeMarkerUtils {

    private static Configuration configuration;

    private static StringTemplateLoader loader;

    static {
        //实例化Freemarker的配置类
        configuration = new Configuration();
        configuration.setObjectWrapper(new DefaultObjectWrapper());
        configuration.setLocale(Locale.CHINA);
        configuration.setDefaultEncoding("utf-8");
        //处理空值为空字符串
        configuration.setClassicCompatible(true);
        //加载自定义指令
        for (Map.Entry<String, TemplateDirectiveModel> entry : CrtrUtils.FREEMARKER_DIRECTIVE_MODEL.entrySet()) {
            configuration.setSharedVariable(entry.getKey(), entry.getValue());
        }
        //模板加载器
        loader = new StringTemplateLoader();
    }

    public static String format(Object subjectParams, FreeMarkerFtl ftl) throws Exception {
        String name = ftl.getTemplateName();
        String templateText = ftl.getTemplateText();
        loader.putTemplate(name, templateText);
        configuration.setTemplateLoader(loader);
        Template template = configuration.getTemplate(name);
        Writer out = new StringWriter(templateText.length());
        template.process(subjectParams, out);
        String xml = out.toString();
        return xml;
    }
}
