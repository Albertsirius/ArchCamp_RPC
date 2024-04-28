package org.galaxy.siriusrpc.core.consumer;

import lombok.extern.slf4j.Slf4j;
import org.galaxy.siriusrpc.core.api.Filter;
import org.galaxy.siriusrpc.core.api.RpcContext;
import org.galaxy.siriusrpc.core.api.RpcRequest;
import org.galaxy.siriusrpc.core.api.RpcResponse;
import org.galaxy.siriusrpc.core.api.SiriusRpcException;
import org.galaxy.siriusrpc.core.consumer.http.HttpInvoker;
import org.galaxy.siriusrpc.core.consumer.http.OkhttpInvoker;
import org.galaxy.siriusrpc.core.meta.InstanceMeta;
import org.galaxy.siriusrpc.core.util.MethodUtils;
import org.galaxy.siriusrpc.core.util.TypeUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;


/**
 * @author AlbertSirius
 * @since 2024/3/17
 */
@Slf4j
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

        for (Filter filter : this.context.getFilters()) {
            Object preResult = filter.preFilter(rpcRequest);
            if (Objects.nonNull(preResult)) {
                log.info(filter.getClass().getName() + " ===> preFilter: " + preResult);
                return preResult;
            }
        }

        List<InstanceMeta> instances = context.getRouter().rout(providers);
        InstanceMeta instance = context.getLoadBalancer().choose(instances);
        log.debug("loadBalancer.choose(urls) ===> " + instance.toUrl());
        RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, instance.toUrl());
        Object result = castReturnResult(method, rpcResponse);
        for (Filter filter : this.context.getFilters()) {
            Object filterResult = filter.postFilter(rpcRequest, rpcResponse, result);
            if (Objects.nonNull(filterResult)) {
                return filterResult;
            }else {
                result = filterResult;
            }
        }

        return result;
    }

    @Nullable
    private static Object castReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if (rpcResponse.isStatus()) {
            return TypeUtils.castMethodResult(method, rpcResponse.getData());
        } else {
            Exception exception = rpcResponse.getEx();
            if (exception instanceof SiriusRpcException ex) {
                throw ex;
            } else {
                throw new SiriusRpcException(rpcResponse.getEx(), SiriusRpcException.NoSuchMethodEx);
            }
        }
    }
}
