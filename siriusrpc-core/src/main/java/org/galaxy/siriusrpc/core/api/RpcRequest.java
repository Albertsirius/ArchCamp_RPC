package org.galaxy.siriusrpc.core.api;

import lombok.Data;

/**
 * @author AlbertSirius
 * @since 2024/3/10
 */
@Data
public class RpcRequest {

    private String service;
    private String method;
    private Object[] args;
}
