package com.hebaibai.ctrt;

import com.alibaba.fastjson.JSONObject;
import com.hebaibai.ctrt.transmit.Config;
import com.hebaibai.ctrt.transmit.util.CrtrUtils;
import org.apache.commons.cli.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        Config config = new Config();
        try {
            JSONObject jsonObject = JSONObject.parseObject(getConf(args));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }


    /**
     * 获取配置文件
     *
     * @param args
     * @return
     * @throws ParseException
     */
    private static String getConf(String[] args) throws ParseException, IOException {
        Options options = new Options();
        options.addOption("c", "conf", true, "config file path");
        CommandLine parse = new BasicParser().parse(options, args);
        if (!parse.hasOption("conf")) {
            HelpFormatter hf = new HelpFormatter();
            hf.printHelp("Options", options);
            System.exit(0);
        }
        String conf = parse.getOptionValue("conf");
        return CrtrUtils.getFileText(conf);
    }
}
