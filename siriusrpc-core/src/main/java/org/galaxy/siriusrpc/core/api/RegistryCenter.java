package org.galaxy.siriusrpc.core.api;

import org.galaxy.siriusrpc.core.registry.ChangedListener;

import java.util.List;

/**
 * @author AlbertSirius
 * @since 2024/3/31
 */
public interface RegistryCenter {
    void start();

    void stop();

    void register(String service, String instance);

    void unregister(String service, String instance);

    List<String> fetchAll(String service);

    void subscribe(String service, ChangedListener listener);

    class StaticRegistryCenter implements RegistryCenter {

        List<String> providers;

        public StaticRegistryCenter(List<String> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, String instance) {

        }

        @Override
        public void unregister(String service, String instance) {

        }

        @Override
        public List<String> fetchAll(String service) {
            return providers;
        }

        @Override
        public void subscribe(String service, ChangedListener listener) {

        }
    }
}
