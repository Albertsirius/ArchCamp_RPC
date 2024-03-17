package org.galaxy.siriusrpc.core.provider;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.galaxy.siriusrpc.core.annotation.SiriusProvider;
import org.galaxy.siriusrpc.core.api.RpcRequest;
import org.galaxy.siriusrpc.core.api.RpcResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author AlbertSirius
 * @since 2024/3/10
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {

    ApplicationContext applicationContext;

    private Map<String, Object> skeletion = new HashMap<>();

    public RpcResponse invoke(RpcRequest request) {
        Object bean = skeletion.get(request.getService());
        try {
            Method method = findMethod(bean.getClass(), request.getMethod());
            Object result = method.invoke(bean, request.getArgs());
            return new RpcResponse(true, result);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method findMethod(Class<?> aClass, String methodName) {
        Optional<Method> optional = Arrays.stream(aClass.getMethods()).filter(m -> m.getName().equals(methodName))
                .findFirst();
        return optional.orElse(null);
    }


    @PostConstruct
    public void buildProvider() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(SiriusProvider.class);
        providers.forEach((x, y) -> System.out.println(x));
        providers.values().forEach( x -> {
            Class<?> itfer = x.getClass().getInterfaces()[0];
            skeletion.put(itfer.getCanonicalName(), x);
        });
    }
}
