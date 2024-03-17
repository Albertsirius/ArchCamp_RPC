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
    public User findById(Integer id) {
        return new User(id, "Sirius-" + System.currentTimeMillis());
    }
}
