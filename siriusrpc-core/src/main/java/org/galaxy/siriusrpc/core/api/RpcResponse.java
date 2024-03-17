package org.galaxy.siriusrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author AlbertSirius
 * @since 2024/3/10
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> {

    boolean status;
    T data;
}
