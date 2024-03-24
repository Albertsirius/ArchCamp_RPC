package org.galaxy.siriusrpc.demo.api;

/**
 * @author AlbertSirius
 * @since 2024/3/10
 */
public interface UserService {
    User findById(int id);

    User findById(int id, String name);
    int getId(int id);
    String getName();

    String getName(int id);
}
