package org.galaxy.siriusrpc.demo.provider;

import org.galaxy.siriusrpc.core.annotation.SiriusProvider;
import org.galaxy.siriusrpc.demo.api.User;
import org.galaxy.siriusrpc.demo.api.UserService;
import org.springframework.stereotype.Component;

/**
 * @author AlbertSirius
 * @since 2024/3/10
 */
@Component
@SiriusProvider
public class UserServiceImpl implements UserService {
    @Override
    public User findById(int id) {
        return new User(id, "Sirius-" + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, "Sirius-" + name + System.currentTimeMillis());
    }

    @Override
    public int getId(int id) {
        return id;
    }

    @Override
    public String getName() {
        return "Sirius";
    }

    @Override
    public String getName(int id) {
        return "Sirius-" + id;
    }
}
