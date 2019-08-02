package com.hebaibai.ctrt.transmit;

import com.hebaibai.ctrt.CtrtLancher;
import com.hebaibai.ctrt.transmit.util.CrtrUtils;
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
            setCode("text-post");
            //路由地址
            setReqPath("/post");
            //路由地址请求方式
            setReqMethod(HttpMethod.POST);
            //入参类型 表单提交
            setReqType(DataType.FROM);
            //出参类型 json
            setResType(DataType.JSON);
            //=========================================
            //转发接口地址
            setApiPath("http://127.0.0.1:8080/post");
            //转发接口请求方式 POST
            setApiMethod(HttpMethod.POST);
            //转发接口请求参数类型 表单
            setApiReqType(DataType.FROM);
            //转发接口响应参数类型 JSON
            setApiResType(DataType.JSON);
            //转发接口请求参数转换模板
            setApiReqFtlText(CrtrUtils.getFileText("/home/hjx/work/myProject/transmit/file/post-req.ftl"));
            //转发接口响应参数转换模板
            setApiResFtlText(CrtrUtils.getFileText("/home/hjx/work/myProject/transmit/file/post-res.ftl"));
        }});
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
            setReqPath("/params");
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