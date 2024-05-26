package org.galaxy.siriusrpc.core.consumer;

import lombok.extern.slf4j.Slf4j;
import org.galaxy.siriusrpc.core.api.Filter;
import org.galaxy.siriusrpc.core.api.RpcContext;
import org.galaxy.siriusrpc.core.api.RpcRequest;
import org.galaxy.siriusrpc.core.api.RpcResponse;
import org.galaxy.siriusrpc.core.api.RpcException;
import org.galaxy.siriusrpc.core.consumer.http.HttpInvoker;
import org.galaxy.siriusrpc.core.consumer.http.OkhttpInvoker;
import org.galaxy.siriusrpc.core.governace.SlidingTimeWindow;
import org.galaxy.siriusrpc.core.meta.InstanceMeta;
import org.galaxy.siriusrpc.core.util.MethodUtils;
import org.galaxy.siriusrpc.core.util.TypeUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @author AlbertSirius
 * @since 2024/3/17
 */
@Slf4j
public class SiriusInvocationHandler implements InvocationHandler {
    Class<?> service;
    RpcContext context;
    final List<InstanceMeta> providers;
    final List<InstanceMeta> isolatedProviders = new ArrayList<>();
    final List<InstanceMeta> halfOpenProviders = new ArrayList<>();
    HttpInvoker httpInvoker;
    Map<String, SlidingTimeWindow> windows = new HashMap<>();

    ScheduledExecutorService executor;


    public SiriusInvocationHandler(Class<?> clazz, RpcContext context, List<InstanceMeta> providers) {
        this.service = clazz;
        this.context = context;
        this.providers = providers;
        int timeout = Integer.parseInt(context.getParameters().getOrDefault("app.timeout", "1000"));
        this.httpInvoker = new OkhttpInvoker(timeout);
        this.executor = Executors.newScheduledThreadPool(1);
        this.executor.scheduleWithFixedDelay(this::halfOpen, 10, 60, TimeUnit.SECONDS);
    }

    private void halfOpen() {
        log.debug(" ===> half open isolatedProviders: " + isolatedProviders);
        halfOpenProviders.clear();
        halfOpenProviders.addAll(isolatedProviders);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {

        if (MethodUtils.checkLocalMethod(method.getName())) {
            return null;
        }

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        int retries = Integer.parseInt(context.getParameters().getOrDefault("app.retries", "1"));
        while (retries -- > 0) {
            log.info(" ===> retries: " + retries);
            try {
                for (Filter filter : this.context.getFilters()) {
                    Object preResult = filter.preFilter(rpcRequest);
                    if (Objects.nonNull(preResult)) {
                        log.info(filter.getClass().getName() + " ===> preFilter: " + preResult);
                        return preResult;
                    }
                }

                InstanceMeta instance;
                synchronized (halfOpenProviders) {
                    if (halfOpenProviders.isEmpty()) {
                        List<InstanceMeta> instances = context.getRouter().rout(providers);
                        instance = context.getLoadBalancer().choose(instances);
                        log.debug("loadBalancer.choose(urls) ===> " + instance.toUrl());
                    } else {
                        instance = halfOpenProviders.remove(0);
                        log.debug(" check alive instance ===> {}", instance);
                    }
                }

                RpcResponse<?> rpcResponse;
                Object result;
                String url = instance.toUrl();
                try {
                    rpcResponse = httpInvoker.post(rpcRequest, url);
                    result = castReturnResult(method, rpcResponse);
                } catch (Exception e) {
                    //故障规则统计和隔离
                    SlidingTimeWindow window = windows.get(url);
                    if (Objects.isNull(url)) {
                        window = new SlidingTimeWindow();
                        windows.put(url, window);
                    }
                    window.record(System.currentTimeMillis());
                    log.debug("instance {} in window with {}", url, window.getSum());
                    // 发生10次， 就做故障隔离
                    if (window.getSum() >= 10) {
                        isolate(instance);
                    }
                    throw e;
                }
                synchronized (providers) {
                    if (!providers.contains(instance)) {
                        isolatedProviders.remove(instance);
                        providers.add(instance);
                        log.debug("instance {} is recovered, isolatedProviders={}, providers={}", instance, isolatedProviders);
                    }
                }

                for (Filter filter : this.context.getFilters()) {
                    Object filterResult = filter.postFilter(rpcRequest, rpcResponse, result);
                    if (Objects.nonNull(filterResult)) {
                        return filterResult;
                    }
                }
                return result;
            } catch (Exception ex) {
                if (!(ex.getCause()  instanceof SocketTimeoutException)) {
                    throw ex;
                }
            }
        }
        return null;
    }

    private void isolate(InstanceMeta instance) {
        log.debug(" ===> isolate instance: " + instance);
        providers.remove(instance);
        log.debug(" ===> providers = {}", providers);
        isolatedProviders.add(instance);
        log.debug(" ===> isolated providers = {}", providers);

    }

    @Nullable
    private static Object castReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if (rpcResponse.isStatus()) {
            return TypeUtils.castMethodResult(method, rpcResponse.getData());
        } else {
            Exception exception = rpcResponse.getEx();
            if (exception instanceof RpcException ex) {
                throw ex;
            } else {
                throw new RpcException(rpcResponse.getEx(), RpcException.NoSuchMethodEx);
            }
        }
    }
}
