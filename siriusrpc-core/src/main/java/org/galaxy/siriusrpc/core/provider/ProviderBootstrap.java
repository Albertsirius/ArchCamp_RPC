package org.galaxy.siriusrpc.core.provider;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import org.galaxy.siriusrpc.core.annotation.SiriusProvider;
import org.galaxy.siriusrpc.core.api.RegistryCenter;
import org.galaxy.siriusrpc.core.meta.InstanceMeta;
import org.galaxy.siriusrpc.core.meta.ProviderMeta;
import org.galaxy.siriusrpc.core.meta.ServiceMeta;
import org.galaxy.siriusrpc.core.util.MethodUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Map;

/**
 * @author AlbertSirius
 * @since 2024/3/10
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {

    ApplicationContext applicationContext;

    private MultiValueMap<String, ProviderMeta> skeletion = new LinkedMultiValueMap<>();

    private InstanceMeta instance;
    @Value("${server.port}")
    private String port;

    @Value("${app.id}")
    private String app;
    @Value("${app.namespace}")
    private String namespace;
    @Value("${app.env}")
    private String env;

    private RegistryCenter registryCenter;

    @SneakyThrows
    @PostConstruct
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(SiriusProvider.class);

        registryCenter = applicationContext.getBean(RegistryCenter.class);

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

    private void registerService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app).namespace(namespace).env(env).name(service).build();
        registryCenter.register(serviceMeta, instance);
    }

    @SneakyThrows
    public void start() {
        instance = InstanceMeta.httpInstance(InetAddress.getLocalHost().getHostAddress() , port);
        registryCenter.start();
        skeletion.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        System.out.println(" ===> unreg all services.");
        skeletion.keySet().forEach(this::unregisterService);
        registryCenter.stop();
    }

    private void unregisterService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app).namespace(namespace).env(env).name(service).build();
        registryCenter.unregister(serviceMeta, instance);
    }
}
