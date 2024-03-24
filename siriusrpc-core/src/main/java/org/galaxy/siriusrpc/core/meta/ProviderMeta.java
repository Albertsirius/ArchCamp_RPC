package org.galaxy.siriusrpc.core.meta;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author AlbertSirius
 * @since 2024/3/23
 */

@Data
public class ProviderMeta {
    Method method;
    String methodSign;
    Object servieImpl;
}
