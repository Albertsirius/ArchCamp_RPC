package org.galaxy.siriusrpc.demo.api;

/**
 * @author AlbertSirius
 * @since 2024/3/10
 */
public interface UserService {
    User findById(Integer id);

    int getId(int id);
}
