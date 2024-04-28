package org.galaxy.siriusrpc.core.api;

import lombok.Data;

/**
 * @author AlbertSirius
 * @since 2024/4/26
 */
@Data
public class SiriusRpcException extends RuntimeException {

    private String errCode;
    public SiriusRpcException() {
    }

    public SiriusRpcException(String message) {
        super(message);
    }

    public SiriusRpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public SiriusRpcException(Throwable cause) {
        super(cause);
    }

    public SiriusRpcException(Throwable cause, String errCode) {
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
