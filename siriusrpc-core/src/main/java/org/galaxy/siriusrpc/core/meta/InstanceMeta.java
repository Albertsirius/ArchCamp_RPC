package org.galaxy.siriusrpc.core.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author AlbertSirius
 * @since 2024/4/5
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceMeta {

    private String schema;
    private String host;
    private Integer port;
    private String context;
    private boolean status; //online or offline
    private Map<String, String> parameters;

    public String toPath() {
        return String.format("%s_%d", host, port);
    }

    public InstanceMeta(String schema, String host, Integer port, String context) {
        this.schema = schema;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public static InstanceMeta httpInstance(String host, String port) {
        return new InstanceMeta("http", host, Integer.valueOf(port), "");
    }

    public String toUrl() {
        return String.format("%s://%s:%d/%s", schema, host, port, context);
    }
}
