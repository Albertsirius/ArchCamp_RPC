package org.galaxy.siriusrpc.core.registry;

/**
 * @author AlbertSirius
 * @since 2024/4/5
 */
public interface ChangedListener {
    void fire(Event event);
}
