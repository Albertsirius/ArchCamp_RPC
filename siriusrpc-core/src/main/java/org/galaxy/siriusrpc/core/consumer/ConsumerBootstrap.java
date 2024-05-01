package org.galaxy.siriusrpc.core.consumer;

import lombok.Data;
import org.galaxy.siriusrpc.core.annotation.SiriusConsumer;
import org.galaxy.siriusrpc.core.api.Filter;
import org.galaxy.siriusrpc.core.api.LoadBalancer;
import org.galaxy.siriusrpc.core.api.RegistryCenter;
import org.galaxy.siriusrpc.core.api.Router;
import org.galaxy.siriusrpc.core.api.RpcContext;
import org.galaxy.siriusrpc.core.meta.InstanceMeta;
import org.galaxy.siriusrpc.core.meta.ServiceMeta;
import org.galaxy.siriusrpc.core.registry.ChangedListener;
import org.galaxy.siriusrpc.core.registry.Event;
import org.galaxy.siriusrpc.core.util.MethodUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
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

    @Value("${app.id}")
    private String app;
    @Value("${app.namespace}")
    private String namespace;
    @Value("${app.env}")
    private String env;
    @Value("${app.retries")
    private int retries;
    @Value("${app.timeout")
    private int timeout;

    public void start() {
        Router<InstanceMeta> router = applicationContext.getBean(Router.class);
        LoadBalancer<InstanceMeta> loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        List<Filter> filters = applicationContext.getBeansOfType(Filter.class).values().stream().toList();

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);
        context.setFilters(filters);
        context.getParameters().put("app.retires", String.valueOf(retries));

        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name); //SiriusrpcDemoConsumerApplication这个bean被增强，实际是子类。如果下面的getDeclaredFields方法获取不了属于父类的属性。
            List<Field> fields = MethodUtils.findAnnotatedField(bean.getClass(), SiriusConsumer.class);
            fields.stream().forEach( f -> {
                Class<?> service = f.getType();
                String serviceName = service.getCanonicalName();
                Object consumer = stub.get(serviceName);
                if (Objects.isNull(consumer)) {
                    consumer = createFromRegistry(service, context, registryCenter);
                    stub.put(serviceName, consumer);
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
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app).namespace(namespace).env(env).name(service.getCanonicalName()).
                build();
        List<InstanceMeta> providers = registryCenter.fetchAll(serviceMeta);
        registryCenter.subscribe(serviceMeta, new ChangedListener() {
            @Override
            public void fire(Event event) {
                providers.clear();
                providers.addAll(event.getData());
            }
        });
        return createConsumer(service, context,providers);
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},
                new SiriusInvocationHandler(service, context, providers));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
