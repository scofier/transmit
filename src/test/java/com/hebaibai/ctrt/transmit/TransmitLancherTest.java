package com.hebaibai.ctrt.transmit;

import com.hebaibai.ctrt.CtrtLancher;
import io.vertx.core.http.HttpMethod;
import org.junit.Test;

import java.io.File;
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
        config.put(new TransmitConfig() {{
            setReqPath("/json");
            setReqMethod(HttpMethod.POST);
            setReqType(DataType.JSON);
        }});
        config.put(new TransmitConfig() {{
            setReqPath("/xml");
            setReqMethod(HttpMethod.POST);
            setReqType(DataType.JSON);
        }});
        config.put(new TransmitConfig() {{
            setReqPath("/post");
            setReqMethod(HttpMethod.POST);
            setReqType(DataType.FROM);
            setResType(DataType.JSON);

            setApiPath("http://127.0.0.1:8080/post");
            setApiMethod(HttpMethod.POST);
            setApiReqType(DataType.FROM);
            setApiResType(DataType.JSON);
            setApiReqFtl(new File("/home/hjx/work/myProject/transmit/file/post-req.ftl"));
            setApiResFtl(new File("/home/hjx/work/myProject/transmit/file/post-res.ftl"));
        }});
        config.put(new TransmitConfig() {{
            setReqPath("/get");
            setReqMethod(HttpMethod.GET);
            setReqType(DataType.QUERY);
        }});
        config.put(new TransmitConfig() {{
            setReqPath("/text");
            setReqMethod(HttpMethod.POST);
            setReqType(DataType.TEXT);
        }});

        ctrtLancher.start(config);
    }
}