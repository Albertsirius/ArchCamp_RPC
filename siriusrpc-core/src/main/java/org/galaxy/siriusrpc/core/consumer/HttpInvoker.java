package org.galaxy.siriusrpc.core.consumer;

import org.galaxy.siriusrpc.core.api.RpcRequest;
import org.galaxy.siriusrpc.core.api.RpcResponse;

/**
 * @author AlbertSirius
 * @since 2024/4/5
 */
public interface HttpInvoker {

    RpcResponse<?> post(RpcRequest rpcRequest, String url);
}
