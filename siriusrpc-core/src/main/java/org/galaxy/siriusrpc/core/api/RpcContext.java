package org.galaxy.siriusrpc.core.api;

import lombok.Data;
import org.galaxy.siriusrpc.core.meta.InstanceMeta;

import java.util.List;

/**
 * @author AlbertSirius
 * @since 2024/3/31
 */

@Data
public class RpcContext {
    private Router<InstanceMeta> router;
    private LoadBalancer<InstanceMeta> loadBalancer;
    private List<Filter> filters;
}
