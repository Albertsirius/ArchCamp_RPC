package org.galaxy.siriusrpc.core.consumer;

import org.galaxy.siriusrpc.core.api.RpcContext;
import org.galaxy.siriusrpc.core.api.RpcRequest;
import org.galaxy.siriusrpc.core.api.RpcResponse;
import org.galaxy.siriusrpc.core.consumer.http.HttpInvoker;
import org.galaxy.siriusrpc.core.consumer.http.OkhttpInvoker;
import org.galaxy.siriusrpc.core.meta.InstanceMeta;
import org.galaxy.siriusrpc.core.util.MethodUtils;
import org.galaxy.siriusrpc.core.util.TypeUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;


/**
 * @author AlbertSirius
 * @since 2024/3/17
 */
public class SiriusInvocationHandler implements InvocationHandler {
    Class<?> service;
    RpcContext context;
    List<InstanceMeta> providers;

    HttpInvoker httpInvoker = new OkhttpInvoker();


    public SiriusInvocationHandler(Class<?> clazz, RpcContext context, List<InstanceMeta> providers) {
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

        List<InstanceMeta> instances = context.getRouter().rout(providers);
        InstanceMeta instance = context.getLoadBalancer().choose(instances);
        System.out.println("loadBalancer.choose(urls) ===> " + instance.toUrl());
        RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, instance.toUrl());
        if (rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            return TypeUtils.castMethodResult(method, data);
        } else {
            Exception exception = rpcResponse.getEx();
            throw exception;
        }
    }
}
