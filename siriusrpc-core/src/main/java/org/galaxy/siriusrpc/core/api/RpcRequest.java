package org.galaxy.siriusrpc.core.api;

import lombok.Data;
import lombok.ToString;

/**
 * @author AlbertSirius
 * @since 2024/3/10
 */
@Data
@ToString
public class RpcRequest {
    private String service;
    private String methodSign; //方法签名
    private Object[] args;
}
