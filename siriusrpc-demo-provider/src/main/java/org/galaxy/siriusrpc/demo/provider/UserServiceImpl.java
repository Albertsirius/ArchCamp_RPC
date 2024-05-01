package org.galaxy.siriusrpc.demo.provider;

import org.galaxy.siriusrpc.core.annotation.SiriusProvider;
import org.galaxy.siriusrpc.demo.api.User;
import org.galaxy.siriusrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author AlbertSirius
 * @since 2024/3/10
 */
@Component
@SiriusProvider
public class UserServiceImpl implements UserService {

    @Autowired
    Environment environment;
    @Override
    public User findById(int id) {
        return new User(id, "Sirius-" + environment.getProperty("server.port") + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, "Sirius-" + name + System.currentTimeMillis());
    }

    @Override
    public long getId(int id) {
        return id;
    }

    @Override
    public long getId(User user) {
        return user.getId().longValue();
    }

    @Override
    public String getName() {
        return "Sirius";
    }

    @Override
    public String getName(int id) {
        return "Sirius-" + id;
    }

    @Override
    public int[] getIds() {
        return new int[] {1,2,3};
    }

    @Override
    public long[] getLongIds() {
        return new long[]{100,200,300};
    }

    @Override
    public int[] getIds(int[] ids) {
        return ids;
    }

    @Override
    public User find(int timeout) {
        String port = environment.getProperty("server.port");
        if ("8081".equals(port)) {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return new User(1001, "Sirius001-" + port);
    }
}
