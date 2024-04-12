package org.galaxy.siriusrpc.demo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.galaxy.siriusrpc.core.annotation.SiriusConsumer;
import org.galaxy.siriusrpc.demo.api.User;
import org.galaxy.siriusrpc.demo.api.UserService;
import org.springframework.stereotype.Component;

/**
 * @author AlbertSirius
 * @since 2024/3/21
 */
@Component
@Slf4j
public class Demo2 {
    @SiriusConsumer
    UserService userService2;

    public void test() {
        User user = userService2.findById(100);
        log.info(user.toString());
    }
}
