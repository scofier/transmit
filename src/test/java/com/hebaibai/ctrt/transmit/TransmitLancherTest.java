package com.hebaibai.ctrt.transmit;

import com.hebaibai.ctrt.CtrtLancher;
import com.hebaibai.ctrt.transmit.router.XmlToJsonTransmitRouterImpl;
import com.hebaibai.ctrt.transmit.router.XmlToXmlTransmitRouterImpl;
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

        config.put(new XmlToXmlTransmitRouterImpl() {{
            reqPath("/xmlToxml");
            reqFile(new File("/home/hjx/work/myProject/transmit/file/req-xml.ftl"));

            relayPath("http://127.0.0.1:8891/test/xml");
            relayFile(new File("/home/hjx/work/myProject/transmit/file/relay-xml.ftl"));
        }});

        config.put(new XmlToJsonTransmitRouterImpl() {{
            reqPath("/xmlToJosn");
            reqFile(new File("/home/hjx/work/myProject/transmit/file/req-xml.ftl"));

            relayPath("http://127.0.0.1:8891/test/json");
            relayFile(new File("/home/hjx/work/myProject/transmit/file/relay-json.ftl"));
        }});

        ctrtLancher.start(config);
    }
}