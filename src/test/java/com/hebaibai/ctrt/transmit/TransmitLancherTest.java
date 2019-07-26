package com.hebaibai.ctrt.transmit;

import com.hebaibai.ctrt.CtrtLancher;
import com.hebaibai.ctrt.transmit.config.Config;
import com.hebaibai.ctrt.transmit.config.DataType;
import com.hebaibai.ctrt.transmit.config.ConvertData;
import io.vertx.core.http.HttpMethod;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class TransmitLancherTest {
    @Test
    public void name() throws MalformedURLException {

        URL url = new URL("http://127.0.0.1:9090/test");
        String host = url.getHost();
        String path = url.getPath();
        System.out.println(host);
        System.out.println(path);
    }

    public static void main(String[] args) throws IOException {
        CtrtLancher ctrtLancher = new CtrtLancher();
        Config config = new Config();
        config.setPort(9090);
        config.put(
                new ConvertData() {{
                    this.setDataType(DataType.json);
                    setMethod(HttpMethod.POST);
                    setPath("/test");
                    setConvertFilePath("/home/hjx/work/myProject/transmit/file/json.ftl");
                }},
                new ConvertData() {{
                    this.setDataType(DataType.xml);
                    setMethod(HttpMethod.GET);
                    setPath("https://www.baidu.com");
                }}
        );

        ctrtLancher.start(config);
    }
}