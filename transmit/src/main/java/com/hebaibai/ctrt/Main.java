package com.hebaibai.ctrt;


import com.hebaibai.ctrt.transmit.ext.ApiInfoExt;
import com.hebaibai.ctrt.transmit.util.ext.Exts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;


/**
 * 程序入口
 *
 * @author hjx
 */
@Slf4j
public class Main {

    static {
        //注册插件
        Exts.add("API_INFO", ApiInfoExt.class, "现有接口配置");
    }

    /**
     * 启动入口
     * 需要传入 -c 配置文件路径 参数
     * 例如 -c /home/config.json
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        //获取配置文件内容
        String configFilePath = getConfigFilePath(args);
        //启动
        CtrtLancher ctrtLancher = new CtrtLancher();
        ctrtLancher.start(configFilePath);
    }


    /**
     * 获取配置文件
     *
     * @param args
     * @return
     * @throws ParseException
     */
    private static String getConfigFilePath(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("c", "conf", true, "config file path");
        CommandLine parse = new DefaultParser().parse(options, args);
        if (!parse.hasOption("conf")) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp("Options", options);
            System.exit(0);
        }
        String configFilePath = parse.getOptionValue("conf");
        return configFilePath;
    }

}
