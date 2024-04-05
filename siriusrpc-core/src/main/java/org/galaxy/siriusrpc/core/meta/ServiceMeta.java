package org.galaxy.siriusrpc.core.meta;

import lombok.Builder;
import lombok.Data;

/**
 * @author AlbertSirius
 * @since 2024/4/5
 */

@Data
@Builder
public class ServiceMeta {

    private String app;
    private String namespace;
    private String env;
    private String name;

    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace, env, name);
    }
}
