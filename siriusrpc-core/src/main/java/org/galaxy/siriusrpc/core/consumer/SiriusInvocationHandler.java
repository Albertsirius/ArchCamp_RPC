package org.galaxy.siriusrpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.galaxy.siriusrpc.core.api.LoadBalancer;
import org.galaxy.siriusrpc.core.api.Router;
import org.galaxy.siriusrpc.core.api.RpcContext;
import org.galaxy.siriusrpc.core.api.RpcRequest;
import org.galaxy.siriusrpc.core.api.RpcResponse;
import org.galaxy.siriusrpc.core.util.MethodUtils;
import org.galaxy.siriusrpc.core.util.TypeUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author AlbertSirius
 * @since 2024/3/17
 */
public class SiriusInvocationHandler implements InvocationHandler {

    Class<?> service;
    RpcContext context;
    List<String> providers;

    final static MediaType JSONTYPE =  MediaType.get("application/json; charset=utf-8");

    public SiriusInvocationHandler(Class<?> clazz, RpcContext context, List<String> providers) {
        this.service = clazz;
        this.context = context;
        this.providers = providers;
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

        List<String> urls = context.getRouter().rout(providers);
        String url = (String) context.getLoadBalancer().choose(urls);
        RpcResponse rpcResponse = post(rpcRequest, url);
        if (rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            Class<?> type = method.getReturnType();
            if (data instanceof JSONObject jsonResult) {
                if (Map.class.isAssignableFrom(type)) {
                    Map resultMap = new HashMap();
                    Type genericReturnType = method.getGenericReturnType();
                    if (genericReturnType instanceof ParameterizedType parameterizedType) {
                        Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                        Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[1];
                        jsonResult.entrySet().stream().forEach( e -> {
                            Object key = TypeUtils.cast(e.getKey(), keyType);
                            Object value = TypeUtils.cast(e.getValue(), valueType);
                            resultMap.put(key, value);
                        });
                    }
                    return resultMap;
                }
                return jsonResult.toJavaObject(type);
            } else if (data instanceof JSONArray jsonArray){
                Object[] array = jsonArray.toArray();
                if (type.isArray()) {
                    Class<?> componentType = method.getReturnType().getComponentType();
                    Object returnArray = Array.newInstance(componentType, array.length);
                    for (int i = 0; i < array.length; i++) {
                        Array.set(returnArray, i, array[i]);
                    }
                    return returnArray;
                } else if (List.class.isAssignableFrom(type)) {
                    List<Object> resultList = new ArrayList<>(array.length);
                    Type genericeReturnType = method.getGenericReturnType();
                    if (genericeReturnType instanceof ParameterizedType parameterizedType) {
                        Type actualType = parameterizedType.getActualTypeArguments()[0];
                        Arrays.stream(array).forEach(o -> resultList.add(TypeUtils.cast(o, (Class<?>) actualType)));
                    } else {
                        resultList.addAll(Arrays.asList(array));
                    }
                    return resultList;
                } else {
                    return null;
                }
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

    private RpcResponse post(RpcRequest rpcRequest, String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
        Request request = new Request.Builder()
                .url(url)
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
