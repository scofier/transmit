package com.hebaibai.ctrt.transmit.ext;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ApiInfoExtTest {

    @Test
    public void getConfigInfo() throws IOException {
        ApiInfoExt ext = new ApiInfoExt();
        ext.setGroup("group");
        JSONObject configInfo = ext.getConfigInfo("/home/hjx/work/myprogect/transmit/config.json");
        System.out.println(configInfo);
    }
}