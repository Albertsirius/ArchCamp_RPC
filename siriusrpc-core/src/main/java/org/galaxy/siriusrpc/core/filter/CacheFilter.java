package org.galaxy.siriusrpc.core.filter;

import org.galaxy.siriusrpc.core.api.Filter;
import org.galaxy.siriusrpc.core.api.RpcRequest;
import org.galaxy.siriusrpc.core.api.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author AlbertSirius
 * @since 2024/4/14
 */

public class CacheFilter implements Filter {

    static Map<String, Object> cache = new ConcurrentHashMap();

    @Override
    public Object preFilter(RpcRequest request) {
        return cache.get(request.toString());
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        cache.putIfAbsent(request.toString(), result);
        return result;
    }
}
