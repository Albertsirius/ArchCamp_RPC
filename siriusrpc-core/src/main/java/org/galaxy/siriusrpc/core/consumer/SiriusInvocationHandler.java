package org.galaxy.siriusrpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.galaxy.siriusrpc.core.api.RpcRequest;
import org.galaxy.siriusrpc.core.api.RpcResponse;
import org.galaxy.siriusrpc.core.util.MethodUtils;
import org.galaxy.siriusrpc.core.util.TypeUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;


/**
 * @author AlbertSirius
 * @since 2024/3/17
 */
public class SiriusInvocationHandler implements InvocationHandler {

    Class<?> service;

    final static MediaType JSONTYPE =  MediaType.get("application/json; charset=utf-8");

    public SiriusInvocationHandler(Class<?> clazz) {
        this.service = clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (MethodUtils.checkLocalMethod(method.getName())) {
            return null;
        }

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        RpcResponse rpcResponse = post(rpcRequest);
        if (rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            if (data instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) data;
                return jsonObject.toJavaObject(method.getReturnType());
            } else if (data instanceof JSONArray jsonArray){
                Object[] array = jsonArray.toArray();
                Class<?> componentType = method.getReturnType().getComponentType();
                Object returnArray = Array.newInstance(componentType, array.length);
                for (int i = 0; i< array.length; i++) {
                    Array.set(returnArray, i, array[i]);
                }
                return returnArray;
            } else {
                return TypeUtils.cast(data, method.getReturnType());
            }
        }else {
            Exception exception = rpcResponse.getEx();
            //exception.printStackTrace();
            throw exception;
        }
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build();

    private RpcResponse post(RpcRequest rpcRequest) {
        String reqJson = JSON.toJSONString(rpcRequest);
        Request request = new Request.Builder()
                .url("http://localhost:8080")
                .post(RequestBody.create(reqJson, JSONTYPE))
                .build();
        try {
            String resp = client.newCall(request).execute().body().string();
            RpcResponse rpcResponse = JSON.parseObject(resp, RpcResponse.class);
            return rpcResponse;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
