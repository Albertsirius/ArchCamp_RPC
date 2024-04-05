package org.galaxy.siriusrpc.core.api;

import org.galaxy.siriusrpc.core.meta.InstanceMeta;
import org.galaxy.siriusrpc.core.meta.ServiceMeta;
import org.galaxy.siriusrpc.core.registry.ChangedListener;

import java.util.List;

/**
 * @author AlbertSirius
 * @since 2024/3/31
 */
public interface RegistryCenter {
    void start();

    void stop();

    void register(ServiceMeta service, InstanceMeta instance);

    void unregister(ServiceMeta service, InstanceMeta instance);

    List<InstanceMeta> fetchAll(ServiceMeta service);

    void subscribe(ServiceMeta service, ChangedListener listener);

    class StaticRegistryCenter implements RegistryCenter {

        List<InstanceMeta> providers;

        public StaticRegistryCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public void unregister(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(ServiceMeta service) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ChangedListener listener) {

        }
    }
}
