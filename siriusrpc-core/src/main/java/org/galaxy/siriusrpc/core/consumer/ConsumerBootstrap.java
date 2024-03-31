package org.galaxy.siriusrpc.core.consumer;

import lombok.Data;
import org.galaxy.siriusrpc.core.annotation.SiriusConsumer;
import org.galaxy.siriusrpc.core.api.LoadBalancer;
import org.galaxy.siriusrpc.core.api.RegistryCenter;
import org.galaxy.siriusrpc.core.api.Router;
import org.galaxy.siriusrpc.core.api.RpcContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author AlbertSirius
 * @since 2024/3/17
 */

@Data
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;

    private Environment environment;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);





        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name); //SiriusrpcDemoConsumerApplication这个bean被增强，实际是子类。如果下面的getDeclaredFields方法获取不了属于父类的属性。
            List<Field> fields = findAnnotatedField(bean.getClass());
            fields.stream().forEach( f -> {
                Class<?> service = f.getType();
                String serviceName = service.getCanonicalName();
                Object consumer = stub.get(serviceName);
                if (Objects.isNull(consumer)) {
                    consumer = createFromRegistry(service, context, registryCenter);
                }
                f.setAccessible(true);
                try {
                    f.set(bean, consumer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private Object createFromRegistry(Class<?> service, RpcContext context, RegistryCenter registryCenter) {
        String serviceName = service.getCanonicalName();
        List<String> providers = registryCenter.fetchAll(serviceName);
        return createConsumer(service, context,providers);
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<String> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},
                new SiriusInvocationHandler(service, context, providers));
    }

    private List<Field> findAnnotatedField(Class<?> aClass) {
        List<Field> result = new ArrayList<>();
        while (Objects.nonNull(aClass)) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(SiriusConsumer.class)) {
                    result.add(f);
                }
            }
            aClass = aClass.getSuperclass();
        }
        return result;
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
