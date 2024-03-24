package org.galaxy.siriusrpc.core.provider;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.galaxy.siriusrpc.core.annotation.SiriusProvider;
import org.galaxy.siriusrpc.core.api.RpcRequest;
import org.galaxy.siriusrpc.core.api.RpcResponse;
import org.galaxy.siriusrpc.core.meta.ProviderMeta;
import org.galaxy.siriusrpc.core.util.MethodUtils;
import org.galaxy.siriusrpc.core.util.TypeUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author AlbertSirius
 * @since 2024/3/10
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {

    ApplicationContext applicationContext;

    private MultiValueMap<String, ProviderMeta> skeletion = new LinkedMultiValueMap<>();

    public RpcResponse invoke(RpcRequest request) {
        String methodSign = request.getMethodSign();
        List<ProviderMeta> providerMetas = skeletion.get(request.getService());
        RpcResponse rpcResponse = new RpcResponse();
        try {
            ProviderMeta providerMeta = findProviderMeta(providerMetas, methodSign);
            Method method = providerMeta.getMethod();
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes());
            Object result = method.invoke(providerMeta.getServieImpl(), args);
            rpcResponse.setStatus(true);
            rpcResponse.setData(result);
        } catch (InvocationTargetException e) {
            rpcResponse.setStatus(false);
            rpcResponse.setEx(new RuntimeException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            rpcResponse.setStatus(false);
            rpcResponse.setEx(new RuntimeException(e.getMessage()));
        } finally {
            return rpcResponse;
        }
    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
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

    @PostConstruct
    public void buildProvider() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(SiriusProvider.class);
        providers.forEach((x, y) -> System.out.println(x));
        for (Object object : providers.values()) {
            Class<?>[] itfers = object.getClass().getInterfaces();
            for (Class<?> itfer : itfers) {
                Method[] methods = itfer.getMethods();
                for (Method method : methods) {
                    if (MethodUtils.checkLocalMethod(method)) {
                        continue;
                    }
                    createProvider(itfer, object, method);
                }
            }
        }
    }

    private void createProvider(Class<?> itfer, Object object, Method method) {
        ProviderMeta meta = new ProviderMeta();
        meta.setMethod(method);
        meta.setServieImpl(object);
        meta.setMethodSign(MethodUtils.methodSign(method));
        System.out.println(" Create a provider: " + meta);
        skeletion.add(itfer.getCanonicalName(), meta);
    }
}
