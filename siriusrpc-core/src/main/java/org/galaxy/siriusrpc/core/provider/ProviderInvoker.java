package org.galaxy.siriusrpc.core.provider;

import org.galaxy.siriusrpc.core.api.RpcRequest;
import org.galaxy.siriusrpc.core.api.RpcResponse;
import org.galaxy.siriusrpc.core.api.RpcException;
import org.galaxy.siriusrpc.core.meta.ProviderMeta;
import org.galaxy.siriusrpc.core.util.TypeUtils;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * @author AlbertSirius
 * @since 2024/4/5
 */
public class ProviderInvoker {

    private MultiValueMap<String, ProviderMeta> skeletion;

    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.skeletion = providerBootstrap.getSkeletion();
    }

    public RpcResponse<Object> invoke(RpcRequest request) {
        String methodSign = request.getMethodSign();
        List<ProviderMeta> providerMetas = skeletion.get(request.getService());
        RpcResponse<Object> rpcResponse = new RpcResponse<>();
        try {
            ProviderMeta providerMeta = findProviderMeta(providerMetas, methodSign);
            Method method = providerMeta.getMethod();
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes());
            Object result = method.invoke(providerMeta.getServieImpl(), args);
            rpcResponse.setStatus(true);
            rpcResponse.setData(result);
        } catch (InvocationTargetException e) {
            rpcResponse.setStatus(false);
            rpcResponse.setEx(new RpcException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            rpcResponse.setStatus(false);
            rpcResponse.setEx(new RpcException(e.getMessage()));
        }
        return rpcResponse;
    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) { //TODO 这个函数有bug，请看第十课开头
        if (Objects.isNull(args) || args.length == 0) return args;
        Object[] actuals = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            actuals[i] = TypeUtils.cast(args[i], parameterTypes[i]);
        }
        return actuals;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        return providerMetas.stream().filter( x -> x.getMethodSign().equals(methodSign)).findFirst()
                .orElse(null);
    }

}
