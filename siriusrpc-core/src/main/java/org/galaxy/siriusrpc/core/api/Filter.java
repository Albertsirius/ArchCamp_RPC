package org.galaxy.siriusrpc.core.api;

/**
 * @author AlbertSirius
 * @since 2024/3/28
 */
public interface Filter {

    Object preFilter(RpcRequest request);

    Object postFilter(RpcRequest request, RpcResponse response, Object result);

    Filter Default = new Filter() {
        @Override
        public RpcResponse preFilter(RpcRequest request) {
            return null;
        }

        @Override
        public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
            return null;
        }
    };
}
