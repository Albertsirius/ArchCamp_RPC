package org.galaxy.siriusrpc.core.api;

import java.util.List;

/**
 * @author AlbertSirius
 * @since 2024/3/28
 */
public interface Router<T> {
    List<T> rout(List<T> providers);

    Router Default = p -> p;
}
