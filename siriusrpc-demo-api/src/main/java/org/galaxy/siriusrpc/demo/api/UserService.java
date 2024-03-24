package org.galaxy.siriusrpc.demo.api;

/**
 * @author AlbertSirius
 * @since 2024/3/10
 */
public interface UserService {
    User findById(int id);

    User findById(int id, String name);
    long getId(int id);

    long getId(User user);
    String getName();

    String getName(int id);

    int[] getIds();

    long[] getLongIds();

    int[] getIds(int[] ids);
}
