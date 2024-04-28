package org.galaxy.siriusrpc.core.api;

import lombok.Data;

/**
 * @author AlbertSirius
 * @since 2024/4/26
 */
@Data
public class RpcException extends RuntimeException {

    private String errCode;
    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(Throwable cause, String errCode) {
        super(cause);
        this.errCode = errCode;
    }

    // X => tech exception
    // Y => business exception
    // Z => unknown
    public static final String SocketTimeoutEx = "X001" + "-" + "http_invoke_timeout";
    public static final String NoSuchMethodEx = "X002" + "-" + "method_not_exists";
    public static final String UnknownEx = "Z001" + "-" + "unknown";

}
