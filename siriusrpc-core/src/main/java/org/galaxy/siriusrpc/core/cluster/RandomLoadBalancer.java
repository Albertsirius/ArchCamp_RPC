package org.galaxy.siriusrpc.core.cluster;

import org.galaxy.siriusrpc.core.api.LoadBalancer;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author AlbertSirius
 * @since 2024/3/31
 */
public class RandomLoadBalancer<T> implements LoadBalancer<T> {

    Random random = new Random();
    @Override
    public T choose(List<T> providers) {
        if (CollectionUtils.isEmpty(providers)) return null;
        if (providers.size() == 1) return providers.get(0);
        return providers.get(random.nextInt(providers.size()));
    }
}
