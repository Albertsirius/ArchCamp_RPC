package org.galaxy.siriusrpc.core.api;

import lombok.Data;

import java.util.List;

/**
 * @author AlbertSirius
 * @since 2024/3/31
 */

@Data
public class RpcContext {
    private Router router;
    private LoadBalancer loadBalancer;
    private List<Filter> filters;
}
