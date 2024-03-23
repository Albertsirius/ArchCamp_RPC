package org.galaxy.siriusrpc.demo.consumer;

import org.galaxy.siriusrpc.core.annotation.SiriusConsumer;
import org.galaxy.siriusrpc.demo.api.User;
import org.galaxy.siriusrpc.demo.api.UserService;
import org.springframework.stereotype.Component;

/**
 * @author AlbertSirius
 * @since 2024/3/21
 */
@Component
public class Demo2 {
    @SiriusConsumer
    UserService userService2;

    public void test() {
        User user = userService2.findById(100);
        System.out.println(user);
    }
}
