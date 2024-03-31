package org.galaxy.siriusrpc.core.cluster;

import org.galaxy.siriusrpc.core.api.LoadBalancer;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author AlbertSirius
 * @since 2024/3/31
 */
public class RoundRobinLoadBalancer<T> implements LoadBalancer<T> {

    AtomicInteger index = new AtomicInteger(0);
    @Override
    public T choose(List<T> providers) {
        if (CollectionUtils.isEmpty(providers)) return null;
        if (providers.size() == 1) return providers.get(0);
        return providers.get((index.getAndIncrement() & 0x7fffffff) % providers.size()); //保证正数
    }
}
