package org.galaxy.siriusrpc.core.api;

import java.util.List;

/**
 * @author AlbertSirius
 * @since 2024/3/28
 *
 * 基于权重，AAWR 自适应
 *
 * avg * 0.3 + last * 0.7
 *
 */
public interface LoadBalancer<T> {
    T choose(List<T> providers);

    LoadBalancer Default = p -> (p == null || p.size() == 0) ? null : p.get(0);
}
