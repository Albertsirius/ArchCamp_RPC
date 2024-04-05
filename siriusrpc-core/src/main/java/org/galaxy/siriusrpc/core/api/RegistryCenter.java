package org.galaxy.siriusrpc.core.api;

import org.galaxy.siriusrpc.core.meta.InstanceMeta;
import org.galaxy.siriusrpc.core.registry.ChangedListener;

import java.util.List;

/**
 * @author AlbertSirius
 * @since 2024/3/31
 */
public interface RegistryCenter {
    void start();

    void stop();

    void register(String service, InstanceMeta instance);

    void unregister(String service, InstanceMeta instance);

    List<InstanceMeta> fetchAll(String service);

    void subscribe(String service, ChangedListener listener);

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
        public void register(String service, InstanceMeta instance) {

        }

        @Override
        public void unregister(String service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(String service) {
            return providers;
        }

        @Override
        public void subscribe(String service, ChangedListener listener) {

        }
    }
}
