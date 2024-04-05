package org.galaxy.siriusrpc.core.consumer;

import com.alibaba.fastjson.JSON;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.galaxy.siriusrpc.core.api.RpcRequest;
import org.galaxy.siriusrpc.core.api.RpcResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author AlbertSirius
 * @since 2024/4/5
 */
public class OkhttpInvoker implements HttpInvoker {

    final static MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

    private OkHttpClient client;

    public OkhttpInvoker() {
        this.client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .connectTimeout(1, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public RpcResponse<?> post(RpcRequest rpcRequest, String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
        System.out.println(" ===> reqJson = " + reqJson);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson, JSONTYPE))
                .build();
        try {
            String resp = client.newCall(request).execute().body().string();
            System.out.println(" ===> respJson = " + resp);
            return JSON.parseObject(resp, RpcResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
