package com.hebaibai.test.ext;

import io.vertx.core.Future;
import okhttp3.OkHttpClient;
import org.junit.Test;

import java.util.HashMap;

public class TestExtTest {

    @Test
    public void name() {
        OkHttpUtil httpUtil = new OkHttpUtil(new OkHttpClient());

        Future<String> future = Future.future();
        String result = httpUtil.get(
                "http://www.baidu.com",
                new HashMap<>()
        );
        future.complete(result);

        System.out.println(result);

    }
}
